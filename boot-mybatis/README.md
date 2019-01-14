## SpringBoot集成Mybatis实战

> mybatis是一款优秀的持久层框架，支持定制化SQL，存储过程和高级映射。MyBatis 避免了几乎所有的 JDBC 代码和手动设置参数以及获取结果集。MyBatis 可以使用简单的 XML 或注解来配置和映射原生信息，将接口和 Java 的 POJOs(Plain Old Java Objects,普通的 Java对象)映射成数据库中的记录。

### 起步

> SpringBoot可以通过`MyBatis-Spring-Boot-Starter`，快速集成Mybatis，只需在maven中引入依赖

```java
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>1.3.2</version>
</dependency>
```
`mybatis-spring-boot-starter`提供了以下功能：
- 自动检测现有数据源
- 创建并注册SQLSessionFactory的实例，该实例使用SqlSessionFactoryBean将该数据源作为输入
- 创建并注册在SqlSessionFactory中获取的SqlSessionTemplate实例
- 自动扫描Mapper并链接到SqlSessionTemplate，并将它们注册到Spring上下文中，这样它们就能在Bean中被注入
<!-- more -->
> 引入依赖之后，还需要在配置文件中添加JDBC基本的配置

```yaml
mybatis:
  type-aliases-package: org.boot.mybatis.model
  type-handlers-package: org.boot.mybatis.typehandler
  configuration:
    map-underscore-to-camel-case: true
    default-fetch-size: 100
    default-statement-timeout: 30
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    password: root
    username: root
    url: jdbc:mysql://localhost:3306/test
```

这里使用了MySQL作为数据源，但是请注意这里的`driver-class-name`和以前的已经不一样了， `com.mysql.cj.jdbc.Driver`是mysql-connector-java6中的新驱动名称，如果使用老版本的mysql-connector-java，名称还是`com.mysql.jdbc.Driver`

> 配置完成之后，编写数据访问层(DAO)，也就是`Mybatis`文档上写的`Mapper`，提供对`User`表的`CRUD`操作，这里使用的是注解的方式，需要在类名上添加`@Mapper`注解

```java
@Mapper
public interface UserMapper {
    /**
     * 根据ID查询用户
     *
     * @param id
     * @return
     */
    @Select("select * from user where id = #{id}")
    User findById(Integer id);

    /**
     * 添加一条用户数据
     *
     * @param user
     */
    @Insert("insert into user(name, password, state, address, email) values (#{name}, #{password}, #{state}, #{address}, #{email})")
    void insert(User user);

    /**
     * 更新用户数据
     *
     * @param user
     */
    @Update("update user set name=#{name},password=#{password},state=#{state},address=#{address},email=#{email} where id=#{id}")
    void update(User user);

    @Delete("delete from user where id = #{id}")
    void delete(Integer id);

    /**
     * 查询指定状态的用户列表
     *
     * @param state
     * @return
     */
    @Select("select * from user where id = #{state}")
    List<User> selectList(Integer state);
}

```

编写测试方法对Mapper进行简单的测试

```java
public class UserTest extends BootMybatisApplicationTests {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserTest.class);
    @Resource
    private UserMapper userMapper;

    @Test
    public void testInsertUser() {
        User user = new User();
        user.setName("thor");
        user.setPassword("1234");
        user.setAddress("Cheng Du");
        user.setEmail("1234@gmail.com");
        user.setState(1);
        userMapper.insert(user);
    }

    @Test
    public void testUpdateUser() {
        User user = new User();
        user.setId(1);
        user.setName("thor");
        user.setPassword("123456");
        user.setAddress("Cheng Du");
        user.setEmail("1234@gmail.com");
        user.setState(1);
        userMapper.update(user);
    }

    @Test
    public void testFindById() {
        User user = userMapper.findById(1);
        Assert.assertNotEquals(user, null);
        LOGGER.info(user.toString());
    }

    @Test
    public void testDeleteUser() {
        userMapper.delete(2);
    }

    @Test
    public void testSelectList() {
        List<User> list = userMapper.selectList(1);
        LOGGER.info(list.toString());
    }
}

```

### End
本文使用SpringBoot集成了Mybatis，但只简单的使用了Mybatis，编写了一个单表的栗子，使用的也是Annotation的形式，在日常开发中还是xml+interface的形式更方便编码。实际上Mybatis的功能非常强大，本文没有写出来，感兴趣请查阅官方文档。
