package org.boot.kafka.bootkafka;

import org.boot.kafka.bootkafka.service.KafkaProviderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Created by IntelliJ IDEA.
 *
 * @author luoliang
 * @date 2018/3/9
 **/
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class MsgServiceTest {
    @Resource
    private KafkaProviderService kafkaProviderService;

    @Test
    public void testSend() {
        kafkaProviderService.sendMsg("fooooooooooooo");
    }
}
