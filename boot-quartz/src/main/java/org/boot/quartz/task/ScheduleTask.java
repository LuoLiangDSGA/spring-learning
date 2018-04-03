package org.boot.quartz.task;

import org.quartz.*;
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
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        JobDataMap map = jobExecutionContext.getJobDetail().getJobDataMap();

        System.out.println(map.get("name") + " Hello, I'm the quartz job!");
    }
}
