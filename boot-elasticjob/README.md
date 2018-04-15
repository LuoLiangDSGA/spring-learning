### SpringBoot使用Elastic-Job-lite，实现动态创建定时任务，任务持久化
Elastic-Job是当当开源的一个分布式调度解决方案，由两个相互独立的子项目Elastic-Job-Lite和Elastic-Job-Cloud组成。

Elastic-Job-Lite定位为轻量级无中心化解决方案，使用jar包的形式提供分布式任务的协调服务；Elastic-Job-Cloud采用自研Mesos Framework的解决方案，额外提供资源治理、应用分发以及进程隔离等功能。

这里以Elastic-Job-lite为例，跟SpringBoot进行整合，当当的官方文档中并没有对SpringBoot集成作说明，所有的配置都是基于文档中的xml的配置修改出来的。

### 起步
准备好一个SpringBoot的项目，pom.xml中引入Elastic-job，mysql，jpa等依赖

```
<dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>com.dangdang</groupId>
            <artifactId>elastic-job-lite-spring</artifactId>
            <version>2.1.5</version>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>

        <dependency>
            <groupId>com.zaxxer</groupId>
            <artifactId>HikariCP</artifactId>
        </dependency>
    </dependencies>
```

### 配置
使用yaml进行相关属性的配置，主要配置的是数据库连接池，jpa

```elasticjob:
     serverlists: 172.31.31.48:2181
     namespace: boot-job

   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/test?characterEncoding=utf-8&verifyServerCertificate=false&useSSL=false&requireSSL=false
       driver-class-name: com.mysql.jdbc.Driver
       username: root
       password: root
       type: com.zaxxer.hikari.HikariDataSource
   #  自动创建更新验证数据库结构
     jpa:
       hibernate:
         ddl-auto: update
       show-sql: true
       database: mysql
 ```

 elastic-job相关的配置使用java配置实现，代替官方文档的xml配置
```
@Configuration
@Data
@ConfigurationProperties(prefix = "elasticjob")
public class ElasticJobConfig {
    private String serverlists;
    private String namespace;
    @Resource
    private HikariDataSource dataSource;

    @Bean
    public ZookeeperConfiguration zkConfig() {
        return new ZookeeperConfiguration(serverlists, namespace);
    }

    @Bean(initMethod = "init")
    public ZookeeperRegistryCenter regCenter(ZookeeperConfiguration config) {
        return new ZookeeperRegistryCenter(config);
    }

    /**
     * 将作业运行的痕迹进行持久化到DB
     *
     * @return
     */
    @Bean
    public JobEventConfiguration jobEventConfiguration() {
        return new JobEventRdbConfiguration(dataSource);
    }

    @Bean
    public ElasticJobListener elasticJobListener() {
        return new ElasticJobListener(100, 100);
    }
}
```
所有相关的配置到这里就已经OK了，接下来开始具体的编码实现
### 定时任务实现
先实现一个自己的任务类，需要实现elastic-job提供的SimpleJob接口，实现它的execute(ShardingContext shardingContext)方法
```
@Slf4j
public class MyElasticJob implements SimpleJob {
    @Override
    public void execute(ShardingContext shardingContext) {
        //打印出任务相关信息，JobParameter用于传递任务的ID
        log.info("任务名：{}, 片数：{}, id={}", shardingContext.getJobName(), shardingContext.getShardingTotalCount(),
                shardingContext.getJobParameter());
    }
}
```
