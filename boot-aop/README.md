### Spring AOP其实很简单

#### 什么是AOP
AOP（Aspect-Oriented Programming），面向切面编程，是OOP的补充和完善。OOP允许定义从上到下的关系，但并不适合从左到右的关系。比如日志功能，日志的记录往往散步在系统的各个地方，如果用OOP来实现，就会出现大量重复的代码，而这些记录日志的动作和核心业务没有直接的关系，这时候就需要AOP，对所有记录日志的动作进行一种称为“横切”的操作。就是用这种“横切”的操作，剖解开对象内部，将那些影响多个类的公共行为封装到一个可重用的模块，命名为“Aspect”，即方面。就是将那些与业务无关，却为业务模块所共用的逻辑，封装起来，减少代码的重复，同时降低系统耦合度。

> 下面以几张图来说明AOP的作用：

在日常的开发中，多个业务逻辑会存在相同代码的情况，这时候屌丝程序员就会进行一个操作-复制->粘贴->大功告成！

<div align=center>
<img width="250" height="250" src="https://s1.ax1x.com/2018/03/28/9jVLDg.png"/>
</div>

这样就会存在一个问题，如果这些相同的代码块需要修改，如果只有两三个业务逻辑使用的话还好，如果有成千上万个需要改，那这种做法是很难维护的。

这时候普通程序员出来了，觉得这样相同的代码逻辑可以提出来，单独写在一个方法里面，这样每一个需要使用这个代码块的业务直接调用方法就好了，就算以后要修改，也只需要改一个方法。

<div align=center>
<img width="250" height="250" src="https://s1.ax1x.com/2018/03/28/9jZmP1.png"/>
</div>

这样的方法极大地提高了系统的可维护性，但是也存在一个问题，每个业务逻辑调用这个方法，那么这些业务逻辑就和这个方法以硬编码的方式强耦合了。

这时候文艺程序员站了出来，他觉得我们可以使用AOP来达到一种效果，这些业务逻辑不需要自己去调用这个方法，它们只需要执行自己主要的业务，而相同的这部分代码块，通过AOP动态的织入业务中，起到一种对原有业务增强的作用。

#### OOP和AOP
- 概念
1. OOP  Object-Oriented Programming 面向对象编程
2. AOP  Aspect-Oriented Programming 面向切面编程

- 方向
1. OOP定义从上到下的关系
2. AOP定义从左到右的关系

- 核心关注点
1. OOP - 业务处理的主要流程，与业务主要流程关系不大的部分
2. AOP - 经常发生在核心关注点的多处，而各处都基本相似，比如权限，日志，事务处理

#### AOP主要使用场景
- 缓存代理，缓存某方法的返回值，下次执行该方法时，直接从缓存里获取。
- 记录日志，在方法执行前后记录系统日志。
- 权限验证，方法执行前验证是否有权限执行当前方法，没有则抛出没有权限执行异常，由业务代码捕捉。

#### Spring AOP重要概念
- 名词
1. Aspect（切面）：一个关注点的模块化，这个关注点会横切多个对象。
2. Joinpoint（连接点）：在程序执行过程中某个特定的点，比如某方法调用的时候。
3. Pointcut（切入点）：匹配连接点的断言，Advice和一个Pointcut表达式关联，并在满足这个Pointcut的Jointpoint上运行。
4. Introduction（引入）：用来给一个类型声明额外的方法或属性。Spring可以引入新的接口到任何被代理的对象。
5. Target Object（目标对象）：被一个或多个切面所通知的对象，Spring AOP是通过运行时代理实现的，所以这个对象永远是一个被代理对象。
6. AOP proxy（AOP 代理）：AOP框架动态创建的对象，用来执行切面所定义的方法。在Spring中，AOP代理可以是JDK动态代理或者Cglib动态代理。
7. Weaving（织入）：把切面连接到其他的应用程序类型或者对象上，并创建一个被通知的对象。这些可以在编译时、类加载时或者运行时完成。Spring是在运行时完成的织入。
8. Advice（通知）：在切面的某个特定的连接点上执行的动作。
- 通知类型
1. Before advice（前置通知）：在某连接点之前执行的通知，但这个通知不能阻止连接点之前执行的流程（除开抛出异常）。
2. After returning advice（后置通知）：在某连接点正常完成后执行的通知，一个方法正常返回后，没有异常。
3. After throw advice（异常通知）：在方法抛出异常时执行的通知。
4. After advice（最终通知）：当某连接点退出的时候执行的通知，不论是正常返回还是异常退出。
5. Around advice（环绕通知）：包围一个连接点的通知，如方法调用。环绕通知可以在方法调用前后完成自定义的行为。他也可以选择是否执行连接点或直接返回他自己的返回值或抛出异常来结束执行。

#### 实例
- AOP事务的实现

接下来通过注解的方式来使用Spring AOP，模拟在Service层数据库操作前后事务以及日志记录的执行。

1. 新建一个SpringBoot的项目，并且在pom中引入AOP需要的依赖

```
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-aop</artifactId>
		</dependency>
```

2. 定义一个切面类，用于在方法前后进行记录日志和事务的操作

```
@Aspect
@Component
public class TransactionAspect {
    /**
     * 切入点
     * execution表达式匹配org.boot.aop.service包下所有类的所有方法，包括任意参数
     */
    @Pointcut("execution(* org.boot.aop.service..*(..))")
    public void pointcut() {
    }

    /**
     * 前置通知
     */
    @Before("pointcut()")
    public void before() {
        System.out.println("前置通知---->开始事务");
    }

    /**
     * 后置通知
     */
    @AfterReturning("pointcut()")
    public void afterReturning() {
        System.out.println("后置通知---->提交事务");
    }

    /**
     * 环绕通知
     *
     * @param joinPoint
     * @throws Throwable
     */
    @Around("pointcut()")
    public void around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("环绕通知---->开始事务");
        joinPoint.proceed();
        System.out.println("环绕通知---->提交事务");
    }
}
```
3. 切面类写好之后，在对应的service包下建一个DataServic，变编写一个测试方法

```
@Service
public class DatabaseService {

    /**
     * 模拟数据库的添加操作
     */
    public void add() {
        System.out.println("执行添加操作...");
    }
}
```
4. 执行

这里需要加上一个EnableAspectJAutoProxy注解，用于开启AOP代理自动配置
```
@SpringBootApplication
@EnableAspectJAutoProxy
public class BootAopApplication implements CommandLineRunner{
    @Resource
    private DatabaseService databaseService;

    public static void main(String[] args) {
        SpringApplication.run(BootAopApplication.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        databaseService.add();
    }
}

```
输出结果如下：

```
环绕通知---->开始事务
前置通知---->记录方法开始日志
执行添加操作...
环绕通知---->提交事务
后置通知---->记录方法结束日志
```
可以看出来在Spring中使用AOP，在简化了我们重复编写事务和日志代码的同时，也大大降低了代码的耦合度，我们的service层中并没有编写任何事务和日志有关的代码，通过动态切入，就完成了这两个功能，如果是日后需要重构，也只需要修改切面类的代码，维护起来也很容易。

#### 原理
Spring的AOP主要实现原理其实就是动态代理，通过代理对目标类的指定方法进行增强处理。Spring主要使用了两种动态代理，一种是JDK动态代理，另一种是Cglib动态代理。Spring默认的策略是JDK动态代理，这时目标类必须是接口或接口的实现类，否则Spring将使用Cglib进行动态代理，上面的例子中，Spring就是通过Cglib为DataService生成的动态代理。
##### JDK动态代理
- JDK动态代理主要涉及到java.lang.reflect包中的Proxy和InvocationHandler两个类。InvocationHandler是一个接口，通过实现该接口定义横切逻辑，并通过反射机制调用目标类的代码，动态将横切逻辑和业务逻辑编织在一起。
- Proxy利用InvocationHandler动态创建一个符合某一接口的实例，生成目标类的增强代理对象。
##### Cglib动态代理
- CGLib全称为Code Generation Library，是一个强大的高性能，高质量的代码生成类库，可以在运行期扩展Java类与实现Java接口，CGLib封装了asm，可以再运行期动态生成新的class。和JDK动态代理相比较：JDK创建代理有一个限制，就是只能为接口创建代理实例，而对于没有通过接口定义业务方法的类，则可以通过CGLib创建动态代理，但是目标类不能为final，因为final修饰的类不允许继承。