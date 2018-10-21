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

> 编写一个基础CRUD接口`UserService.java`
```yaml
public interface UserService {
    /**
     * 保存用户
     * @param user
     */
    void saveUser(User user);

    /**
     * 根据名称查询用户
     * @param username
     * @return
     */
    User findUserByUsername(String username);

    /**
     * 更新用户信息
     * @param user
     */
    void updateUser(User user);

    /**
     * 根据ID删除用户
     * @param id
     */
    void deleteUserById(Long id);
}
```