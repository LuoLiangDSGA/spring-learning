## SpringBoot2 WebFlux

> Author：FrankLo  
> Date：2018-09-02

### 开始
SpringBoot升级到2.0版本之后，支持了WebFlux

![](http://images.gitbook.cn/87db53c0-b936-11e7-b969-cb3cfaf54002)

> WebFlux是什么

相对于`SpringMVC`，`MVC`是基于`Servlet API`和`Servlet`容器设计的。`Spring WebFlux`是基于`Reactive Streams`和`Servlet3.1+`容器设计的。

> Reactor

RxJava 库是 JVM 上反应式编程的先驱，也是反应式流规范的基础。RxJava2在RxJava的基础上做了很多的更新。不过 RxJava 库也有其不足的地方。RxJava 产生于反应式流规范之前，虽然可以和反应式流的接口进行转换，但是由于底层实现的原因，使用起来并不是很直观。RxJava 2 在设计和实现时考虑到了与规范的整合，不过为了保持与 RxJava 的兼容性，很多地方在使用时也并不直观。Reactor 则是完全基于反应式流规范设计和实现的库，没有 RxJava 那样的历史包袱，在使用上更加的直观易懂。Reactor 也是 Spring 5 中反应式编程的基础。学习和掌握 Reactor 可以更好地理解 Spring 5 中的相关概念。
  
在 Java 程序中使用 Reactor 库非常的简单，只需要通过 Maven 或 Gradle 来添加对 io.projectreactor:reactor-core 的依赖即可，目前的版本是 3.0.5.RELEASE。

> Flux 和 Mono

Flux 和 Mono 是 Reactor 中的两个基本概念。Flux 表示的是包含 0 到 N 个元素的异步序列。在该序列中可以包含三种不同类型的消息通知：正常的包含元素的消息、序列结束的消息和序列出错的消息。当消息通知产生时，订阅者中对应的方法 onNext(), onComplete()和 onError()会被调用。Mono 表示的是包含 0 或者 1 个元素的异步序列。该序列中同样可以包含与 Flux 相同的三种类型的消息通知。Flux 和 Mono 之间可以进行转换。对一个 Flux 序列进行计数操作，得到的结果是一个 Mono<Long>对象。把两个 Mono 序列合并在一起，得到的是一个 Flux 对象。

- 创建Flux
  - just()：可以指定序列中包含的全部元素。创建出来的 Flux 序列在发布这些元素之后会自动结束。
  - fromArray()，fromIterable()和 fromStream()：可以从一个数组、Iterable 对象或 Stream 对象中创建 Flux 对象。
  - empty()：创建一个不包含任何元素，只发布结束消息的序列。
  - error(Throwable error)：创建一个只包含错误消息的序列。
  - never()：创建一个不包含任何消息通知的序列。
  - range(int start, int count)：创建包含从 start 起始的 count 个数量的 Integer 对象的序列。
  - interval(Duration period)和 interval(Duration delay, Duration period)：创建一个包含了从 0 开始递增的 Long 对象的序列。其中包含的元素按照指定的间隔来发布。除了间隔时间之外，还可以指定起始元素发布之前的延迟时间。
  - intervalMillis(long period)和 intervalMillis(long delay, long period)：与 interval()方法的作用相同，只不过该方法通过毫秒数来指定时间间隔和延迟时间。
- 代码实例如下：
  
```java
Flux.just("Hello", "World").subscribe(System.out::println);
Flux.fromArray(new Integer[] {1, 2, 3}).subscribe(System.out::println);
Flux.empty().subscribe(System.out::println);
Flux.range(1, 10).subscribe(System.out::println);
Flux.interval(Duration.of(10, ChronoUnit.SECONDS)).subscribe(System.out::println);
Flux.intervalMillis(1000).subscribe(System.out::println);
```

- 创建Mono

    - fromCallable()、fromCompletionStage()、fromFuture()、fromRunnable()和 fromSupplier()：分别从 Callable、CompletionStage、CompletableFuture、Runnable 和 Supplier 中创建 Mono。
    - delay(Duration duration)和 delayMillis(long duration)：创建一个 Mono 序列，在指定的延迟时间之后，产生数字 0 作为唯一值。
    - ignoreElements(Publisher<T> source)：创建一个 Mono 序列，忽略作为源的 Publisher 中的所有元素，只产生结束消息。
    - justOrEmpty(Optional<? extends T> data)和 justOrEmpty(T data)：从一个 Optional 对象或可能为 null 的对象中创建 Mono。只有 Optional 对象中包含值或对象不为 null 时，Mono 序列才产生对应的元素。

还可以通过 create()方法来使用 MonoSink 来创建 Mono。
```java
Mono.fromSupplier(() -> "Hello").subscribe(System.out::println);
Mono.justOrEmpty(Optional.of("Hello")).subscribe(System.out::println);
Mono.create(sink -> sink.success("Hello")).subscribe(System.out::println);
```

### WebFlux的使用

> 首先，需要创建一个SpringBoot2的项目工程，并且引入WebFlux和其他需要的依赖
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-test</artifactId>
    <scope>test</scope>
</dependency> 

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis-reactive</artifactId>
</dependency>

<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
</dependency>
```

> 配置Reactive Redis

```java
@SpringBootConfiguration
public class RedisConfig {
    @Resource
    private RedisConnectionFactory factory;

    @Bean
    public ReactiveRedisTemplate<String, String> reactiveRedisTemplate(ReactiveRedisConnectionFactory connectionFactory) {
        return new ReactiveRedisTemplate<>(connectionFactory, RedisSerializationContext.string());
    }

    @Bean
    public ReactiveRedisConnection connection(ReactiveRedisConnectionFactory connectionFactory) {
        return connectionFactory.getReactiveConnection();
    }

    @Bean
    ReactiveRedisOperations<String, Object> redisOperations(ReactiveRedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        RedisSerializationContext.RedisSerializationContextBuilder<String, Object> builder = RedisSerializationContext
                .newSerializationContext(new StringRedisSerializer());
        RedisSerializationContext<String, Object> context = builder.value(serializer).build();

        return new ReactiveRedisTemplate<>(factory, context);
    }

    public @PreDestroy
    void flushDb() {
        factory.getConnection().flushDb();
    }
}
```

> 编写一个`RedisLoader.java`类，在项目启动的时候初始化数据
```java
@Component
public class RedisLoader {
    @Resource
    private ReactiveRedisConnectionFactory factory;
    @Resource
    private ReactiveRedisOperations<String, Object> redisOperations;

    @PostConstruct
    public void loadData() {
        factory.getReactiveConnection().serverCommands().flushAll()
                .thenMany(Flux.just("Thor", "Hulk", "Tony")
                        .map(name -> new User(UUID.randomUUID().toString().substring(0, 5), name, "123456"))
                        .flatMap(user -> redisOperations.opsForValue().set(user.getId(), user))
                ).thenMany(redisOperations.keys("*")
                .flatMap(redisOperations.opsForValue()::get))
                .subscribe(System.out::println);
    }
}
```

> 创建一个简单的User.java类，作为用户数据模型
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String id;

    private String name;

    private String password;
}
```

> 定义用户数据操作接口`UserService.java`
```java
public interface UserService {
    /**
     * 用户注册
     *
     * @param id
     * @param username
     * @return
     */
    Mono<Boolean> add(String id, String username);

    /**
     * 用户登录
     *
     * @param username
     * @param password
     * @return
     */
    Mono<User> find(String username, String password);

    /**
     * 获取所有用户
     *
     * @return
     */
    Flux<User> getAll();

    Mono<Boolean> remove(String id);
}
```

> 定义接口实现类`UserServiceImpl.java`
```java
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Resource
    private ReactiveRedisOperations<String, User> redisOperations;

    @Override
    public Mono<Boolean> add(String id, String username) {
        User user = new User();
        user.setId(id);
        user.setName(username);
        user.setPassword("123456");
        return redisOperations.opsForValue().set(id, user);
    }

    @Override
    public Mono<User> find(String username, String password) {
        return redisOperations.opsForValue().get(username);
    }

    @Override
    public Flux<User> getAll() {
        return redisOperations.keys("*")
                .flatMap(redisOperations.opsForValue()::get);
    }

    @Override
    public Mono<Boolean> remove(String id) {
        return redisOperations.opsForValue().delete(id);
    }
}
```

> 创建基于`SpringMVC`的`REST API`
```java
@RestController
public class UserController {
    @Resource
    private UserService userService;

    @GetMapping("/users")
    public Flux<User> all() {
        return userService.getAll();
    }

    @PostMapping("/add")
    public Mono<Boolean> register(@RequestBody User user) {
        return userService.add(user.getId(), user.getName());
    }

    @PostMapping("/find")
    public Mono find(@RequestBody User user) {
        return userService.find(user.getName(), user.getPassword());
    }
}
```

> 基于 Functional 函数式路由实现 RESTful API
```java
@SpringBootConfiguration
public class Router {
    @Resource
    private UserHandler userHandler;

    @Bean
    public RouterFunction<?> routerFunction() {
        return RouterFunctions.route(RequestPredicates.GET("/hello"), userHandler::hello)
                .andRoute(RequestPredicates.POST("/login"), userHandler::login);
    }
}
```

> `UserHandler.java`

```java
@Service
public class UserHandler {
    private final static Logger log = LoggerFactory.getLogger(UserHandler.class);
    @Resource
    private ReactiveRedisConnection connection;

    public Mono<ServerResponse> hello(ServerRequest request) {
        return ServerResponse
                .ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(BodyInserters.fromObject("Hello, World"));
    }

    /**
     * 登录
     *
     * @param request
     * @return
     */
    public Mono<ServerResponse> login(ServerRequest request) {
        Mono<Map> body = request.bodyToMono(Map.class);
        return body.flatMap(map -> {
            String username = (String) map.get("username");
            String password = (String) map.get("password");
            log.debug("username:{},password:{}", username, password);
            return connection.stringCommands().get(
                    ByteBuffer.wrap(username.getBytes()))
                    .flatMap(byteBuffer -> {
                        byte[] bytes = new byte[byteBuffer.remaining()];
                        byteBuffer.get(bytes, 0, bytes.length);
                        String userStr;
                        userStr = new String(bytes, StandardCharsets.UTF_8);
                        log.debug(userStr);
                        User user = JSON.parseObject(userStr, User.class);
                        Map<String, String> result = new HashMap<>(2);
                        if (Objects.isNull(user.getPassword()) || !user.getPassword().equals(password)) {
                            result.put("message", "账号或密码错误");
                            log.debug("账号或密码错误");
                            return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                                    .body(BodyInserters.fromObject(result));
                        } else {
                            result.put("message", "登录成功");
                            log.debug("登录成功");
                            return ServerResponse.ok()
                                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                                    .body(BodyInserters.fromObject(result));
                        }
                    });
        });
    }
}
```

### 参考
- https://www.ibm.com/developerworks/cn/java/j-cn-with-reactor-response-encode/index.html