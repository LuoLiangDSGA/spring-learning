## Spring中的Scope
Scope用于描述Spring容器如何新建Bean实例

### 概述
Spring框架中提供了多种不同类型的Bean scope，这些Scope定义了bean在其使用的上下文中的生命周期和可见性。当前版本的Spring中定义了如下6种不同类型的Scope:
- singleton
> bean会被限制在每一个Spring IOC容器中只有一个实例，Spring默认配置即为singleton
- prototype
> 每次调用都会新建一个Bean实例

下面四种类型只能在web应用程序中使用

- request
> Web项目中，给每一个http request新建一个Bean实例
- session
> Web项目中，给每一个http session新建一个Bean实例 
- application
> Web项目中，会在整个ServletContext的生命周期中新建一个Bean实例
- websocket
> 首次访问时，会新建一个Bean实例存储在WebSocket会话属性中，每当在WebSocket会话期间访问Bean时，都会返回该Bean的相同实例

### example
> singleton scope
定义成singleton的bean会被限制在每一个Spring IOC容器中只有一个实例，Spring默认配置即为singleton

创建一个`Person`实体，来验证作用域的功能
```java
public class Person {
    private String name;

    public Person() {
    }

    public Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

使用`@Scope`注解来标识Bean的作用域
```java
@Bean
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public Person personSingleton() {
    return new Person();
}
```

编写一个单元测试，来测试引用同一个Bean的两个对象是否相同
```java
@Test
public void singletonScopeTest() {
    ApplicationContext applicationContext = new AnnotationConfigApplicationContext(BeanConfig.class);

    Person personA = (Person) applicationContext.getBean("personSingleton");
    Person personB = (Person) applicationContext.getBean("personSingleton");
    personA.setName("Thor");

    Assert.assertEquals(personA, personB);
}
```
运行测试用例通过，说明即使改变其中一个对象的状态，两个对象仍然引用同一个Bean实例

> prototype scope
定义成prototype的bean，在每次都会新建一个实例，只需要把`@Scope`注解的value值设置为Prototype

```java
@Bean
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public Person personPrototype() {
    return new Person();
}
```

同样，编写一个测试用例来进行测试
```java
@Test
public void prototypeScopeTest() {
    ApplicationContext applicationContext = new AnnotationConfigApplicationContext(BeanConfig.class);

    Person personA = (Person) applicationContext.getBean("personPrototype");
    Person personB = (Person) applicationContext.getBean("personPrototype");

    Assert.assertEquals(personA, personB);
}
```
运行测试，可以看到两个对象没有引用同一个Bean

前面提到，有四种只能在web应用程序中使用的scope，WebSocket的用得较少，所以只列出前三种
> request scope

在request scope下，每一个http请求都会创建一个bean实例

编写一个`HelloMessageGenerator`类用于实例化bean
```java
public class HelloMessageGenerator {
    public String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
```
接下来，在`@Scope`注解中，把value值声明为request，代码如下

```java
@Bean
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public HelloMessageGenerator requestScopedBean() {
    return new HelloMessageGenerator();
}
```
`@Scope`注解中声明proxyMode属性是非常有必要的，因为在web应用程序上下文初始化的过程中，这时候没有有效的请求，声明为`TARGET_CLASS`，Spring将会创建一个代理作为依赖注入，在请求的时候实例化Bean

编写一个controller，注入`requestScopedBean`，用于测试Bean的scope
```java
@RestController
public class ScopesController {
    private static final Logger logger = LoggerFactory.getLogger(ScopesController.class);
    @Resource(name = "requestScopedBean")
    private HelloMessageGenerator requestScopedBean;

    @RequestMapping("/scopes/request")
    public String getRequestScopeMessage() {
        logger.debug("previousMessage：{}", requestScopedBean.getMessage());
        requestScopedBean.setMessage("Good Morning!");
        logger.debug("currentMessage：{}", requestScopedBean.getMessage());

        return "scopesExample";
    }
}
```

使用Chrome访问`localhost:8081/scopes/request`两次，可以看到，每次输出的日志如下
```java
previousMessage：null
currentMessage：Good Morning!
previousMessage：null
currentMessage：Good Morning!
```
说明Spring为每一个请求都创建了一个Bean实例

> session scope

在`@Scope`注解中，把value值声明为session，Spring会为每一个http session实例化一个Bean
```java
@Bean
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public HelloMessageGenerator sessionScopedBean() {
    return new HelloMessageGenerator();
}
```

同样地，在Controller中注入`sessionScopedBean`
```java
...
@Resource(name = "sessionScopedBean")
private HelloMessageGenerator sessionScopedBean;

@RequestMapping("/scopes/session")
public String getSessionScopeMessage() {
    logger.debug("previousMessage：{}", sessionScopedBean.getMessage());
    sessionScopedBean.setMessage("Good Afternoon!");
    logger.debug("currentMessage：{}", sessionScopedBean.getMessage());

    return "scopesExample";
}
...
```
使用Chrome访问两次这个接口，观察message的变化
```java
previousMessage：null
currentMessage：Good Afternoon!
previousMessage：Good Afternoon!
currentMessage：Good Afternoon!
```
可以看到，在第一次访问的时候message为null，当第二次返回时，message的值已经改变，说明在同一个Session当中的值被保留了下来，整个会话中都返回了相同的Bean实例

> application scope 

```java
@Bean
@Scope(value = WebApplicationContext.SCOPE_APPLICATION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public HelloMessageGenerator applicationScopedBean() {
    return new HelloMessageGenerator();
}
```
后续代码和上面一样，在此处省略，可以使用多个不同的浏览器进行访问，测试是否在整个ServletContext的生命周期都是同一个Bean实例，这其实有点类似于单例模式，但是两者有一个非常重要的区别。当scope为application时，Bean的相同实例会在同一个ServletContext中运行的多个基于Servlet的应用程序之间共享，而scope为singleton仅作用于单个应用程序上下文。


### 结束
本篇学习记录到此结束，如有问题请指出，代码在[github](https://github.com/LuoLiangDSGA/spring-learning/tree/master/spring-scope)上
