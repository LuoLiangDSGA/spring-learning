## SpringBoot集成Mybatis实战

> mybatis是一款优秀的持久层框架，支持定制化SQL，存储过程和高级映射。MyBatis 避免了几乎所有的 JDBC 代码和手动设置参数以及获取结果集。MyBatis 可以使用简单的 XML 或注解来配置和映射原生信息，将接口和 Java 的 POJOs(Plain Old Java Objects,普通的 Java对象)映射成数据库中的记录。

### 起步

SpringBoot可以通过`MyBatis-Spring-Boot-Starter`，快速集成Mybatis，只需在maven中引入依赖

```java
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>1.3.2</version>
</dependency>
```

