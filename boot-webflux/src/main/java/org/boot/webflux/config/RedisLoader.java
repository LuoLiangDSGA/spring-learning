package org.boot.webflux.config;

import org.boot.webflux.entity.User;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.util.UUID;

/**
 * @author luoliang
 * @date 2018/9/8
 */
@Component
public class RedisLoader {
    private final ReactiveRedisConnectionFactory factory;
    private final ReactiveRedisOperations<String, Object> redisOperations;

    public RedisLoader(ReactiveRedisConnectionFactory factory, ReactiveRedisOperations<String, Object> redisOperations) {
        this.factory = factory;
        this.redisOperations = redisOperations;
    }

    @PostConstruct
    public void loadData() {
        factory.getReactiveConnection().serverCommands().flushAll()
                .thenMany(Flux.just("Thor", "Hulk", "Tony")
                        .map(name -> new User(UUID.randomUUID().toString(), name))
                        .flatMap(user -> redisOperations.opsForValue().set(user.getId(), user))
                ).thenMany(redisOperations.keys("*")
                .flatMap(redisOperations.opsForValue()::get))
                .subscribe(System.out::println);
    }
}
