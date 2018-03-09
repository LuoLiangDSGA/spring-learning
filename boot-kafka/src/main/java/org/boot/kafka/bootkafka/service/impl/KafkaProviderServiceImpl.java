package org.boot.kafka.bootkafka.service.impl;

import org.boot.kafka.bootkafka.service.KafkaProviderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by IntelliJ IDEA.
 *
 * @author luoliang
 * @date 2018/3/9
 **/
@Service
public class KafkaProviderServiceImpl implements KafkaProviderService {
    private static Logger logger = LoggerFactory.getLogger(KafkaProviderServiceImpl.class);
    @Resource
    private KafkaTemplate<String, String> template;

    @Override
    public void sendMsg(String content) {
        logger.info("开始发送消息...");
        template.send("myKafka", content);
        logger.info("消息发送完成");
    }
}
