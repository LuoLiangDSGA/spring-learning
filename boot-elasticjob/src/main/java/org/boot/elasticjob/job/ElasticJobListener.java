package org.boot.elasticjob.job;

import com.dangdang.ddframe.job.executor.ShardingContexts;
import com.dangdang.ddframe.job.lite.api.listener.AbstractDistributeOnceElasticJobListener;
import org.apache.log4j.Logger;
import org.boot.elasticjob.dao.TaskRepository;
import org.boot.elasticjob.entity.JobTask;

import javax.annotation.Resource;


/**
 * Created by IntelliJ IDEA.
 *
 * @author luoliang
 * @date 2018/4/9
 **/
public class ElasticJobListener extends AbstractDistributeOnceElasticJobListener {
    private static final Logger logger = Logger.getLogger(ElasticJobListener.class);
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
