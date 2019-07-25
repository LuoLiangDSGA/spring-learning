---
title: 编写自己的spring-boot-starter
date: 2019-07-17 16:03:30
tags: 
    - SpringBoot
    - Starter
    - AutoConfiguration
---
## 编写自己的spring-boot-starter

> 如今越来越多的Java应用都开始使用SpringBoot进行构建了，SpringBoot的一大特性就是它的约定大于配置，只需在`pom.xml`中加入对应的starter依赖，即可完成自动配置。比如在传统`Spring`项目中，要集成`SpringMVC`，则需要手动添加前端控制器`DispatcherServlet`，处理器映射器`BeanNameUrlHandlerMapping`，视图解析器`InternalResourceViewResolver`等配置，对于初学者来说并不友好，SpringBoot解决了这些问题，其内部是怎样实现自动配置的，通过自己写一个starter来学习。

### 起步
> 为了便于理解，我们假设要实现一个能自动配置数据库的starter，我开始表演了

- 首先，我们要新建一个`Maven`项目，我直接用`Spring Initializr`生成了，你们随意
> 先把`pom.xml`配置加上，如下

```xml
    <dependencies>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
            <version>${springboot.version}</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-autoconfigure</artifactId>
            <version>${springboot.version}</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <version>${springboot.version}</version>
        </dependency>

    </dependencies>
```

- 编写`xxxProperties.java`
> 这个类的作用就是读取配置，读取`SpringBoot`中`yaml`或者`properties`里面的配置，比如下面贴的这部分代码是`spring-boot-starter-data-mongodb`里面的源码

`MongoProperties.java`

```java
@ConfigurationProperties(
    prefix = "spring.data.mongodb"
)
public class MongoProperties {
    public static final int DEFAULT_PORT = 27017;
    public static final String DEFAULT_URI = "mongodb://localhost/test";
    private String host;
    private Integer port = null;
    private String uri;
    private String database;
    private String authenticationDatabase;
    private String gridFsDatabase;
    private String username;
    private char[] password;
    private Class<?> fieldNamingStrategy;

    public MongoProperties() {
    }
    ...
}

```

看了这个，很简单对吧， 开始写我们自己的配置类，就叫做DataProperties吧，怎么写？复制粘贴就行了，为了不让别人觉得我们在复制粘贴，我们删点字段，同时把配置前缀改改，就是上面的`@ConfigurationProperties`注解里面的prefix属性
`DataProperties.java`
```java
@ConfigurationProperties(prefix = "data")
public class DataProperties {
    public static final String DEFAULT_URI = "localhost:3306";
    public static final String DEFAULT_TYPE = "mysql";
    public static final boolean DEFAULT_ENABLED = false;
    private Boolean enabled;
    private String uri;
    private String type;
    ... 省略getter setter方法   
}
```
这样就搞定了，在下面添加了自动配置之后，配置文件中以data为前缀的配置，会被自动赋值到这个对象对应的字段上，如果没有配置，就会使用默认的值，这就解释了为啥我们添加了`spring-boot-starter-data-mongodb`之后，如果不配任何东西，应用启动时就会默认连接`mongodb://localhost/test`这个地址

- 编写模板类
> `spring-boot-starter-mongo`中有一个`MongoTemplate`，所以我们也编写一个类，用于验证我们的自动配置是否生效

```java
public class MyDataTemplate {
    private Logger logger = LoggerFactory.getLogger(MyDataTemplate.class);
    private String url;
    private String type;

    public MyDataTemplate(String url, String type) {
        this.url = url;
        this.type = type;
    }

    public String getData() {
        logger.info("=========> get data from: ({}), type=({})", url, type);

        return String.format("%s-%s(time：%s)", url, type, LocalDateTime.now());
    }
}
```
    这个类的作用就是打印当前的数据库配置信息

- 编写配置类

`MyDataTemplateAutoConfiguration.java`

```java
@Configuration
@ConditionalOnClass(MyDataTemplate.class)
@EnableConfigurationProperties(DataProperties.class)
public class MyDataTemplateAutoConfiguration {
    @Autowired
    private DataProperties properties;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "data", value = "enabled", havingValue = "true")
    public MyDataTemplate myDataTemplate() {
        return new MyDataTemplate(properties.getUri(), properties.getType());
    }
}
```
    @Configuration：表名这是一个Spring配置
    @ConditionalOnClass(MyDataTemplate.class)：会在classpath中有MyDataTemplate类的时候进行配置
    @EnableConfigurationProperties(DataProperties.class)：将带有@ConfigurationProperties注解的类注入为Spring容器的Bean
    @ConditionalOnMissingBean：当Spring Context中不存在该Bean时注入
    @ConditionalOnProperty(prefix = "data", value = "enabled", havingValue = "true")：当配置文件中的data.enabled为true时注入


- 到了最后一步了，上面的步骤完成之后，SpringBoot怎么知道这个类需要自动配置呢，只需要添加spring.factories就行了
> 在`resources/META-INF/`下创建`spring.factories`文件，添加内容

```
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
my.boot.starter.MyDataTemplateAutoConfiguration
```
    到这里，一个starter就已经开发完成了，这时候只需要mvn:package打包就行了
    
    
- 测试功能

> 我们在其他项目中引入我们编写的starter

```
 <dependency>
    <groupId>my.boot</groupId>
    <artifactId>custom-starter</artifactId>
    <version>0.0.1</version>
</dependency>
```
    这里的命名并没有遵循SpringBoot的规范，只是为了学习，SpringBoot官方的命名是spring-boot-starter-xxx，比如spring-boot-starter-data-mongodb，非官方Starter命名应遵循xxx-spring-boot-starter的格式

> 按照上面starter的写法，我们可以直接使用DataTemplate，因为他已经自动配置并且加入到Spring容器中

```java
    @Autowired
    private MyDataTemplate myDataTemplate;

    @Test
    public void contextLoads() {
    }

    @Test
    public void test() {
        String result = myDataTemplate.getData();
        logger.info("get data result：{}", result);
    }
```

> 在yaml中添加enabled属性，这样会自动注入DataTemplate

```
data:
  enabled: true
```

> 此时我们没有配置uri和type，所以打印结果如下

```
2019-07-25 16:05:31.974  INFO 39174 --- [           main] o.b.mystarter.MyStarterApplicationTests  : get data result：localhost:3306-mysql(time：2019-07-25T16:05:31.974)
```
    可以看到当前打印的结果是我们在DataProperties.java中写的默认值
    
> 添加其他配置

```
data:
  enabled: true
  uri: 172.31.31.189:3306
  type: mysql
```
> 运行之后打印结果如下

```
2019-07-25 16:09:49.942  INFO 39221 --- [           main] o.b.mystarter.MyStarterApplicationTests  : get data result：172.31.31.189:3306-mysql(time：2019-07-25T16:09:49.941)
```
    此时可以看到，打印的结果是我们配置的属性值了
    
### End
> 总结

- SpringBoot在启动时扫描项目所依赖的jar包，寻找包含spring.factories文件的jar包
- 根据spring.factories配置加载AutoConfiguration类
- 根据@Conditional注解的条件，进行自动配置并将Bean注入Spring容器中

**文章到这里就结束了，个人能力有限，文中可能会存在错误的地方，如果有问题，可以在issue中指出，我会及时修正，以免误人子弟。**
