package org.boot.elasticjob.service;

import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.JobEventConfiguration;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.boot.elasticjob.job.ElasticJobListener;
import org.boot.elasticjob.job.MyElasticJob;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.security.PrivateKey;

/**
 * Created by IntelliJ IDEA.
 *
 * @author luoliang
 * @date 2018/4/1
 **/
@Service
public class ElasticJobService {
    @Resource
    private ZookeeperRegistryCenter registryCenter;
//    @Resource
//    private JobEventConfiguration jobEventConfiguration;
    @Resource
    private ElasticJobListener elasticJobListener;

    private static LiteJobConfiguration.Builder simpleJobConfigBuilder(
            final String jobName,
            final Class<? extends SimpleJob> jobClass,
            final int shardingTotalCount,
            final String cron) {
        return LiteJobConfiguration.newBuilder(new SimpleJobConfiguration(
                JobCoreConfiguration.newBuilder(jobName, cron, shardingTotalCount).build(), jobClass.getCanonicalName()));
    }

    public void addJob(String cron) {
        LiteJobConfiguration jobConfig = simpleJobConfigBuilder("boot-job",
                MyElasticJob.class, 1, cron).overwrite(true).build();
        new SpringJobScheduler(new MyElasticJob(), registryCenter, jobConfig, elasticJobListener).init();
    }
}
