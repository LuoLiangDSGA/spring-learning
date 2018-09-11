package org.boot.webflux.web;

import org.boot.webflux.entity.User;
import org.boot.webflux.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * @author luoliang
 * @date 2018/9/8
 * 通过Rest API创建Web Flux
 */
@RestController
public class UserController {
    @Resource
    private UserService userService;

    @GetMapping("/users")
    public Flux<User> all() {
        return userService.getAll();
    }

    @PostMapping("/add")
    public Mono<Boolean> register(@RequestBody User user) {
        return userService.add(user.getId(), user.getName());
    }

    @PostMapping("/find")
    public Mono login(@RequestBody User user) {
        return userService.find(user.getName(), user.getPassword());
    }
}
