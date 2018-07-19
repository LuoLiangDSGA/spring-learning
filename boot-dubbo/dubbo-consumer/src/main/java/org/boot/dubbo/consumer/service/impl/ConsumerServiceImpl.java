package org.boot.dubbo.consumer.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import lombok.extern.slf4j.Slf4j;
import org.boot.dubbo.api.service.HelloService;
import org.boot.dubbo.consumer.service.ConsumerService;
import org.springframework.stereotype.Service;

/**
 * Created by IntelliJ IDEA.
 *
 * @author luoliang
 * @date 2018/1/8
 **/
@Service
@Slf4j
public class ConsumerServiceImpl implements ConsumerService {
    @Reference(version = "1.0.0",
            application = "${dubbo.application.id}")
    private HelloService helloService;

    @Override
    public String sayHello(String name) {
        log.debug("invoke service {}", helloService.getClass());

        return helloService.sayHello(name);
    }
}
