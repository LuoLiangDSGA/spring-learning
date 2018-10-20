## SpringBoot MongoDB

### 使用Docker运行MongoDB
```jshelllanguage
docker run -d -p 27017:27017 --name mongo mongo --auth
```

### 创建一个SpringBoot项目
> 引入所需依赖

```xml
 <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-mongodb</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
 </dependencies>
```

> yaml配置

`pom.xml`中引入了`spring-boot-starter-data-mongodb`之后，只需要在yaml中进行简单的配置，就可以轻松地操作MongoDB
```yaml
server:
  port: 8081

spring:
  data:
    mongodb:
      uri: mongodb://root:root@localhost:27017/test
      username: root
      authentication-database: root

logging:
  level:
    org.boot: debug
```

