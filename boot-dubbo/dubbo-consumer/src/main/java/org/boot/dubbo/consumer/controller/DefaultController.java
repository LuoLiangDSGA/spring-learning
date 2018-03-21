package org.boot.dubbo.consumer.controller;

import org.boot.dubbo.consumer.service.ConsumerService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by IntelliJ IDEA.
 *
 * @author luoliang
 * @date 2018/1/8
 **/
@RestController
@RequestMapping("/user")
public class DefaultController {
    @Resource
    private ConsumerService consumerService;

    @RequestMapping("/sayHello")
    public String register(String name) {
        return consumerService.sayHello(name);
    }
}
