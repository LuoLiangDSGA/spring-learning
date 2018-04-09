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
        switch (shardingContext.getShardingItem()) {
            case 0:
                System.out.println("boot-job-1");
                break;
            case 1:
                System.out.println("boot-job-2");
                break;
            case 2:
                System.out.println("boot-job-3");
                break;
            default:
        }
    }
}
