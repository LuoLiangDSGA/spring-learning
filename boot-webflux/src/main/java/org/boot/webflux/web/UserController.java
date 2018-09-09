package org.boot.webflux.web;

import org.boot.webflux.entity.User;
import org.boot.webflux.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;

/**
 * @author luoliang
 * @date 2018/9/8
 */
@RestController
public class UserController {
    @Resource
    private UserService userService;

    @GetMapping("/users")
    public Flux<User> all() {
        return userService.getAll();
    }
}
