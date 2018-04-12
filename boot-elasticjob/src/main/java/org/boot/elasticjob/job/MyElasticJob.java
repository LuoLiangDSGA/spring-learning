package org.boot.elasticjob.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;

/**
 * Created by IntelliJ IDEA.
 *
 * @author luoliang
 * @date 2018/4/9
 **/
public class MyElasticJob implements SimpleJob {
    @Override
    public void execute(ShardingContext shardingContext) {
        System.out.println("任务名：" + shardingContext.getJobName() + "，分片数：" + shardingContext.getShardingTotalCount() + ",id=" + shardingContext.getJobParameter());
    }
}
