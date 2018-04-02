package org.boot.quartz.task;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 *
 * @author luoliang
 * @date 2018/4/2
 **/
@Component
@EnableScheduling
public class ScheduleTask implements Job {
    public void sayHello(String name) {
        System.out.println(name + " Hello world, i'm the king of the world!!!");
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println(" Hello world, i'm the king of the world!!!");
    }
}
