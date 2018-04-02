package org.boot.quartz;

import org.boot.quartz.service.TaskService;
import org.boot.quartz.task.ScheduleTask;
import org.junit.runner.RunWith;
import org.quartz.SchedulerException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Created by IntelliJ IDEA.
 *
 * @author luoliang
 * @date 2018/4/2
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class Test {
    public static String JOB_NAME = "动态任务调度";
    public static String TRIGGER_NAME = "动态任务触发器";
    public static String JOB_GROUP_NAME = "XLXXCC_JOB_GROUP";
    public static String TRIGGER_GROUP_NAME = "XLXXCC_JOB_GROUP";
    @Resource
    private TaskService taskService;

    @org.junit.Test
    public void test() throws SchedulerException {
        taskService.addJob(JOB_NAME, JOB_GROUP_NAME, TRIGGER_NAME, TRIGGER_GROUP_NAME, ScheduleTask.class, "0/1 * * * * ?");
    }
}
