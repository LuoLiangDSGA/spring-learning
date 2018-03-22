package org.boot.kafka.bootkafka.controller;

import org.boot.kafka.bootkafka.model.A;
import org.boot.kafka.bootkafka.service.KafkaProviderService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * Created by IntelliJ IDEA.
 *
 * @author luoliang
 * @date 2018/3/9
 * <p>
 * 发送消息的控制器
 **/
@RestController
@RequestMapping("/msg")
public class MsgController {
    @Resource
    private KafkaProviderService kafkaProviderService;

    @RequestMapping("/{content}")
    public String send(@PathVariable String content) {
        kafkaProviderService.sendMsg(content);
        return "消息发送成功";
    }

    @PostMapping("/test")
    public String test(@RequestBody A a) {
        System.out.println(a.toString());
        return "success";
    }
}
