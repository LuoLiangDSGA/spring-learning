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

首先，需要创建一个SpringBoot2的项目工程，并且引入WebFlux和其他需要的依赖
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



### 参考
- https://www.ibm.com/developerworks/cn/java/j-cn-with-reactor-response-encode/index.html