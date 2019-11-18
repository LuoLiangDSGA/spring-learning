# Dubbo使用Nacos作为注册中心

## Nacos是什么

官方定义是：Nacos 致力于帮助您发现、配置和管理微服务。Nacos 提供了一组简单易用的特性集，帮助您快速实现动态服务发现、服务配置、服务元数据及流量管理。
Nacos是阿里搞出来的又一个开源项目，可以用于替代其他的注册中心，众所周知Eureka已经不再更新，目前Nacos已经支持和SpringBoot，Spring Cloud，Dubbo等集成，理论上可以实现不同微服务框架之间相互调用。

## 准备工作

开始之前需要提前安装Nacos，可以通过构建源码运行或者直接下载打包好的zip文件解压后执行，在开始下面的步骤之前，确保Nacos已经启动，如果没有安装Nacos，请参考[Nacos快速入门](https://nacos.io/en-us/docs/quick-start.html)。

## 快速开始

> 首先创建一个父子结构的maven工程，大致如下：

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.9.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>

    <modelVersion>4.0.0</modelVersion>

    <artifactId>dubbo-spring-boot-registry-nacos</artifactId>
    <groupId>org.boot.dubbo</groupId>
    <name>Apache Dubbo Spring Boot :: Samples : Registry Nacos</name>
    <description>Apache Dubbo Spring Boot Registry Nacos Samples</description>
    <version>${revision}</version>
    <packaging>pom</packaging>

    <modules>
        <module>provider-sample</module>
        <module>consumer-sample</module>
        <module>sample-api</module>
    </modules>

  ...
</project>
```

### 创建提供者
> 在子项目中引入必要的依赖

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <groupId>org.boot.dubbo</groupId>
        <artifactId>dubbo-spring-boot-registry-nacos</artifactId>
        <version>${revision}</version>
        <relativePath>../pom.xml</relativePath>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>dubbo-spring-boot-registry-nacos-provider-sample</artifactId>
    <name>Apache Dubbo Spring Boot :: Samples : Registry Nacos :: Provider Sample</name>

    <properties>
        <nacos.version>1.1.1</nacos.version>
    </properties>

    <dependencies>
        <!-- Spring Boot dependencies -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>

        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo-spring-boot-starter</artifactId>
            <version>${revision}</version>
        </dependency>

        <!-- Dubbo Registry Nacos -->
        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo-registry-nacos</artifactId>
            <version>${revision}</version>
        </dependency>

        <dependency>
            <groupId>com.alibaba.nacos</groupId>
            <artifactId>nacos-client</artifactId>
            <version>${nacos.version}</version>
        </dependency>

        <dependency>
            <groupId>org.boot.dubbo</groupId>
            <artifactId>dubbo-spring-boot-sample-api</artifactId>
            <version>${revision}</version>
        </dependency>
    </dependencies>

    ...
</project>
```

> 添加Dubbo外部化配置，SpringBoot会进行自动配置

这是Dubbo推荐的一种方式，也可以通过xml的形式进行配置

```
# Spring boot application
spring.application.name=dubbo-registry-nacos-provider-sample
# Base packages to scan Dubbo Component: @org.apache.dubbo.config.annotation.Service
dubbo.scan.base-packages=org.boot.dubbo.nacos.demo.provider.service

# Dubbo Application
## The default value of dubbo.application.name is ${spring.application.name}
## dubbo.application.name=${spring.application.name}
nacos.server-address = 127.0.0.1
nacos.port = 8848

# Dubbo Protocol
dubbo.protocol.name=dubbo
## Random port
dubbo.protocol.port=-1

## Dubbo Registry
dubbo.registry.address=nacos://${nacos.server-address}:${nacos.port}

## DemoService version
demo.service.version=1.0.0

```

> 编写示例接口和实现

```java
public interface DemoService {

    String sayHello(String name);

}
```
 
 
```java
 
@Service(version = "${demo.service.version}")
public class DefaultDemoService implements DemoService {
    /**
     * The default value of ${dubbo.application.name} is ${spring.application.name}
     */
    @Value("${dubbo.application.name}")
    private String serviceName;

    @Override
    public String sayHello(String name) {
        return String.format("[%s] : Hello, %s", serviceName, name);
    }
}

```
这里通过Dubbo提供的`@Service`注解暴露服务，注意和Spring提供的`@Service`注解区分开。

```java
@EnableAutoConfiguration
public class DubboRegistryNacosProviderBootstrap {

    public static void main(String[] args) {
        new SpringApplicationBuilder(DubboRegistryNacosProviderBootstrap.class)
                .run(args);
    }
}
```
 
> 这时候可以启动提供者，可以看到我们编写的服务以及注册在Nacos中

![](https://tva1.sinaimg.cn/large/006y8mN6gy1g929yhj72qj32120u0afr.jpg)

### 创建服务消费者

> 创建一个web项目用于消费我们刚才提供的服务

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <groupId>org.boot.dubbo</groupId>
        <artifactId>dubbo-spring-boot-registry-nacos</artifactId>
        <version>${revision}</version>
        <relativePath>../pom.xml</relativePath>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>dubbo-spring-boot-registry-nacos-consumer-sample</artifactId>
    <name>Apache Dubbo Spring Boot :: Samples : Registry Nacos :: Consumer Sample</name>

    <properties>
        <nacos.version>1.1.1</nacos.version>
    </properties>

    <dependencies>
        <!-- Spring Boot dependencies -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>

        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo-spring-boot-starter</artifactId>
            <version>${revision}</version>
        </dependency>

        <!-- Dubbo Registry Nacos -->
        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo-registry-nacos</artifactId>
            <version>${revision}</version>
        </dependency>

        <dependency>
            <groupId>com.alibaba.nacos</groupId>
            <artifactId>nacos-client</artifactId>
            <version>${nacos.version}</version>
        </dependency>

        <dependency>
            <groupId>org.boot.dubbo</groupId>
            <artifactId>dubbo-spring-boot-sample-api</artifactId>
            <version>${revision}</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <version>${spring-boot.version}</version>
        </dependency>
    </dependencies>

    ...
</project>

```

> 配置服务注册中心

```
spring:
  application:
    name: dubbo-registry-nacos-consumer-sample

demo:
  service:
    version: 1.0.0

nacos:
  host: localhost
  port: 8848

dubbo:
  registry:
    address: nacos://${nacos.host}:${nacos.port}

server:
  port: 8081
```

> 编写Controller用于消费服务

```java
@RestController
@RequestMapping("/demo")
public class DemoController {

    @Reference(version = "${demo.service.version}")
    private DemoService demoService;

    @GetMapping("/{name}")
    public String sayHello(@PathVariable("name") String name) {
        return demoService.sayHello(name);
    }

}
```

这里直接通过`@Reference`注解进行服务调用，比xml方式更加优雅方便。

### 验证

直接通过浏览器访问/demo/{name}这个url进行访问即可，网页上显示`hello,xxx`表示服务消费成功。


## 总结
可以看到Dubbo重新维护之后，非常重视生态的建设，并且也在积极探索，Dubbo现在也是Apache基金会的孵化项目，未来可期，是除了Spring Cloud 之后又一个不错的选择。不同于其他注册中心的是，Nacos在阿里的支持下，生态发展得相当不错，并且已经可以用于生产环境。

#### 源码
[github](https://github.com/LuoLiangDSGA/spring-learning/tree/master/boot-dubbo-registry-nacos)