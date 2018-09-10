package org.boot.webflux.service.impl;

import org.boot.webflux.entity.User;
import org.boot.webflux.service.UserService;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author luoliang
 * @date 2018/9/8
 */
@Service
public class UserServiceImpl implements UserService {
    @Resource
    private ReactiveRedisOperations<String, User> redisOperations;

    @Override
    public Mono<Boolean> register(String id, String username) {
        User user = new User();
        user.setId(id);
        user.setName(username);
        user.setPassword("123456");
        return redisOperations.opsForValue().set(id, user);
    }

    @Override
    public Mono<ServerResponse> login(String username, String password) {
        return redisOperations.opsForValue().get(username)
                .flatMap(user -> {
                    Map<String, String> result = new HashMap<>(2);
                    if (Objects.isNull(user) || !Objects.equals(password, user.getPassword())) {
                        result.put("message", "账号或密码错误");
                        return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                                .contentType(MediaType.APPLICATION_JSON_UTF8)
                                .body(BodyInserters.fromObject(result));
                    } else {
                        result.put("message", "登录成功");
                        return ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON_UTF8)
                                .body(BodyInserters.fromObject(result));
                    }
                });
    }

    @Override
    public Flux<User> getAll() {
        return redisOperations.keys("*")
                .flatMap(redisOperations.opsForValue()::get);
    }
}
