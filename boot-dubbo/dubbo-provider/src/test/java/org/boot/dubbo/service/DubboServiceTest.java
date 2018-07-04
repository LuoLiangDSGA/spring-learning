package org.boot.dubbo.service;

import com.alibaba.dubbo.config.annotation.Reference;
import lombok.extern.slf4j.Slf4j;
import org.boot.dubbo.consumer.service.HelloService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author luoliang
 * @date 2018/6/26
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class DubboServiceTest {
    @Reference
    private HelloService helloService;

    @Test
    public void hello() {
        log.debug(helloService.sayHello("dubbo"));
    }
}
