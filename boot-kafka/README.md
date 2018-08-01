### 安装kafka和zookeeper
确保本地已经安装kafka，如果安装之后跳过这一步
这里使用的是[wurstmeister/zookeeper](https://github.com/wurstmeister/zookeeper-docker)和[wurstmeister/kafka](https://github.com/wurstmeister/kafka-docker)

步骤参考这个博客[docker运行kafka](http://blog.csdn.net/snowcity1231/article/details/54946857)

### 集成步骤
> 在pom中添加以下依赖
```
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
    <version>1.2.2.RELEASE</version>
</dependency>
``` 
> 在application.yml中进行配置
```
spring:
  kafka:
    #kafka地址，可以配置多个，用逗号隔开
    bootstrap-servers: localhost:9092   
    consumer:
      group-id: boot-kafka
      auto-offset-reset: earliest
```
更多配置参考org.springframework.boot.autoconfigure.kafka.KafkaProperties这个类

> 发送消息
```java
@Service
public class KafkaProviderServiceImpl implements KafkaProviderService {
    private static Logger logger = LoggerFactory.getLogger(KafkaProviderServiceImpl.class);
    @Resource
    private KafkaTemplate<String, String> template;

    @Override
    public void sendMsg(String content) {
        logger.info("开始发送消息...");
        template.send("myKafka", content);
        logger.info("消息发送完成");
        logger.info("---------------------------------");
    }
}
```
`KafkaTemplate`是一个发送消息的模版，避免我们书写过多的重复代码，常用的发送消息操作在`KafkaTemplate`中都已经提供。
