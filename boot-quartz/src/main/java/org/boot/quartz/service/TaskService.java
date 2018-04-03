package org.boot.quartz.service;

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
     *                         <p>
     *                         添加一个定时任务
     */
    void addJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName, Class jobClass, String cron);

    /**
     * 修改定时任务时间
     *
     * @param jobName
     * @param jobGroupName
     * @param triggerName
     * @param triggerGroupName
     * @param cron
     */
    void modifyJobTime(String jobName, String jobGroupName, String triggerName, String triggerGroupName, String cron);

    /**
     * 移除定时任务
     *
     * @param jobName
     * @param jobGroupName
     * @param triggerName
     * @param triggerGroupName
     */
    void removeJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName);

    /**
     * 启动所有定时任务
     */
    void startJobs();

    /**
     * 停止所有定时任务
     */
    void shutdownJobs();
}
