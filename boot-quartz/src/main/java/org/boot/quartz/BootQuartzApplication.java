package org.boot.quartz;

import org.boot.quartz.service.TaskService;
import org.boot.quartz.task.ScheduleTask;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

/**
 * @author luoliang
 */
@SpringBootApplication
public class BootQuartzApplication implements CommandLineRunner {
    @Resource
    private TaskService taskService;

    public static void main(String[] args) {
        SpringApplication.run(BootQuartzApplication.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        System.out.println("添加一个任务");
        taskService.addJob("aaa", "111", "", "", ScheduleTask.class, "0/1 * * * * ?");
        taskService.addJob("bbb", "222", "", "", ScheduleTask.class, "0/2 * * * * ?");
        Thread.sleep(5000);
        System.out.println("修改任务");
        taskService.modifyJobTime("bbb", "222", "", "", "0/3 * * * * ?");
        Thread.sleep(5000);
        System.out.println("删除任务");
        taskService.removeJob("aaa", "111", "", "");
    }
}
