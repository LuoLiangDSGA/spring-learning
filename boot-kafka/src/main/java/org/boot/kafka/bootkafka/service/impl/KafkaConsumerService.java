package org.boot.kafka.bootkafka.service.impl;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 *
 * @author luoliang
 * @date 2018/3/9
 **/
@Component
public class KafkaConsumerService {
    private static Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    @KafkaListener(topics = "myKafka")
    public void receiveMsg(ConsumerRecord<?, ?> cr) {
        logger.info("收到一条消息...");
        logger.info("{} : {}", cr.topic(), cr.value());
        logger.info("---------------------------------");
    }
}
