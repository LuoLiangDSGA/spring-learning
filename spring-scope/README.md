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
定义成prototype的bean，在每次都会新建一个实例

```java

```

