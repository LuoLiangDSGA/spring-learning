package org.boot.dubbo.consumer.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import org.boot.dubbo.consumer.service.ConsumerService;
import org.boot.dubbo.provider.service.DubboService;
import org.springframework.stereotype.Service;

/**
 * Created by IntelliJ IDEA.
 *
 * @author luoliang
 * @date 2018/1/8
 **/
@Service
public class ConsumerServiceImpl implements ConsumerService {
    @Reference(version = "1.0.0",
            application = "${dubbo.application.id}")
    private DubboService dubboService;

    @Override
    public String sayHello(String name) {
        return dubboService.sayHello(name);
    }
}
