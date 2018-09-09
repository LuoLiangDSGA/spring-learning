package org.boot.webflux.service.impl;

import org.boot.webflux.entity.User;
import org.boot.webflux.service.UserService;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * @author luoliang
 * @date 2018/9/8
 */
@Service
public class UserServiceImpl implements UserService {
    @Resource
    private ReactiveRedisOperations<String, User> redisOperations;

    @Override
    public Mono<User> register(String id, String username) {
        return null;
    }

    @Override
    public Mono<ServerResponse> login(String username, String password) {
        redisOperations.opsForValue().get(username);
        return Mono.create(sink -> {

        });
    }

    @Override
    public Flux<User> getAll() {
        return redisOperations.keys("*")
                .flatMap(redisOperations.opsForValue()::get);
    }
}
