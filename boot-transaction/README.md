# Spring中事务你用对了吗

> 背景

Spring中为JTA，JPA，Hibernate等事务API提供了一致性的编程模型，但是编程式事务需要编码支持，在实际中很少使用。所以Spring提供了声明式事务，
配合SpringBoot，我们可以通过@Transactional注解，轻松地实现事务的控制，让事务控制达到极简。注解事务固然方便，但是如果对它不够了解，很容易
留下坑，就我目前的项目中，有一些事务根本就没有生效。

## 开始

### 新建工程

> 引入H2 Database

```
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.properties.show_sql=true
spring.jpa.properties.format_sql=true
spring.jpa.properties.use_sql_comments=true
spring.h2.console.enabled=true
spring.h2.console.path=/console
logging.level.org.springframework.orm.jpa=debug
```

都是一些基础的配置，这里使用了jpa，并且把日志级别设置成debug，为了更方便的观察事务的执行情况。

> 编写一个业务类

```java
@Service
public class DataService {

    @Autowired
    private UserRepository userRepository;
   
    private final Logger logger = LoggerFactory.getLogger(DataService.class);

    @Transactional(rollbackFor = RuntimeException.class)
    public void saveA(String user) {
        logger.info("invoke saveA {}", user);
        User u = new User();
        u.setName(user);
        userRepository.save(u);
    }

    public void saveB(String user) {
        logger.info("invoke saveB {}", user);
        try {
            User u = new User();
            u.setName(user);
            this.saveAndRollback(user);
        } catch (RuntimeException e) {
            logger.warn("catch an exception in saveB()");
        }
    }

    public void findAll() {
        logger.info("print data:");
        userRepository.findAll().forEach(user -> logger.info(user.toString()));
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public void saveAndRollback(String user) {
        logger.info("invoke saveAndRollback {}", user);
        User u = new User();
        u.setName(user);
        userRepository.save(u);
        throw new RuntimeException();
    }

}

```

### 验证栗子

```java
@SpringBootApplication
@EnableTransactionManagement
public class Application implements ApplicationRunner {

    @Autowired
    private DataService dataService;
    private final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        demo1();
    }

    private void demo1() {
        dataService.saveA("frank");//1
        dataService.findAll();
        try {
            dataService.saveAndRollback("frank");//2
        } catch (Exception e) {
            logger.warn("catch an save exception");
        }
        dataService.findAll();
        dataService.saveB("jack");//3
        dataService.findAll();
    }
}
```

> 代码1执行结果，上面是insert，下面是查询操作

```
2020-03-31 14:14:14.733 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Creating new transaction with name [org.boot.transaction.service.DataService.saveA]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT,-java.lang.RuntimeException
2020-03-31 14:14:14.733 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Opened new EntityManager [SessionImpl(1955991197<open>)] for JPA transaction
2020-03-31 14:14:14.740 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Exposing JPA transaction as JDBC [org.springframework.orm.jpa.vendor.HibernateJpaDialect$HibernateConnectionHandle@4e4162bc]
2020-03-31 14:14:14.754  INFO 17477 --- [           main] o.boot.transaction.service.DataService   : invoke saveA frank
2020-03-31 14:14:14.758 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Found thread-bound EntityManager [SessionImpl(1955991197<open>)] for JPA transaction
2020-03-31 14:14:14.759 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Participating in existing transaction
2020-03-31 14:14:14.804 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Initiating transaction commit
2020-03-31 14:14:14.805 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Committing JPA transaction on EntityManager [SessionImpl(1955991197<open>)]
2020-03-31 14:14:14.822 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Closing JPA EntityManager [SessionImpl(1955991197<open>)] after transaction
```
```
print data:
2020-03-31 14:14:14.823 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Creating new transaction with name [org.springframework.data.jpa.repository.support.SimpleJpaRepository.findAll]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT,readOnly
2020-03-31 14:14:14.824 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Opened new EntityManager [SessionImpl(2045560071<open>)] for JPA transaction
2020-03-31 14:14:14.841 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Exposing JPA transaction as JDBC [org.springframework.orm.jpa.vendor.HibernateJpaDialect$HibernateConnectionHandle@7e5efcab]
2020-03-31 14:14:15.059 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Initiating transaction commit
2020-03-31 14:14:15.060 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Committing JPA transaction on EntityManager [SessionImpl(2045560071<open>)]
2020-03-31 14:14:15.060 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Closing JPA EntityManager [SessionImpl(2045560071<open>)] after transaction
2020-03-31 14:14:15.061  INFO 17477 --- [           main] o.boot.transaction.service.DataService   : User{id=1, name='frank'}
```
这里可以看到插入成功了

> 代码2执行结果

```
2020-03-31 14:14:15.062  INFO 17477 --- [           main] o.boot.transaction.service.DataService   : invoke saveAndRollback frank
2020-03-31 14:14:15.062 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Found thread-bound EntityManager [SessionImpl(1796154990<open>)] for JPA transaction
2020-03-31 14:14:15.062 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Participating in existing transaction
2020-03-31 14:14:15.063 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Initiating transaction rollback
2020-03-31 14:14:15.063 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Rolling back JPA transaction on EntityManager [SessionImpl(1796154990<open>)]
2020-03-31 14:14:15.067 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Closing JPA EntityManager [SessionImpl(1796154990<open>)] after transaction
2020-03-31 14:14:15.068  WARN 17477 --- [           main] org.boot.transaction.Application         : catch an save exception
2020-03-31 14:14:15.068  INFO 17477 --- [           main] o.boot.transaction.service.DataService   : print data:
2020-03-31 14:14:15.068 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Creating new transaction with name [org.springframework.data.jpa.repository.support.SimpleJpaRepository.findAll]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT,readOnly
2020-03-31 14:14:15.069 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Opened new EntityManager [SessionImpl(1138190994<open>)] for JPA transaction
2020-03-31 14:14:15.069 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Exposing JPA transaction as JDBC [org.springframework.orm.jpa.vendor.HibernateJpaDialect$HibernateConnectionHandle@4a2bf50f]
2020-03-31 14:14:15.076 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Initiating transaction commit
2020-03-31 14:14:15.076 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Committing JPA transaction on EntityManager [SessionImpl(1138190994<open>)]
2020-03-31 14:14:15.076 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Closing JPA EntityManager [SessionImpl(1138190994<open>)] after transaction
2020-03-31 14:14:15.076  INFO 17477 --- [           main] o.boot.transaction.service.DataService   : User{id=1, name='frank'}
```
从这里的结果可以看出，这时候数据没有插入，还是我们第一次添加的数据，说明事务生效了。

> 代码3执行结果

```
2020-03-31 14:14:15.076  INFO 17477 --- [           main] o.boot.transaction.service.DataService   : invoke saveB jack
2020-03-31 14:14:15.076  INFO 17477 --- [           main] o.boot.transaction.service.DataService   : invoke saveAndRollback jack
2020-03-31 14:14:15.077 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Creating new transaction with name [org.springframework.data.jpa.repository.support.SimpleJpaRepository.save]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
2020-03-31 14:14:15.077 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Opened new EntityManager [SessionImpl(1683617002<open>)] for JPA transaction
2020-03-31 14:14:15.077 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Exposing JPA transaction as JDBC [org.springframework.orm.jpa.vendor.HibernateJpaDialect$HibernateConnectionHandle@740b9a50]
2020-03-31 14:14:15.077 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Initiating transaction commit
2020-03-31 14:14:15.077 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Committing JPA transaction on EntityManager [SessionImpl(1683617002<open>)]
2020-03-31 14:14:15.078 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Closing JPA EntityManager [SessionImpl(1683617002<open>)] after transaction
2020-03-31 14:14:15.078  WARN 17477 --- [           main] o.boot.transaction.service.DataService   : catch an exception in saveB()
2020-03-31 14:14:15.078  INFO 17477 --- [           main] o.boot.transaction.service.DataService   : print data:
2020-03-31 14:14:15.078 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Creating new transaction with name [org.springframework.data.jpa.repository.support.SimpleJpaRepository.findAll]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT,readOnly
2020-03-31 14:14:15.079 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Opened new EntityManager [SessionImpl(589094312<open>)] for JPA transaction
2020-03-31 14:14:15.079 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Exposing JPA transaction as JDBC [org.springframework.orm.jpa.vendor.HibernateJpaDialect$HibernateConnectionHandle@6b70d1fb]
2020-03-31 14:14:15.080 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Initiating transaction commit
2020-03-31 14:14:15.080 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Committing JPA transaction on EntityManager [SessionImpl(589094312<open>)]
2020-03-31 14:14:15.081 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Closing JPA EntityManager [SessionImpl(589094312<open>)] after transaction
2020-03-31 14:14:15.081  INFO 17477 --- [           main] o.boot.transaction.service.DataService   : User{id=1, name='frank'}
2020-03-31 14:14:15.081  INFO 17477 --- [           main] o.boot.transaction.service.DataService   : User{id=3, name='jack'}
```

这里看到最后的查询结果有两条数据，说明这里虽然产生了异常，但是数据没有回滚，说明事务没有生效。

### 分析原因

从代码层面上看，代码2和代码3的区别是：代码2是直接调用的带@Transactional注解的方法（saveAndRollback），而代码3调用的方法（saveB）没有@Transactional注解，在saveB中调用了saveAndRollback方法，这属于内部调用，也就是通过this去调用对象的方法。而Spring的事务是通过AOP实现的，AOP会在加了事务注解的方法上进行增强，而Spring实现AOP主要是通过动态代理的方式，所以Spring做事务增强是在代理类上面做的增强，而我们用this去调用原来的方法，是没有做增强的，所以事务也就不会生效。

> 解决方法也很简单，只有被AOP增强过的类事务才会生效，有三种：

1. 注入DataService本身调用
2. 通过ApplicationContext拿到bean之后调用
3. 使用AopContext获取到代理类调用

所以我们在DataService中增加如下方法：

```java
@Transactional(rollbackFor = RuntimeException.class)
public void saveAndRollback(String user) {
    logger.info("invoke saveAndRollback {}", user);
    User u = new User();
    u.setName(user);
    userRepository.save(u);
    throw new RuntimeException();
}

public void invokeSelf(String user) {
    try {
        dataService.saveAndRollback(user);
    } catch (RuntimeException e) {
        logger.warn("catch an exception in invokeSelf()");
    }
}

public void invokeWithApplicationContext(String user) {
    try {
        ((DataService) applicationContext.getBean("dataService")).saveAndRollback(user);
    } catch (RuntimeException e) {
        logger.warn("catch an exception in invokeWithApplicationContext()");
    }
}

public void invokeWithAop(String user) {
    try {
        // 需要把@EnableAspectJAutoProxy注解中的(exposeProxy = true)
        ((DataService) AopContext.currentProxy()).saveAndRollback(user);
    } catch (RuntimeException e) {
        logger.warn("catch an exception in invokeWithAop()");
    }
}
```

> 再来运行看看结果

```java
dataService.saveA("frank");
dataService.invokeSelf("frank");
dataService.findAll();
dataService.invokeWithApplicationContext("tom");
dataService.findAll();
dataService.invokeWithAop("jack");
dataService.findAll();
```

可以看到日志中，出现异常的地方出现了rollback字样，说明事务都生效了

```
2020-03-31 18:02:09.622 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Opened new EntityManager [SessionImpl(1779787990<open>)] for JPA transaction
2020-03-31 18:02:09.645 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Exposing JPA transaction as JDBC [org.springframework.orm.jpa.vendor.HibernateJpaDialect$HibernateConnectionHandle@411fa0ce]
2020-03-31 18:02:10.011 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Initiating transaction commit
2020-03-31 18:02:10.012 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Committing JPA transaction on EntityManager [SessionImpl(1779787990<open>)]
2020-03-31 18:02:10.012 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Closing JPA EntityManager [SessionImpl(1779787990<open>)] after transaction
2020-03-31 18:02:10.013  INFO 20635 --- [           main] o.boot.transaction.service.DataService   : User{id=1, name='frank'}
2020-03-31 18:02:10.013 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Creating new transaction with name [org.boot.transaction.service.DataService.saveAndRollback]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT,-java.lang.RuntimeException
2020-03-31 18:02:10.013 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Opened new EntityManager [SessionImpl(1478269879<open>)] for JPA transaction
2020-03-31 18:02:10.013 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Exposing JPA transaction as JDBC [org.springframework.orm.jpa.vendor.HibernateJpaDialect$HibernateConnectionHandle@138f0661]
2020-03-31 18:02:10.014  INFO 20635 --- [           main] o.boot.transaction.service.DataService   : invoke saveAndRollback tom
2020-03-31 18:02:10.014 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Found thread-bound EntityManager [SessionImpl(1478269879<open>)] for JPA transaction
2020-03-31 18:02:10.014 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Participating in existing transaction
2020-03-31 18:02:10.015 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Initiating transaction rollback
2020-03-31 18:02:10.016 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Rolling back JPA transaction on EntityManager [SessionImpl(1478269879<open>)]
2020-03-31 18:02:10.018 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Closing JPA EntityManager [SessionImpl(1478269879<open>)] after transaction
2020-03-31 18:02:10.018  WARN 20635 --- [           main] o.boot.transaction.service.DataService   : catch an exception in invokeWithApplicationContext()
2020-03-31 18:02:10.018  INFO 20635 --- [           main] o.boot.transaction.service.DataService   : print data:
2020-03-31 18:02:10.018 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Creating new transaction with name [org.springframework.data.jpa.repository.support.SimpleJpaRepository.findAll]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT,readOnly
2020-03-31 18:02:10.019 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Opened new EntityManager [SessionImpl(321795476<open>)] for JPA transaction
2020-03-31 18:02:10.019 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Exposing JPA transaction as JDBC [org.springframework.orm.jpa.vendor.HibernateJpaDialect$HibernateConnectionHandle@4f235107]
2020-03-31 18:02:10.020 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Initiating transaction commit
2020-03-31 18:02:10.020 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Committing JPA transaction on EntityManager [SessionImpl(321795476<open>)]
2020-03-31 18:02:10.020 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Closing JPA EntityManager [SessionImpl(321795476<open>)] after transaction
2020-03-31 18:02:10.020  INFO 20635 --- [           main] o.boot.transaction.service.DataService   : User{id=1, name='frank'}
2020-03-31 18:02:10.020 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Creating new transaction with name [org.boot.transaction.service.DataService.saveAndRollback]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT,-java.lang.RuntimeException
2020-03-31 18:02:10.020 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Opened new EntityManager [SessionImpl(977952572<open>)] for JPA transaction
2020-03-31 18:02:10.021 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Exposing JPA transaction as JDBC [org.springframework.orm.jpa.vendor.HibernateJpaDialect$HibernateConnectionHandle@4f3356c0]
2020-03-31 18:02:10.021  INFO 20635 --- [           main] o.boot.transaction.service.DataService   : invoke saveAndRollback jack
2020-03-31 18:02:10.021 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Found thread-bound EntityManager [SessionImpl(977952572<open>)] for JPA transaction
2020-03-31 18:02:10.021 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Participating in existing transaction
2020-03-31 18:02:10.022 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Initiating transaction rollback
2020-03-31 18:02:10.022 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Rolling back JPA transaction on EntityManager [SessionImpl(977952572<open>)]
2020-03-31 18:02:10.023 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Closing JPA EntityManager [SessionImpl(977952572<open>)] after transaction
2020-03-31 18:02:10.023  WARN 20635 --- [           main] o.boot.transaction.service.DataService   : catch an exception in invokeWithAop()
2020-03-31 18:02:10.023  INFO 20635 --- [           main] o.boot.transaction.service.DataService   : print data:
2020-03-31 18:02:10.023 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Creating new transaction with name [org.springframework.data.jpa.repository.support.SimpleJpaRepository.findAll]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT,readOnly
2020-03-31 18:02:10.023 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Opened new EntityManager [SessionImpl(1367900185<open>)] for JPA transaction
2020-03-31 18:02:10.023 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Exposing JPA transaction as JDBC [org.springframework.orm.jpa.vendor.HibernateJpaDialect$HibernateConnectionHandle@6f50d55c]
2020-03-31 18:02:10.024 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Initiating transaction commit
2020-03-31 18:02:10.024 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Committing JPA transaction on EntityManager [SessionImpl(1367900185<open>)]
2020-03-31 18:02:10.025 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Closing JPA EntityManager [SessionImpl(1367900185<open>)] after transaction
2020-03-31 18:02:10.025  INFO 20635 --- [           main] o.boot.transaction.service.DataService   : User{id=1, name='frank'}
```

### 小结

在 Spring 的 AOP 代理下，只有目标方法由外部调用，目标方法才由 Spring 生成的代理对象来管理，这会造成自调用问题。若同一类中的其他没有@Transactional 注解的方法内部调用有@Transactional 注解的方法，有@Transactional 注解的方法的事务被忽略，不会发生回滚。解决方法上面已经列出，并且放在了![github](https://github.com/LuoLiangDSGA/spring-learning/tree/master/boot-transaction)上。所以平时在内部调用带有事务的方法时，要小心一点。

> 还有以下常见场景事务也会失效

1. @Transactional注解到非public方法上
2. 如果在事务中抛出了未检查异常（继承自 RuntimeException的异常，也就是说如果有IO操作，抛出了IOException，事务是不会回滚的）或者Error，则 Spring 将回滚事务；除此之外，Spring 不会回滚事务。注意，这里说的是需要抛出，如果没有抛出，比如异常被catch吞了，事务是不会回滚的。# Spring中事务你用对了吗
                                                                                                                                                                  
                                                                                                                                                                  > 背景
                                                                                                                                                                  
                                                                                                                                                                  Spring中为JTA，JPA，Hibernate等事务API提供了一致性的编程模型，但是编程式事务需要编码支持，在实际中很少使用。所以Spring提供了声明式事务，
                                                                                                                                                                  配合SpringBoot，我们可以通过@Transactional注解，轻松地实现事务的控制，让事务控制达到极简。注解事务固然方便，但是如果对它不够了解，很容易
                                                                                                                                                                  留下坑，就我目前的项目中，有一些事务根本就没有生效。
                                                                                                                                                                  
                                                                                                                                                                  ## 开始
                                                                                                                                                                  
                                                                                                                                                                  ### 新建工程
                                                                                                                                                                  
                                                                                                                                                                  > 引入H2 Database
                                                                                                                                                                  
                                                                                                                                                                  ```
                                                                                                                                                                  spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_ON_EXIT=FALSE
                                                                                                                                                                  spring.datasource.driverClassName=org.h2.Driver
                                                                                                                                                                  spring.datasource.username=root
                                                                                                                                                                  spring.datasource.password=root
                                                                                                                                                                  spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
                                                                                                                                                                  spring.jpa.properties.show_sql=true
                                                                                                                                                                  spring.jpa.properties.format_sql=true
                                                                                                                                                                  spring.jpa.properties.use_sql_comments=true
                                                                                                                                                                  spring.h2.console.enabled=true
                                                                                                                                                                  spring.h2.console.path=/console
                                                                                                                                                                  logging.level.org.springframework.orm.jpa=debug
                                                                                                                                                                  ```
                                                                                                                                                                  
                                                                                                                                                                  都是一些基础的配置，这里使用了jpa，并且把日志级别设置成debug，为了更方便的观察事务的执行情况。
                                                                                                                                                                  
                                                                                                                                                                  > 编写一个业务类
                                                                                                                                                                  
                                                                                                                                                                  ```java
                                                                                                                                                                  @Service
                                                                                                                                                                  public class DataService {
                                                                                                                                                                  
                                                                                                                                                                      @Autowired
                                                                                                                                                                      private UserRepository userRepository;
                                                                                                                                                                     
                                                                                                                                                                      private final Logger logger = LoggerFactory.getLogger(DataService.class);
                                                                                                                                                                  
                                                                                                                                                                      @Transactional(rollbackFor = RuntimeException.class)
                                                                                                                                                                      public void saveA(String user) {
                                                                                                                                                                          logger.info("invoke saveA {}", user);
                                                                                                                                                                          User u = new User();
                                                                                                                                                                          u.setName(user);
                                                                                                                                                                          userRepository.save(u);
                                                                                                                                                                      }
                                                                                                                                                                  
                                                                                                                                                                      public void saveB(String user) {
                                                                                                                                                                          logger.info("invoke saveB {}", user);
                                                                                                                                                                          try {
                                                                                                                                                                              User u = new User();
                                                                                                                                                                              u.setName(user);
                                                                                                                                                                              this.saveAndRollback(user);
                                                                                                                                                                          } catch (RuntimeException e) {
                                                                                                                                                                              logger.warn("catch an exception in saveB()");
                                                                                                                                                                          }
                                                                                                                                                                      }
                                                                                                                                                                  
                                                                                                                                                                      public void findAll() {
                                                                                                                                                                          logger.info("print data:");
                                                                                                                                                                          userRepository.findAll().forEach(user -> logger.info(user.toString()));
                                                                                                                                                                      }
                                                                                                                                                                  
                                                                                                                                                                      @Transactional(rollbackFor = RuntimeException.class)
                                                                                                                                                                      public void saveAndRollback(String user) {
                                                                                                                                                                          logger.info("invoke saveAndRollback {}", user);
                                                                                                                                                                          User u = new User();
                                                                                                                                                                          u.setName(user);
                                                                                                                                                                          userRepository.save(u);
                                                                                                                                                                          throw new RuntimeException();
                                                                                                                                                                      }
                                                                                                                                                                  
                                                                                                                                                                  }
                                                                                                                                                                  
                                                                                                                                                                  ```
                                                                                                                                                                  
                                                                                                                                                                  ### 验证栗子
                                                                                                                                                                  
                                                                                                                                                                  ```java
                                                                                                                                                                  @SpringBootApplication
                                                                                                                                                                  @EnableTransactionManagement
                                                                                                                                                                  public class Application implements ApplicationRunner {
                                                                                                                                                                  
                                                                                                                                                                      @Autowired
                                                                                                                                                                      private DataService dataService;
                                                                                                                                                                      private final Logger logger = LoggerFactory.getLogger(Application.class);
                                                                                                                                                                  
                                                                                                                                                                      public static void main(String[] args) {
                                                                                                                                                                          SpringApplication.run(Application.class, args);
                                                                                                                                                                      }
                                                                                                                                                                  
                                                                                                                                                                      @Override
                                                                                                                                                                      public void run(ApplicationArguments args) throws Exception {
                                                                                                                                                                          demo1();
                                                                                                                                                                      }
                                                                                                                                                                  
                                                                                                                                                                      private void demo1() {
                                                                                                                                                                          dataService.saveA("frank");//1
                                                                                                                                                                          dataService.findAll();
                                                                                                                                                                          try {
                                                                                                                                                                              dataService.saveAndRollback("frank");//2
                                                                                                                                                                          } catch (Exception e) {
                                                                                                                                                                              logger.warn("catch an save exception");
                                                                                                                                                                          }
                                                                                                                                                                          dataService.findAll();
                                                                                                                                                                          dataService.saveB("jack");//3
                                                                                                                                                                          dataService.findAll();
                                                                                                                                                                      }
                                                                                                                                                                  }
                                                                                                                                                                  ```
                                                                                                                                                                  
                                                                                                                                                                  > 代码1执行结果，上面是insert，下面是查询操作
                                                                                                                                                                  
                                                                                                                                                                  ```
                                                                                                                                                                  2020-03-31 14:14:14.733 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Creating new transaction with name [org.boot.transaction.service.DataService.saveA]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT,-java.lang.RuntimeException
                                                                                                                                                                  2020-03-31 14:14:14.733 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Opened new EntityManager [SessionImpl(1955991197<open>)] for JPA transaction
                                                                                                                                                                  2020-03-31 14:14:14.740 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Exposing JPA transaction as JDBC [org.springframework.orm.jpa.vendor.HibernateJpaDialect$HibernateConnectionHandle@4e4162bc]
                                                                                                                                                                  2020-03-31 14:14:14.754  INFO 17477 --- [           main] o.boot.transaction.service.DataService   : invoke saveA frank
                                                                                                                                                                  2020-03-31 14:14:14.758 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Found thread-bound EntityManager [SessionImpl(1955991197<open>)] for JPA transaction
                                                                                                                                                                  2020-03-31 14:14:14.759 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Participating in existing transaction
                                                                                                                                                                  2020-03-31 14:14:14.804 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Initiating transaction commit
                                                                                                                                                                  2020-03-31 14:14:14.805 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Committing JPA transaction on EntityManager [SessionImpl(1955991197<open>)]
                                                                                                                                                                  2020-03-31 14:14:14.822 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Closing JPA EntityManager [SessionImpl(1955991197<open>)] after transaction
                                                                                                                                                                  ```
                                                                                                                                                                  ```
                                                                                                                                                                  print data:
                                                                                                                                                                  2020-03-31 14:14:14.823 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Creating new transaction with name [org.springframework.data.jpa.repository.support.SimpleJpaRepository.findAll]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT,readOnly
                                                                                                                                                                  2020-03-31 14:14:14.824 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Opened new EntityManager [SessionImpl(2045560071<open>)] for JPA transaction
                                                                                                                                                                  2020-03-31 14:14:14.841 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Exposing JPA transaction as JDBC [org.springframework.orm.jpa.vendor.HibernateJpaDialect$HibernateConnectionHandle@7e5efcab]
                                                                                                                                                                  2020-03-31 14:14:15.059 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Initiating transaction commit
                                                                                                                                                                  2020-03-31 14:14:15.060 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Committing JPA transaction on EntityManager [SessionImpl(2045560071<open>)]
                                                                                                                                                                  2020-03-31 14:14:15.060 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Closing JPA EntityManager [SessionImpl(2045560071<open>)] after transaction
                                                                                                                                                                  2020-03-31 14:14:15.061  INFO 17477 --- [           main] o.boot.transaction.service.DataService   : User{id=1, name='frank'}
                                                                                                                                                                  ```
                                                                                                                                                                  这里可以看到插入成功了
                                                                                                                                                                  
                                                                                                                                                                  > 代码2执行结果
                                                                                                                                                                  
                                                                                                                                                                  ```
                                                                                                                                                                  2020-03-31 14:14:15.062  INFO 17477 --- [           main] o.boot.transaction.service.DataService   : invoke saveAndRollback frank
                                                                                                                                                                  2020-03-31 14:14:15.062 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Found thread-bound EntityManager [SessionImpl(1796154990<open>)] for JPA transaction
                                                                                                                                                                  2020-03-31 14:14:15.062 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Participating in existing transaction
                                                                                                                                                                  2020-03-31 14:14:15.063 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Initiating transaction rollback
                                                                                                                                                                  2020-03-31 14:14:15.063 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Rolling back JPA transaction on EntityManager [SessionImpl(1796154990<open>)]
                                                                                                                                                                  2020-03-31 14:14:15.067 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Closing JPA EntityManager [SessionImpl(1796154990<open>)] after transaction
                                                                                                                                                                  2020-03-31 14:14:15.068  WARN 17477 --- [           main] org.boot.transaction.Application         : catch an save exception
                                                                                                                                                                  2020-03-31 14:14:15.068  INFO 17477 --- [           main] o.boot.transaction.service.DataService   : print data:
                                                                                                                                                                  2020-03-31 14:14:15.068 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Creating new transaction with name [org.springframework.data.jpa.repository.support.SimpleJpaRepository.findAll]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT,readOnly
                                                                                                                                                                  2020-03-31 14:14:15.069 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Opened new EntityManager [SessionImpl(1138190994<open>)] for JPA transaction
                                                                                                                                                                  2020-03-31 14:14:15.069 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Exposing JPA transaction as JDBC [org.springframework.orm.jpa.vendor.HibernateJpaDialect$HibernateConnectionHandle@4a2bf50f]
                                                                                                                                                                  2020-03-31 14:14:15.076 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Initiating transaction commit
                                                                                                                                                                  2020-03-31 14:14:15.076 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Committing JPA transaction on EntityManager [SessionImpl(1138190994<open>)]
                                                                                                                                                                  2020-03-31 14:14:15.076 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Closing JPA EntityManager [SessionImpl(1138190994<open>)] after transaction
                                                                                                                                                                  2020-03-31 14:14:15.076  INFO 17477 --- [           main] o.boot.transaction.service.DataService   : User{id=1, name='frank'}
                                                                                                                                                                  ```
                                                                                                                                                                  从这里的结果可以看出，这时候数据没有插入，还是我们第一次添加的数据，说明事务生效了。
                                                                                                                                                                  
                                                                                                                                                                  > 代码3执行结果
                                                                                                                                                                  
                                                                                                                                                                  ```
                                                                                                                                                                  2020-03-31 14:14:15.076  INFO 17477 --- [           main] o.boot.transaction.service.DataService   : invoke saveB jack
                                                                                                                                                                  2020-03-31 14:14:15.076  INFO 17477 --- [           main] o.boot.transaction.service.DataService   : invoke saveAndRollback jack
                                                                                                                                                                  2020-03-31 14:14:15.077 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Creating new transaction with name [org.springframework.data.jpa.repository.support.SimpleJpaRepository.save]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
                                                                                                                                                                  2020-03-31 14:14:15.077 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Opened new EntityManager [SessionImpl(1683617002<open>)] for JPA transaction
                                                                                                                                                                  2020-03-31 14:14:15.077 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Exposing JPA transaction as JDBC [org.springframework.orm.jpa.vendor.HibernateJpaDialect$HibernateConnectionHandle@740b9a50]
                                                                                                                                                                  2020-03-31 14:14:15.077 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Initiating transaction commit
                                                                                                                                                                  2020-03-31 14:14:15.077 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Committing JPA transaction on EntityManager [SessionImpl(1683617002<open>)]
                                                                                                                                                                  2020-03-31 14:14:15.078 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Closing JPA EntityManager [SessionImpl(1683617002<open>)] after transaction
                                                                                                                                                                  2020-03-31 14:14:15.078  WARN 17477 --- [           main] o.boot.transaction.service.DataService   : catch an exception in saveB()
                                                                                                                                                                  2020-03-31 14:14:15.078  INFO 17477 --- [           main] o.boot.transaction.service.DataService   : print data:
                                                                                                                                                                  2020-03-31 14:14:15.078 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Creating new transaction with name [org.springframework.data.jpa.repository.support.SimpleJpaRepository.findAll]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT,readOnly
                                                                                                                                                                  2020-03-31 14:14:15.079 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Opened new EntityManager [SessionImpl(589094312<open>)] for JPA transaction
                                                                                                                                                                  2020-03-31 14:14:15.079 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Exposing JPA transaction as JDBC [org.springframework.orm.jpa.vendor.HibernateJpaDialect$HibernateConnectionHandle@6b70d1fb]
                                                                                                                                                                  2020-03-31 14:14:15.080 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Initiating transaction commit
                                                                                                                                                                  2020-03-31 14:14:15.080 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Committing JPA transaction on EntityManager [SessionImpl(589094312<open>)]
                                                                                                                                                                  2020-03-31 14:14:15.081 DEBUG 17477 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Closing JPA EntityManager [SessionImpl(589094312<open>)] after transaction
                                                                                                                                                                  2020-03-31 14:14:15.081  INFO 17477 --- [           main] o.boot.transaction.service.DataService   : User{id=1, name='frank'}
                                                                                                                                                                  2020-03-31 14:14:15.081  INFO 17477 --- [           main] o.boot.transaction.service.DataService   : User{id=3, name='jack'}
                                                                                                                                                                  ```
                                                                                                                                                                  
                                                                                                                                                                  这里看到最后的查询结果有两条数据，说明这里虽然产生了异常，但是数据没有回滚，说明事务没有生效。
                                                                                                                                                                  
                                                                                                                                                                  ### 分析原因
                                                                                                                                                                  
                                                                                                                                                                  从代码层面上看，代码2和代码3的区别是：代码2是直接调用的带@Transactional注解的方法（saveAndRollback），而代码3调用的方法（saveB）没有@Transactional注解，在saveB中调用了saveAndRollback方法，这属于内部调用，也就是通过this去调用对象的方法。而Spring的事务是通过AOP实现的，AOP会在加了事务注解的方法上进行增强，而Spring实现AOP主要是通过动态代理的方式，所以Spring做事务增强是在代理类上面做的增强，而我们用this去调用原来的方法，是没有做增强的，所以事务也就不会生效。
                                                                                                                                                                  
                                                                                                                                                                  > 解决方法也很简单，只有被AOP增强过的类事务才会生效，有三种：
                                                                                                                                                                  
                                                                                                                                                                  1. 注入DataService本身调用
                                                                                                                                                                  2. 通过ApplicationContext拿到bean之后调用
                                                                                                                                                                  3. 使用AopContext获取到代理类调用
                                                                                                                                                                  
                                                                                                                                                                  所以我们在DataService中增加如下方法：
                                                                                                                                                                  
                                                                                                                                                                  ```java
                                                                                                                                                                  @Transactional(rollbackFor = RuntimeException.class)
                                                                                                                                                                  public void saveAndRollback(String user) {
                                                                                                                                                                      logger.info("invoke saveAndRollback {}", user);
                                                                                                                                                                      User u = new User();
                                                                                                                                                                      u.setName(user);
                                                                                                                                                                      userRepository.save(u);
                                                                                                                                                                      throw new RuntimeException();
                                                                                                                                                                  }
                                                                                                                                                                  
                                                                                                                                                                  public void invokeSelf(String user) {
                                                                                                                                                                      try {
                                                                                                                                                                          dataService.saveAndRollback(user);
                                                                                                                                                                      } catch (RuntimeException e) {
                                                                                                                                                                          logger.warn("catch an exception in invokeSelf()");
                                                                                                                                                                      }
                                                                                                                                                                  }
                                                                                                                                                                  
                                                                                                                                                                  public void invokeWithApplicationContext(String user) {
                                                                                                                                                                      try {
                                                                                                                                                                          ((DataService) applicationContext.getBean("dataService")).saveAndRollback(user);
                                                                                                                                                                      } catch (RuntimeException e) {
                                                                                                                                                                          logger.warn("catch an exception in invokeWithApplicationContext()");
                                                                                                                                                                      }
                                                                                                                                                                  }
                                                                                                                                                                  
                                                                                                                                                                  public void invokeWithAop(String user) {
                                                                                                                                                                      try {
                                                                                                                                                                          // 需要把@EnableAspectJAutoProxy注解中的(exposeProxy = true)
                                                                                                                                                                          ((DataService) AopContext.currentProxy()).saveAndRollback(user);
                                                                                                                                                                      } catch (RuntimeException e) {
                                                                                                                                                                          logger.warn("catch an exception in invokeWithAop()");
                                                                                                                                                                      }
                                                                                                                                                                  }
                                                                                                                                                                  ```
                                                                                                                                                                  
                                                                                                                                                                  > 再来运行看看结果
                                                                                                                                                                  
                                                                                                                                                                  ```java
                                                                                                                                                                  dataService.saveA("frank");
                                                                                                                                                                  dataService.invokeSelf("frank");
                                                                                                                                                                  dataService.findAll();
                                                                                                                                                                  dataService.invokeWithApplicationContext("tom");
                                                                                                                                                                  dataService.findAll();
                                                                                                                                                                  dataService.invokeWithAop("jack");
                                                                                                                                                                  dataService.findAll();
                                                                                                                                                                  ```
                                                                                                                                                                  
                                                                                                                                                                  可以看到日志中，出现异常的地方出现了rollback字样，说明事务都生效了
                                                                                                                                                                  
                                                                                                                                                                  ```
                                                                                                                                                                  2020-03-31 18:02:09.622 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Opened new EntityManager [SessionImpl(1779787990<open>)] for JPA transaction
                                                                                                                                                                  2020-03-31 18:02:09.645 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Exposing JPA transaction as JDBC [org.springframework.orm.jpa.vendor.HibernateJpaDialect$HibernateConnectionHandle@411fa0ce]
                                                                                                                                                                  2020-03-31 18:02:10.011 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Initiating transaction commit
                                                                                                                                                                  2020-03-31 18:02:10.012 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Committing JPA transaction on EntityManager [SessionImpl(1779787990<open>)]
                                                                                                                                                                  2020-03-31 18:02:10.012 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Closing JPA EntityManager [SessionImpl(1779787990<open>)] after transaction
                                                                                                                                                                  2020-03-31 18:02:10.013  INFO 20635 --- [           main] o.boot.transaction.service.DataService   : User{id=1, name='frank'}
                                                                                                                                                                  2020-03-31 18:02:10.013 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Creating new transaction with name [org.boot.transaction.service.DataService.saveAndRollback]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT,-java.lang.RuntimeException
                                                                                                                                                                  2020-03-31 18:02:10.013 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Opened new EntityManager [SessionImpl(1478269879<open>)] for JPA transaction
                                                                                                                                                                  2020-03-31 18:02:10.013 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Exposing JPA transaction as JDBC [org.springframework.orm.jpa.vendor.HibernateJpaDialect$HibernateConnectionHandle@138f0661]
                                                                                                                                                                  2020-03-31 18:02:10.014  INFO 20635 --- [           main] o.boot.transaction.service.DataService   : invoke saveAndRollback tom
                                                                                                                                                                  2020-03-31 18:02:10.014 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Found thread-bound EntityManager [SessionImpl(1478269879<open>)] for JPA transaction
                                                                                                                                                                  2020-03-31 18:02:10.014 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Participating in existing transaction
                                                                                                                                                                  2020-03-31 18:02:10.015 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Initiating transaction rollback
                                                                                                                                                                  2020-03-31 18:02:10.016 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Rolling back JPA transaction on EntityManager [SessionImpl(1478269879<open>)]
                                                                                                                                                                  2020-03-31 18:02:10.018 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Closing JPA EntityManager [SessionImpl(1478269879<open>)] after transaction
                                                                                                                                                                  2020-03-31 18:02:10.018  WARN 20635 --- [           main] o.boot.transaction.service.DataService   : catch an exception in invokeWithApplicationContext()
                                                                                                                                                                  2020-03-31 18:02:10.018  INFO 20635 --- [           main] o.boot.transaction.service.DataService   : print data:
                                                                                                                                                                  2020-03-31 18:02:10.018 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Creating new transaction with name [org.springframework.data.jpa.repository.support.SimpleJpaRepository.findAll]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT,readOnly
                                                                                                                                                                  2020-03-31 18:02:10.019 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Opened new EntityManager [SessionImpl(321795476<open>)] for JPA transaction
                                                                                                                                                                  2020-03-31 18:02:10.019 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Exposing JPA transaction as JDBC [org.springframework.orm.jpa.vendor.HibernateJpaDialect$HibernateConnectionHandle@4f235107]
                                                                                                                                                                  2020-03-31 18:02:10.020 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Initiating transaction commit
                                                                                                                                                                  2020-03-31 18:02:10.020 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Committing JPA transaction on EntityManager [SessionImpl(321795476<open>)]
                                                                                                                                                                  2020-03-31 18:02:10.020 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Closing JPA EntityManager [SessionImpl(321795476<open>)] after transaction
                                                                                                                                                                  2020-03-31 18:02:10.020  INFO 20635 --- [           main] o.boot.transaction.service.DataService   : User{id=1, name='frank'}
                                                                                                                                                                  2020-03-31 18:02:10.020 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Creating new transaction with name [org.boot.transaction.service.DataService.saveAndRollback]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT,-java.lang.RuntimeException
                                                                                                                                                                  2020-03-31 18:02:10.020 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Opened new EntityManager [SessionImpl(977952572<open>)] for JPA transaction
                                                                                                                                                                  2020-03-31 18:02:10.021 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Exposing JPA transaction as JDBC [org.springframework.orm.jpa.vendor.HibernateJpaDialect$HibernateConnectionHandle@4f3356c0]
                                                                                                                                                                  2020-03-31 18:02:10.021  INFO 20635 --- [           main] o.boot.transaction.service.DataService   : invoke saveAndRollback jack
                                                                                                                                                                  2020-03-31 18:02:10.021 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Found thread-bound EntityManager [SessionImpl(977952572<open>)] for JPA transaction
                                                                                                                                                                  2020-03-31 18:02:10.021 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Participating in existing transaction
                                                                                                                                                                  2020-03-31 18:02:10.022 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Initiating transaction rollback
                                                                                                                                                                  2020-03-31 18:02:10.022 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Rolling back JPA transaction on EntityManager [SessionImpl(977952572<open>)]
                                                                                                                                                                  2020-03-31 18:02:10.023 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Closing JPA EntityManager [SessionImpl(977952572<open>)] after transaction
                                                                                                                                                                  2020-03-31 18:02:10.023  WARN 20635 --- [           main] o.boot.transaction.service.DataService   : catch an exception in invokeWithAop()
                                                                                                                                                                  2020-03-31 18:02:10.023  INFO 20635 --- [           main] o.boot.transaction.service.DataService   : print data:
                                                                                                                                                                  2020-03-31 18:02:10.023 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Creating new transaction with name [org.springframework.data.jpa.repository.support.SimpleJpaRepository.findAll]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT,readOnly
                                                                                                                                                                  2020-03-31 18:02:10.023 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Opened new EntityManager [SessionImpl(1367900185<open>)] for JPA transaction
                                                                                                                                                                  2020-03-31 18:02:10.023 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Exposing JPA transaction as JDBC [org.springframework.orm.jpa.vendor.HibernateJpaDialect$HibernateConnectionHandle@6f50d55c]
                                                                                                                                                                  2020-03-31 18:02:10.024 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Initiating transaction commit
                                                                                                                                                                  2020-03-31 18:02:10.024 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Committing JPA transaction on EntityManager [SessionImpl(1367900185<open>)]
                                                                                                                                                                  2020-03-31 18:02:10.025 DEBUG 20635 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Closing JPA EntityManager [SessionImpl(1367900185<open>)] after transaction
                                                                                                                                                                  2020-03-31 18:02:10.025  INFO 20635 --- [           main] o.boot.transaction.service.DataService   : User{id=1, name='frank'}
                                                                                                                                                                  ```
                                                                                                                                                                  
                                                                                                                                                                  ### 小结
                                                                                                                                                                  
                                                                                                                                                                  在 Spring 的 AOP 代理下，只有目标方法由外部调用，目标方法才由 Spring 生成的代理对象来管理，这会造成自调用问题。若同一类中的其他没有@Transactional 注解的方法内部调用有@Transactional 注解的方法，有@Transactional 注解的方法的事务被忽略，不会发生回滚。解决方法上面已经列出，并且放在了[github](https://github.com/LuoLiangDSGA/spring-learning/tree/master/boot-transaction)上。所以平时在内部调用带有事务的方法时，要小心一点。
                                                                                                                                                                  
                                                                                                                                                                  > 还有以下常见场景事务也会失效
                                                                                                                                                                  
                                                                                                                                                                  1. @Transactional注解到非public方法上
                                                                                                                                                                  2. 如果在事务中抛出了未检查异常（继承自 RuntimeException的异常，也就是说如果有IO操作，抛出了IOException，事务是不会回滚的）或者Error，则 Spring 将回滚事务；除此之外，Spring 不会回滚事务。注意，这里说的是需要抛出，如果没有抛出，比如异常被catch吞了，事务是不会回滚的。