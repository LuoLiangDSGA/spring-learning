package org.boot.redis.web;

import org.boot.redis.service.RedisTxService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by IntelliJ IDEA.
 *
 * @author luoliang
 * @date 2018/5/21
 **/
@RequestMapping("/redis")
@RestController
public class TestController {
    @Resource
    private RedisTxService redisTxService;

    @PostMapping("/save/{key}/{value}")
    public String save(@PathVariable String key, @PathVariable String value) {
        redisTxService.save(key, value);
        return "success";
    }
}
