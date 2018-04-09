package org.boot.elasticjob.job;

import com.dangdang.ddframe.job.executor.ShardingContexts;
import com.dangdang.ddframe.job.lite.api.listener.AbstractDistributeOnceElasticJobListener;
import org.apache.log4j.Logger;


/**
 * Created by IntelliJ IDEA.
 *
 * @author luoliang
 * @date 2018/4/9
 **/
public class ElasticJobListener extends AbstractDistributeOnceElasticJobListener {
    private static final Logger logger = Logger.getLogger(ElasticJobListener.class);

    /**
     * @param startedTimeoutMilliseconds
     * @param completedTimeoutMilliseconds
     */
    public ElasticJobListener(long startedTimeoutMilliseconds, long completedTimeoutMilliseconds) {
        super(startedTimeoutMilliseconds, completedTimeoutMilliseconds);
    }

    @Override
    public void doBeforeJobExecutedAtLastStarted(ShardingContexts shardingContexts) {
        logger.info("分布式监听器开始……");
    }

    @Override
    public void doAfterJobExecutedAtLastCompleted(ShardingContexts shardingContexts) {
        logger.info("分布式监听器结束……");
    }
}
