package org.boot.elasticjob.test;

import org.boot.elasticjob.dao.TaskRepository;
import org.boot.elasticjob.job.JobTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Created by IntelliJ IDEA.
 *
 * @author luoliang
 * @date 2018/4/10
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class JobTaskTest {
    @Resource
    private TaskRepository taskRepository;

    @Test
    public void add() {
        JobTask task = JobTask.builder().content("测试消息1").status(0).sendTime(System.currentTimeMillis()).build();
        taskRepository.save(task);

    }
}
