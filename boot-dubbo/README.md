## SpringBoot整合Dubbo2.5.10
### 开始
Dubbo已经进入了Apache孵化器，并且发布了官方的spring-boot-starter0.1.0，用于简化dubbo应用的配置，主要包括了autoconfigure(自动装配)，externalized-configuration(外部化配置)，actuator(生产准备)等，可参考官方github  [dubbo-spring-boot-starter](https://github.com/apache/incubator-dubbo-spring-boot-project/releases/tag/0.1.0).
### 准备工作
需要提前安装好JDK1.8，Maven，Zookeeper，Docker。
### 初始化Maven项目
为了整个项目结构清晰，使用模块化的maven项目。pom文件如下：
``` 
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://maven.apache.org/POM/4.0.0"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>1.3.7.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <modelVersion>4.0.0</modelVersion>
    <groupId>org.boot.dubbo</groupId>
    <artifactId>springboot-dubbo</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>pom</packaging>

    <name>springboot-dubbo</name>
    <description>Dubbo project for Spring Boot</description>

    <modules>
        <module>dubbo-provider</module>
        <module>dubbo-consumer</module>
    </modules>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
        <dubbo-spring-boot-starter.version>0.1.0</dubbo-spring-boot-starter.version>
        <fastjson_version>1.2.31</fastjson_version>
        <dubbo-provider.version>1.0.0-SNAPSHOT</dubbo-provider.version>
        <zk-client.version>0.2</zk-client.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>com.alibaba.boot</groupId>
                <artifactId>dubbo-spring-boot-starter</artifactId>
                <version>${dubbo-spring-boot-starter.version}</version>
            </dependency>

            <dependency>
                <groupId>org.boot.dubbo</groupId>
                <artifactId>dubbo-provider</artifactId>
                <version>${dubbo-provider.version}</version>
            </dependency>

            <dependency>
                <groupId>com.101tec</groupId>
                <artifactId>zkclient</artifactId>
                <version>${zk-client.version}</version>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>

``` 
主要分为两个模块，一个provider，一个consumer

![](https://ws4.sinaimg.cn/large/006tKfTcgy1fpkfm3haphj3097091aam.jpg)

### 创建生产者
有了spring-boot-starter，dubbo的配置变得非常简单，再也不用像以前一样配置一大堆xml文件，只需要几个简单的配置，就可以做到开箱即用。

1. 先配置生产者的pom.xml

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://maven.apache.org/POM/4.0.0"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>org.boot.dubbo</groupId>
    <artifactId>dubbo-provider</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <parent>
        <artifactId>springboot-dubbo</artifactId>
        <groupId>org.boot.dubbo</groupId>
        <version>1.0.0-SNAPSHOT</version>
        <relativePath>../pom.xml</relativePath>
    </parent>

    <name>dubbo-provider</name>
    <description>Dubbo project for Spring Boot:provider</description>

    <dependencies>
        <!-- Spring Boot dependencies -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-actuator</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
        </dependency>

        <dependency>
            <groupId>com.alibaba.boot</groupId>
            <artifactId>dubbo-spring-boot-starter</artifactId>
        </dependency>

        <dependency>
            <groupId>com.101tec</groupId>
            <artifactId>zkclient</artifactId>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

2. 接着使用properties进行SpringBoot和Dubbo的配置，配置如下：
```spring.application.name=springboot-dubbo-provider
   server.port=9090
   #dubbo配置
   dubbo.application.id=springboot-dubbo-provider
   dubbo.application.name=springboot-dubbo-provider
   dubbo.application.owner=luoliang
   #协议配置
   dubbo.protocol.id=dubbo
   dubbo.protocol.name=dubbo
   #把默认的20880端口换成12345
   dubbo.protocol.port=12345
   #服务注册配置
   dubbo.registry.id=my-registry
   dubbo.registry.address=zookeeper://localhost:2181
   #配置dubbo的包扫描，针对dubbo的@Service, @Reference注解
   dubbo.scan.base-packages=org.boot.dubbo.provider.service
   #dubbo健康监控
   endpoints.dubbo.enabled=true
   management.health.dubbo.status.defaults=memory
   management.health.dubbo.status.extras=load,threadpool
   management.port=9091
 ```
 3. 进行了上面两步之后，Dubbo已经集成好了，接下来就可以直接开始撸服务代码了，可以直接使用注解来暴露服务接口
* 先写一个interface
 ```
public interface DubboService {
    String sayHello(String name);
}
```
* 实现接口，加上自己的业务逻辑
```
@Service(version = "1.0.0",
        application = "${dubbo.application.id}",
        protocol = "${dubbo.protocol.id}",
        registry = "${dubbo.registry.id}")
public class DubboServiceImpl implements DubboService {

    @Override
    public String sayHello(String name) {
        return "Hello, " + name + " (from Spring Boot)";
    }
}
```
注意，这里的service注解是com.alibaba.dubbo.config.annotation.Service

### 创建消费者

1. 配置消费者者的pom.xml

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://maven.apache.org/POM/4.0.0"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>org.boot.dubbo</groupId>
    <artifactId>dubbo-consumer</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <parent>
        <artifactId>springboot-dubbo</artifactId>
        <groupId>org.boot.dubbo</groupId>
        <version>1.0.0-SNAPSHOT</version>
        <relativePath>../pom.xml</relativePath>
    </parent>

    <name>dubbo-consumer</name>
    <description>Dubbo project for Spring Boot:consumer</description>

    <dependencies>
        <!-- Spring Boot dependencies -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-actuator</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
        </dependency>

        <dependency>
            <groupId>com.alibaba.boot</groupId>
            <artifactId>dubbo-spring-boot-starter</artifactId>
        </dependency>

        <dependency>
            <groupId>org.boot.dubbo</groupId>
            <artifactId>dubbo-provider</artifactId>
        </dependency>

        <dependency>
            <groupId>com.101tec</groupId>
            <artifactId>zkclient</artifactId>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>

```
2. application.properties配置如下:
```
spring.application.name=springboot-dubbo-consumer
server.port=8081
#dubbo配置
dubbo.application.id=springboot-dubbo-consumer
dubbo.application.name=springboot-dubbo-consumer
dubbo.application.owner=luoliang
#服务注册配置
dubbo.registry.id=my-registry
dubbo.registry.address=zookeeper://localhost:2181
management.port=8082
```
3. 编写service来消费dubbo的服务，主要代码如下：
```
@Service
public class ConsumerServiceImpl implements ConsumerService {
    @Reference(version = "1.0.0",
            application = "${dubbo.application.id}")
    private DubboService dubboService;

    @Override
    public String sayHello(String name) {
        return dubboService.sayHello(name);
    }
}
```
在mvc的controller中注入此服务
```
@RestController
@RequestMapping("/user")
public class DefaultController {
    @Resource
    private ConsumerService consumerService;

    @RequestMapping("/sayHello")
    public String register(String name) {
        return consumerService.sayHello(name);
    }
}

```
##### 到这里，整个项目基本结构已经搭建完成，consumer已经能够消费provider提供的服务。

现在来测试一下，分别启动provider和consumer，打开浏览器，输入http://localhost:8081/user/sayHello?name=dubbo

可以看到，返回的结果和预期一样，说明项目已经成功集成

![](https://ws1.sinaimg.cn/large/006tKfTcgy1fpkgcg2ykuj30cm037dg5.jpg)

需要源码请移步本人github，如果能顺手star就更好啦! [boot-dubbo](https://github.com/LuoLiangDSGA/SpringBoot-Learning/tree/master/boot-dubbo)

### 参考
* https://github.com/apache/incubator-dubbo-spring-boot-project

