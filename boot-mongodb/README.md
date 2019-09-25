## 在SpringBoot中使用MongoDB
> 最近项目中使用了MongoDB，在SpringBoot中集成了MongoDB，MongoDB是当前非常火的一个非关系型数据库，同时也是最接近关系型数据库的，本篇文章用于记录SpringBoot中集成MongoDB。

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
<!-- more -->

> yaml配置

`pom.xml`中引入了`spring-boot-starter-data-mongodb`之后，只需要在yaml中进行简单的配置，就可以轻松地使用MongoTemplate操作MongoDB
```yaml
server:
  port: 8081

#mongodb
# 单机模式 mongodb://name:pass@ip:port/database
# 集群模式 mongodb://user:pwd@ip1:port1,ip2:port2/database
spring:
  data:
    mongodb:
      uri: mongodb://root:root@118.24.147.38:27017/test
      username: root
      authentication-database: root

logging:
  level:
    org.boot: debug
```
**配置完成后就可以开始编写代码访问MongoDB了，在spring-data中访问MongoDB有多种方式，主要有三种，原生DB对象，MongoTemplate，MongoRepository，第一种方式过于繁琐，这里只写后两种。**

### MongoTemplate方式
> 编写`User.java`类，包含了用户的基本属性

```java
@Data
@Document(collection = "user") // 集合名称
public class User implements Serializable {
    private static final long serialVersionUID = -7520384490152472164L;

    @Id
    private String id;
    @Field
    @Indexed
    private String username;
    @Field
    private String password;
    @CreatedDate
    private Date gmtCreate;
}
```
	1. @Id表明这是每一条文档的id，MongoDB会自动生成
	2. @Field可指定存储的键值名称，默认就是类字段名
	3. @Indexed表示这是一个索引字段
	4. @CreatedDate会自动赋值当前时间，但是注意需要在启动类中添加@EnableMongoAuditing使其生效
	

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

### MongoRepository方式

相比上面的方式，这种方式使用jpa来操作数据库，更加优雅、简单。只需要继承`MongoRepository`，里面已经提供了大多数基础的数据操作方法。

```java
public interface UserRepository extends MongoRepository<User, String> {
    /**
     * 按名称进行查询
     *
     * @param username
     * @return
     */
    User findUserByUsername(String username);

    /**
     * 自定义查询语句，根据日期查询
     *
     * @param create
     * @param pageable
     * @return
     */
    @Query("{'gmtCreate': ?0}")
    Page<User> queryBySql(String create, Pageable pageable);
}
```
在其他类中注入此类即可。

### 最后
本篇文章只记录了MongoDB基本的CRUD操作，并且记录了两种操作方式，很多更高级的操作没有涉及，以后涉及到之后会进行补充。