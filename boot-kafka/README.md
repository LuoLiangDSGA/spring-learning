### 安装kafka和zookeeper
确保本地已经安装kafka，如果安装之后跳过这一步
这里使用的是[wurstmeister/zookeeper](https://github.com/wurstmeister/zookeeper-docker)和[wurstmeister/kafka](https://github.com/wurstmeister/kafka-docker)

步骤参考这个博客[docker运行kafka](http://blog.csdn.net/snowcity1231/article/details/54946857)

### 集成步骤
在pom中添加以下依赖
```
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
    <version>1.2.2.RELEASE</version>
</dependency>
``` 
在application.yml中进行配置
```
spring:
  kafka:
    bootstrap-servers: localhost:9092   #kafka地址，可以配置多个
    consumer:
      group-id: boot-kafka
      auto-offset-reset: earliest
```
更多配置参考org.springframework.boot.autoconfigure.kafka.KafkaProperties这个类

