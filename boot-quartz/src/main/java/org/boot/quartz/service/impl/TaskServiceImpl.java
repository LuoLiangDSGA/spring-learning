package org.boot.quartz.service.impl;

import org.boot.quartz.service.TaskService;
import org.boot.quartz.task.ScheduleTask;
import org.quartz.*;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static org.quartz.CronScheduleBuilder.cronSchedule;

/**
 * Created by IntelliJ IDEA.
 *
 * @author luoliang
 * @date 2018/4/2
 **/
@Service
public class TaskServiceImpl implements TaskService {
    @Resource
    private SchedulerFactoryBean schedulerFactoryBean;

    @Override
    public void addJob(String jobName, String jobGroupName, String triggerName,
                       String triggerGroupName, Class jobClass, String cron) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("jobArg", "world");
        JobDetail jobDetail = JobBuilder.newJob(ScheduleTask.class)
                .setJobData(jobDataMap)
                .withDescription("demo")
                .withIdentity("demo-job", "demo-group")
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withSchedule(cronSchedule(cron))
                .build();
        if (!scheduler.checkExists(JobKey.jobKey("demo-job", "demo-group"))) {
            scheduler.scheduleJob(jobDetail, trigger);
        }

        scheduler.start();
    }
}
