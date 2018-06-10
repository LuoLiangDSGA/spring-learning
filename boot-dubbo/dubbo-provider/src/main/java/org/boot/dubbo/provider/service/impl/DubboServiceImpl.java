package org.boot.dubbo.provider.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import lombok.extern.slf4j.Slf4j;
import org.boot.dubbo.provider.service.DubboService;

/**
 * Created by IntelliJ IDEA.
 *
 * @author luoliang
 * @date 2018/1/8
 **/
@Service(version = "1.0.0",
        application = "${dubbo.application.id}",
        protocol = "${dubbo.protocol.id}",
        registry = "${dubbo.registry.id}")
@Slf4j
public class DubboServiceImpl implements DubboService {

    @Override
    public String sayHello(String name) {
        log.debug("DubboService被调用，传入：{}", name);
        return "Hello, " + name + " (from Spring Boot)";
    }
}
