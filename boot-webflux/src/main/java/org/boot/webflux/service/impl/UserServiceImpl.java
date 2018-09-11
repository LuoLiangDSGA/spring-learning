package org.boot.webflux.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.boot.webflux.entity.User;
import org.boot.webflux.service.UserService;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * @author luoliang
 * @date 2018/9/8
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Resource
    private ReactiveRedisOperations<String, User> redisOperations;

    @Override
    public Mono<Boolean> add(String id, String username) {
        User user = new User();
        user.setId(id);
        user.setName(username);
        user.setPassword("123456");
        return redisOperations.opsForValue().set(id, user);
    }

    @Override
    public Mono<User> find(String username, String password) {
        return redisOperations.opsForValue().get(username);
    }

    @Override
    public Flux<User> getAll() {
        return redisOperations.keys("*")
                .flatMap(redisOperations.opsForValue()::get);
    }

    @Override
    public Mono<Boolean> remove(String id) {
        return redisOperations.opsForValue().delete(id);
    }
}
