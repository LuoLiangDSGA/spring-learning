package org.boot.quartz;

import org.boot.quartz.service.TaskService;
import org.boot.quartz.task.ScheduleTask;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import javax.annotation.Resource;

/**
 * @author luoliang
 */
@SpringBootApplication
public class BootQuartzApplication implements CommandLineRunner{
    @Resource
    private TaskService taskService;
    public static String JOB_NAME = "动态任务调度";
    public static String TRIGGER_NAME = "动态任务触发器";
    public static String JOB_GROUP_NAME = "XLXXCC_JOB_GROUP";
    public static String TRIGGER_GROUP_NAME = "XLXXCC_JOB_GROUP";

    public static void main(String[] args) {
        SpringApplication.run(BootQuartzApplication.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        taskService.addJob(JOB_NAME, JOB_GROUP_NAME, TRIGGER_NAME, TRIGGER_GROUP_NAME, ScheduleTask.class, "0/1 * * * * ?");
    }
}
