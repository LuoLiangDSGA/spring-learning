## 在SpringBoot中使用MongoDB
> 最近学习了SpringBoot操作MongoDB，本篇文章用于记录学习内容

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

`pom.xml`中引入了`spring-boot-starter-data-mongodb`之后，只需要在yaml中进行简单的配置，就可以轻松地使用MongoTemplate操作MongoDB
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

> 编写`User.java`类，包含了用户的基本属性

```java
@Data
public class User implements Serializable {
    private static final long serialVersionUID = -7520384490152472164L;

    private Long id;

    private String username;

    private String password;
}
```

> 编写一个基础CRUD接口`UserService.java`，提供基础的用户CRUD方法

```java
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

> 用户操作接口实现类`UserServiceImpl.java`
```java
@Service
public class UserServiceImpl implements UserService {
    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public void saveUser(User user) {
        mongoTemplate.save(user);
    }

    @Override
    public User findUserByUsername(String username) {
        Criteria criteria = Criteria.where("username").is(username);
        Query query = new Query(criteria);

        return mongoTemplate.findOne(query, User.class);
    }

    @Override
    public void updateUser(User user) {
        Criteria criteria = Criteria.where("id").is(user.getId());
        Query query = new Query(criteria);
        Update update = new Update().set("username", user.getUsername())
                .set("password", user.getPassword());
        //更新结果集的第一条
        mongoTemplate.updateFirst(query, update, User.class);
        //更新结果集的所有
//        mongoTemplate.updateMulti(query, update, User.class);
    }

    @Override
    public void deleteUserById(Long id) {
        Criteria criteria = Criteria.where("id").is(id);
        Query query = new Query(criteria);
        mongoTemplate.remove(query);
    }
}
```

> 编写单元测试，测试CRUD功能

```java
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class UserServiceTest {
    @Resource
    private UserService userService;

    @Test
    public void saveUser() {
        User user = new User();
        user.setId(123456L);
        user.setUsername("mongodb");
        user.setPassword("root");
        userService.saveUser(user);
    }

    @Test
    public void findUserByUsername() {
        User user = userService.findUserByUsername("mongodb");
        log.debug("user is: {}", user.toString());
    }

    @Test
    public void updateUser() {
        User user = new User();
        user.setId(123456L);
        user.setUsername("mongodb");
        user.setPassword("rootroot");
        userService.updateUser(user);
    }

    @Test
    public void deleteUserById() {
        userService.deleteUserById(123456L);
    }
}
```

经测试，基本的CRUD操作都能够正常使用。

### 最后
本篇文章只记录了MongoDB基本的CRUD操作，很多更高级的操作没有涉及，以后涉及到之后会进行补充。