package org.boot.mystarter.web;

import org.boot.mystarter.service.ExampleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author luoliang
 * @date 2019/2/26
 */
@RestController
public class ExampleController {
    @Resource
    private ExampleService exampleService;

    @GetMapping("/test")
    public String test() {
        exampleService.core();

        return "success";
    }
}
