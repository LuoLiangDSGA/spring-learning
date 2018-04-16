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

```
elasticjob:
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
接下来实现一个分布式的任务监听器，如果任务有分片，分布式监听器会在总的任务开始前执行一次，结束时执行一次。监听器在之前的ElasticJobConfig已经注册到了Spring容器之中。
```
public class ElasticJobListener extends AbstractDistributeOnceElasticJobListener {
    @Resource
    private TaskRepository taskRepository;

    public ElasticJobListener(long startedTimeoutMilliseconds, long completedTimeoutMilliseconds) {
        super(startedTimeoutMilliseconds, completedTimeoutMilliseconds);
    }

    @Override
    public void doBeforeJobExecutedAtLastStarted(ShardingContexts shardingContexts) {
    }

    @Override
    public void doAfterJobExecutedAtLastCompleted(ShardingContexts shardingContexts) {
        //任务执行完成后更新状态为已执行
        JobTask jobTask = taskRepository.findOne(Long.valueOf(shardingContexts.getJobParameter()));
        jobTask.setStatus(1);
        taskRepository.save(jobTask);
    }
}
```
实现一个ElasticJobHandler，用于向Elastic-job中添加指定的作业配置，作业配置分为3级，分别是JobCoreConfiguration，JobTypeConfiguration和LiteJobConfiguration。LiteJobConfiguration使用JobTypeConfiguration，JobTypeConfiguration使用JobCoreConfiguration，层层嵌套。
```
@Component
public class ElasticJobHandler {
    @Resource
    private ZookeeperRegistryCenter registryCenter;
    @Resource
    private JobEventConfiguration jobEventConfiguration;
    @Resource
    private ElasticJobListener elasticJobListener;

    /**
     * @param jobName
     * @param jobClass
     * @param shardingTotalCount
     * @param cron
     * @param id                 数据ID
     * @return
     */
    private static LiteJobConfiguration.Builder simpleJobConfigBuilder(String jobName,
                                                                       Class<? extends SimpleJob> jobClass,
                                                                       int shardingTotalCount,
                                                                       String cron,
                                                                       String id) {
        return LiteJobConfiguration.newBuilder(new SimpleJobConfiguration(
                JobCoreConfiguration.newBuilder(jobName, cron, shardingTotalCount).jobParameter(id).build(), jobClass.getCanonicalName()));
    }

    /**
     * 添加一个定时任务
     *
     * @param jobName            任务名
     * @param cron               表达式
     * @param shardingTotalCount 分片数
     */
    public void addJob(String jobName, String cron, Integer shardingTotalCount, String id) {
        LiteJobConfiguration jobConfig = simpleJobConfigBuilder(jobName, MyElasticJob.class, shardingTotalCount, cron, id)
                .overwrite(true).build();

        new SpringJobScheduler(new MyElasticJob(), registryCenter, jobConfig, jobEventConfiguration, elasticJobListener).init();
    }
}
```
到这里，elastic-job的注册中心，数据源相关配置，以及动态添加的逻辑已经做完了，接下来在service中调用上面写好的方法，验证功能是否正常。

编写一个ElasticJobService类，扫描数据库中状态为0的任务，并且把这些任务添加到Elastic-job中，这里的相关数据库操作使用了spring-data-jpa，dao层相关代码就不贴了，可以在源码中查看。
```
@Service
public class ElasticJobService {
    @Resource
    private ElasticJobHandler jobHandler;
    @Resource
    private TaskRepository taskRepository;

    /**
     * 扫描db，并添加任务
     */
    public void scanAddJob() {
        Specification query = (Specification<JobTask>) (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
                .and(criteriaBuilder.equal(root.get("status"), 0));
        List<JobTask> jobTasks = taskRepository.findAll(query);
        jobTasks.forEach(jobTask -> {
            Long current = System.currentTimeMillis();
            String jobName = "job" + jobTask.getSendTime();
            String cron;
            //说明消费未发送，但是已经过了消息的发送时间，调整时间继续执行任务
            if (jobTask.getSendTime() < current) {
                //设置为一分钟之后执行，把Date转换为cron表达式
                cron = CronUtils.getCron(new Date(current + 60000));
            } else {
                cron = CronUtils.getCron(new Date(jobTask.getSendTime()));
            }
            jobHandler.addJob(jobName, cron, 1, String.valueOf(jobTask.getId()));
        });
    }
}
```
在Junit中添加几条测试数据
```
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class JobTaskTest {
    @Resource
    private TaskRepository taskRepository;

    @Test
    public void add() {
        //生成几个任务，第一任务在三分钟之后
        Long unixTime = System.currentTimeMillis() + 60000;
        JobTask task = new JobTask("test-msg-1", 0, unixTime);
        taskRepository.save(task);
        unixTime += 60000;
        task = new JobTask("test-msg-2", 0, unixTime);
        taskRepository.save(task);
        unixTime += 60000;
        task = new JobTask("test-msg-3", 0, unixTime);
        taskRepository.save(task);
        unixTime += 60000;
        task = new JobTask("test-msg-4", 0, unixTime);
        taskRepository.save(task);
    }
}
```
此时，数据库中多了四条状态为0的数据

![](https://ws2.sinaimg.cn/large/006tNc79gy1fqelwgxnauj30bm02jglp.jpg)

最后，就可以开始验证整个流程了，代码如下
```
@SpringBootApplication
public class ElasticJobApplication implements CommandLineRunner {
    @Resource
    private ElasticJobService elasticJobService;

    public static void main(String[] args) {
        SpringApplication.run(ElasticJobApplication.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        elasticJobService.scanAddJob();
    }
}
```
可以看到，在启动过程中，多个任务被加入到了Elastic-job中，并且一小段时间之后，任务一次执行，执行成功之后，因为我们配置了监听器，会打印数据库的更新SQL，当任务执行完成，再查看数据库，发现状态也更改成功。数据库中同时也会多出两张表JOB_EXECUTION_LOG，JOB_STATUS_TRACE_LOG，这是我们之前配置的JobEventConfiguration，通过数据源持久化了作业配置的相关数据，这两张表的数据可以供Elastic-job提供的运维平台使用，具体请查看官方文档。

![](https://ws3.sinaimg.cn/large/006tNc79gy1fqelzzptz1j31kw0snqku.jpg)


### 总结
至此，整个流程就已经走完了，整个demo中主要用到了Elastic-job和spring-data-jpa相关的技术，作为demo，肯定会有一些缺陷，没考虑到的地方，可以根据自己的业务场景进行改进。

最后，附上github源码，欢迎star，一起交流。上面涉及到的数据库，请自行创建，表会自动生成。[源码地址](https://github.com/LuoLiangDSGA/SpringBoot-Learning/tree/master/boot-elasticjob)
