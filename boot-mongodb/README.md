## SpringBoot MongoDB

### Docker创建MongoDB
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

