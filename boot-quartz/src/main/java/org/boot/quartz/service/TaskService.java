package org.boot.quartz.service;

import org.quartz.SchedulerException;

/**
 * Created by IntelliJ IDEA.
 *
 * @author luoliang
 * @date 2018/4/2
 **/
public interface TaskService {

    /**
     * @param jobName          任务名
     * @param jobGroupName     任务组名
     * @param triggerName      触发器名
     * @param triggerGroupName 触发器组名
     * @param jobClass         任务
     * @param cron             时间设置，cron表达式
     * @Description: 添加一个定时任务
     */
    void addJob(String jobName, String jobGroupName, String triggerName,
                String triggerGroupName, Class jobClass, String cron) throws SchedulerException;
}
