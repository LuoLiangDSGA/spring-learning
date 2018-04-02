package org.boot.quartz.config;

import org.boot.quartz.task.ScheduleTask;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * Created by IntelliJ IDEA.
 *
 * @author luoliang
 * @date 2018/4/2
 **/
@Configuration
public class QuartzConfiguration {
    //配置SchedulerFactoryBean
    @Bean(name = "scheduler")
    public SchedulerFactoryBean schedulerFactory() {
        SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
        // 延时启动，应用启动1秒后
//        scheduler.setStartupDelay(1);
        // 注册触发器
//        scheduler.setTriggers(firstTrigger);

        return scheduler;
    }
}
