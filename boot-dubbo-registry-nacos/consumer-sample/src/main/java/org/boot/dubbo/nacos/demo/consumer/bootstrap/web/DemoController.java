package org.boot.dubbo.nacos.demo.consumer.bootstrap.web;

import org.apache.dubbo.config.annotation.Reference;
import org.boot.dubbo.nacos.demo.consumer.DemoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author luoliang
 * @date 2019/11/13
 */
@RestController
@RequestMapping("/demo")
public class DemoController {

    @Reference(version = "${demo.service.version}")
    private DemoService demoService;

    @GetMapping("/{name}")
    public String sayHello(@PathVariable("name") String name) {
        return demoService.sayHello(name);
    }

}
