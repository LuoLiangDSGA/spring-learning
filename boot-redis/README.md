---
title: SpringBoot中使用Redis的实践
date: 2018-05-22 11:07:06
categories: "SpringBoot"
tags:
     - SpringBoot
     - Redis
---
## SpringBoot中使用Redis的实践
> Redis是一个高性能的内存数据库，在日常开发中运用非常的广泛，主要用作缓存。Redis提供了非常丰富的数据结构，有String，List，Set，ZSet，Hash，
Redis为这些数据结构提供了丰富的原子性操作。弥补了其他NoSQL如Memcached的不足。在SpringBoot中，由于Boot提供了强大的AutoConfiguration，
集成Redis变得非常简单。本文将介绍Redis在SpringBoot中的应用，包括手动使用RedisTemplate进行操作，和使用注解（@Cacheable等）把业务数据缓存到Redis中。

### 开始
环境：JDK1.8，Maven3+，Redis3  
需要预先安装好Redis，也可以使用Docker快速部署一个Redis，可以参考我之前的[文章](https://luoliangdsga.github.io/2018/04/26/使用Docker部署Redis/)  
<!-- more -->

新建一个SpringBoot项目，引入需要用到的相关maven依赖
```xml
  <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <!--SpringBoot的Redis支持-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <!--SpringBoot缓存支持-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-cache</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
    </dependencies>
```
在yaml文件中配置redis连接：
```yaml
spring:
  redis:
    #Redis服务器地址，默认localhost
    host: localhost
    #Redis服务器端口，默认6379
    port: 6379
    pool:
      #连接池最大连接数
      max-active: 8
      #最大阻塞等待时间，-1表示没有限制
      max-wait: -1
      #最大空闲连接
      max-idle: 8
      #最小空闲连接
      min-idle: 0
    #连接超时时间
    timeout: 0

```
### 使用RedisTemplate操作Redis
spring-data-redis提供了一个RedisTemplate类，这个类封装了对Redis基本数据结构的常用操作，它的子类StringRedisTemplate提供了对字符串的常用
操作，接下来将使用StringRedisTemplate来操作Redis中的String和List类型。

注入StringRedisTemplate
```java
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
```

```java

    /**
     * 操作字符串
     */
    private void operateString() {
        stringRedisTemplate.opsForValue().set("author", "luoliang");
        String value = stringRedisTemplate.opsForValue().get("author");
        log.info("stringRedisTemplate输出值：{}", value);
    }

    /**
     * Redis List操作，Redis列表是简单的字符串列表，按照插入顺序排序。可以添加一个元素到列表的头部（左边）或者尾部（右边）
     */
    private void operateList() {
        String key = "website";
        ListOperations<String, String> listOperations = stringRedisTemplate.opsForList();
        //从左压入栈
        listOperations.leftPush(key, "Github");
        listOperations.leftPush(key, "CSDN");
        //从右压入栈
        listOperations.rightPush(key, "SegmentFault");
        log.info("list size:{}", listOperations.size(key));
        List<String> list = listOperations.range(key, 0, 2);
        list.forEach(log::info);
    }

```
上面涉及到的两种类型的操作，都是针对的字符串，可不可以存取对象呢？答案当然是可以的。我们使用Hash来存取对象，首先新建一个User类，用于存取
使用。

**此处需要注意，User类需要实现Serializable接口，否则无法序列化**
```java
@Data
@Builder
public class User implements Serializable {
    private String id;

    private String name;

    private Integer age;
}
```
这时候就不能再使用StringRedisTemplate了，所以需要配置针对Object的RedisTemplate实例，这里可以使用默认的JdkSerializationRedisSerializer
序列化，也可以自己实现RedisSerializer接口来自定义序列化
```java
@Configuration
public class RedisConfig {
    @Resource
    private JedisConnectionFactory jedisConnectionFactory;

    @Bean
    public RedisTemplate<String, Object> objRedisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new JdkSerializationRedisSerializer());
        return template;
    }
}
```
操作Hash
```java
    @Resource
    private RedisTemplate<String, Object> objRedisTemplate;

    /**
     * 操作hash，存放User对象
     */
    private void operateHash() {
        String key = "user";
        HashOperations<String, String, User> hashOperations = objRedisTemplate.opsForHash();
        hashOperations.put(key, "user1", User.builder().name("Hulk").age(50).build());
        hashOperations.put(key, "user2", User.builder().name("Thor").age(1500).build());
        hashOperations.put(key, "user3", User.builder().name("Rogers").age(150).build());
        log.info("hash size:{}", hashOperations.size(key));
        log.info("--------拿到Map的key集合--------");
        Set<String> keys = hashOperations.keys(key);
        keys.forEach(log::info);
        log.info("--------拿到Map的value集合--------");
        List<User> users = hashOperations.values(key);
        users.forEach(user -> log.info(user.toString()));
        log.info("--------拿到user1的value--------");
        User user = hashOperations.get(key, "user1");
        log.info(user.toString());
    }
```
最后，验证我们的操作，可以看到，结果和预期相同。
```java
2018-05-22 10:45:07.754  INFO 42127 --- [           main] org.boot.redis.BootRedisApplication      : ----------Operate String----------
2018-05-22 10:45:07.820  INFO 42127 --- [           main] org.boot.redis.BootRedisApplication      : stringRedisTemplate输出值：luoliang
2018-05-22 10:45:07.821  INFO 42127 --- [           main] org.boot.redis.BootRedisApplication      : ----------Operate List----------
2018-05-22 10:45:07.832  INFO 42127 --- [           main] org.boot.redis.BootRedisApplication      : list size:57
2018-05-22 10:45:07.836  INFO 42127 --- [           main] org.boot.redis.BootRedisApplication      : CSDN
2018-05-22 10:45:07.836  INFO 42127 --- [           main] org.boot.redis.BootRedisApplication      : Github
2018-05-22 10:45:07.836  INFO 42127 --- [           main] org.boot.redis.BootRedisApplication      : CSDN
2018-05-22 10:45:07.836  INFO 42127 --- [           main] org.boot.redis.BootRedisApplication      : ----------Operate Hash----------
2018-05-22 10:45:07.858  INFO 42127 --- [           main] org.boot.redis.BootRedisApplication      : hash size:3
2018-05-22 10:45:07.858  INFO 42127 --- [           main] org.boot.redis.BootRedisApplication      : --------拿到Map的key集合--------
2018-05-22 10:45:07.865  INFO 42127 --- [           main] org.boot.redis.BootRedisApplication      : user2
2018-05-22 10:45:07.866  INFO 42127 --- [           main] org.boot.redis.BootRedisApplication      : user1
2018-05-22 10:45:07.866  INFO 42127 --- [           main] org.boot.redis.BootRedisApplication      : user3
2018-05-22 10:45:07.866  INFO 42127 --- [           main] org.boot.redis.BootRedisApplication      : --------拿到Map的value集合--------
2018-05-22 10:45:07.870  INFO 42127 --- [           main] org.boot.redis.BootRedisApplication      : User(id=null, name=Thor, age=1500)
2018-05-22 10:45:07.870  INFO 42127 --- [           main] org.boot.redis.BootRedisApplication      : User(id=null, name=Hulk, age=50)
2018-05-22 10:45:07.870  INFO 42127 --- [           main] org.boot.redis.BootRedisApplication      : User(id=null, name=Rogers, age=150)
2018-05-22 10:45:07.870  INFO 42127 --- [           main] org.boot.redis.BootRedisApplication      : --------拿到user1的value--------
2018-05-22 10:45:07.873  INFO 42127 --- [           main] org.boot.redis.BootRedisApplication      : User(id=null, name=Hulk, age=50)
```
### 使用Annotation缓存数据
上面的操作方式，是手动操作Redis进行存取，在真实的业务场景中，我们并不想这样去使用，而是把Redis当做一种缓存来使用，把service或者dao层的数据进行缓存，
最简单的方式就是通过注解。在SpringBoot中使用Redis做缓存也非常简单，只需要在pom中引入spring-boot-starter-cache即可。

下面列出的是Spring缓存的常用注解（来自@程序猿DD）：
- @CacheConfig：主要用于配置该类中会用到的一些共用的缓存配置。在这里@CacheConfig(cacheNames = "users")：配置了该数据访问对象中返回的
内容将存储于名为users的缓存对象中，我们也可以不使用该注解，直接通过@Cacheable自己配置缓存集的名字来定义。
- @Cacheable：配置了findByName函数的返回值将被加入缓存。同时在查询时，会先从缓存中获取，若不存在才再发起对数据库的访问。该注解主要有下面几个参数：
  1. value、cacheNames：两个等同的参数（cacheNames为Spring4新增，作为value的别名），用于指定缓存存储的集合名。由于Spring 4中新增了@CacheConfig，
因此在Spring 3中原本必须有的value属性，也成为非必需项了
  2. key：缓存对象存储在Map集合中的key值，非必需，缺省按照函数的所有参数组合作为key值，若自己配置需使用SpEL表达式，比如：@Cacheable(key = "#p0")：
使用函数第一个参数作为缓存的key值，更多关于SpEL表达式的详细内容可参考官方文档
  3. condition：缓存对象的条件，非必需，也需使用SpEL表达式，只有满足表达式条件的内容才会被缓存，比如：@Cacheable(key = "#p0", condition = "#p0.length() < 3")，
表示只有当第一个参数的长度小于3的时候才会被缓存，若做此配置上面的AAA用户就不会被缓存，读者可自行实验尝试。
  4. unless：另外一个缓存条件参数，非必需，需使用SpEL表达式。它不同于condition参数的地方在于它的判断时机，该条件是在函数被调用之后才做判断的，
所以它可以通过对result进行判断。
  5. keyGenerator：用于指定key生成器，非必需。若需要指定一个自定义的key生成器，我们需要去实现org.springframework.cache.interceptor.KeyGenerator接口，
并使用该参数来指定。需要注意的是：该参数与key是互斥的
  6. cacheManager：用于指定使用哪个缓存管理器，非必需。只有当有多个时才需要使用
  7. cacheResolver：用于指定使用那个缓存解析器，非必需。需通过org.springframework.cache.interceptor.CacheResolver接口来实现自己的
缓存解析器，并用该参数指定。
- @CachePut：配置于函数上，能够根据参数定义条件来进行缓存，它与@Cacheable不同的是，它每次都会真实调用函数，所以主要用于数据新增和修改操作上
。它的参数与@Cacheable类似，具体功能可参考上面对@Cacheable参数的解析。
- @CacheEvict：配置于函数上，通常用在删除方法上，用来从缓存中移除相应数据。除了同@Cacheable一样的参数之外，它还有下面两个参数：
1. allEntries：非必需，默认为false。当为true时，会移除所有数据
2. beforeInvocation：非必需，默认为false，会在调用方法之后移除数据。当为true时，会在调用方法之前移除数据。

由于本项目没有涉及到数据库的链接，下面，我们来模拟数据库的操作，并把结果缓存到Redis中
```java
@Service
@Slf4j
@CacheConfig(cacheNames = "users")
public class RedisCacheServiceImpl implements RedisCacheService {

    @Override
    @CachePut(key = "#p0.id")
    public User save(User user) {
        log.info("-----执行数据库更新操作");
        log.info("-----数据库更新完成，返回结果");

        return user;
    }

    @Override
    @Cacheable(key = "#p0")
    public User get(String id) {
        log.info("-----执行数据库查询操作");
        User user = User.builder().id(id).name("spring").age(18).build();
        log.info("-----数据库查询完成，返回结果");
        return user;
    }

    @Override
    @CacheEvict(key = "#p0")
    public void delete(String id) {
        log.info("-----执行数据库删除操作");
        log.info("-----数据库删除完成，返回结果");
    }
}
```

在Junit中进行测试
```java
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
public class RedisCacheServiceTest {
    @Resource
    private RedisCacheService redisCacheService;

    @Test
    public void testGet() {
        User user = redisCacheService.get("1111111");
        log.info(user.toString());
    }

    @Test
    public void testSave() {
        User user = User.builder().id("1111111").name("spring").age(20).build();
        redisCacheService.save(user);
    }

    @Test
    public void testDelete() {
        redisCacheService.delete("1111111");
    }
}
```
先调用get方法，此时，Redis中没有此数据，会进入方法，拿到数据之后返回，并且把数据缓存到Redis中，结果如下：
```java
2018-05-22 10:57:57.532  INFO 42313 --- [           main] o.b.r.s.impl.RedisCacheServiceImpl       : -----执行数据库查询操作
2018-05-22 10:57:57.533  INFO 42313 --- [           main] o.b.r.s.impl.RedisCacheServiceImpl       : -----数据库查询完成，返回结果
2018-05-22 10:57:57.557  INFO 42313 --- [           main] o.b.redis.service.RedisCacheServiceTest  : User(id=1111111, name=spring, age=18)
```
再调用一次get方法，此时将不会进入方法中，直接从缓存中拿到数据并返回，结果如下：
```java
2018-05-22 10:57:57.557  INFO 42313 --- [           main] o.b.redis.service.RedisCacheServiceTest  : User(id=1111111, name=spring, age=18)
```
再调用save方法，会把缓存中ID为1111111的User年龄更新为20，调用delete方法会删除缓存，和预期的结果一致，这里就不贴结果了，感兴趣的同学可以自行验证。

### 总结
至此，整篇文章就结束了，文章包含了RedisTemplate的使用，以及Spring提供的@Cacheable等注解的使用，都是日常开发中常常用到的东西。

最后，附上github源码，欢迎star，一起交流。[boot-redis](https://github.com/LuoLiangDSGA/Spring-Learning/tree/master/boot-redis)
