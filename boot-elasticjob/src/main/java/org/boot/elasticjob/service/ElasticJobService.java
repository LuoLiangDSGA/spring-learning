package org.boot.elasticjob.service;

import org.boot.elasticjob.dao.TaskRepository;
import org.boot.elasticjob.entity.JobTask;
import org.boot.elasticjob.job.ElasticJobHandler;
import org.boot.elasticjob.util.CronUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author luoliang
 * @date 2018/4/1
 **/
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
