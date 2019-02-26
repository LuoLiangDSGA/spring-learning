package org.boot.mystarter;

import org.boot.mystarter.service.ExampleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MystarterApplicationTests {
    @Resource
    private ExampleService exampleService;

    @Test
    public void contextLoads() {
    }

    @Test
    public void test() {
        exampleService.core();
    }
}

