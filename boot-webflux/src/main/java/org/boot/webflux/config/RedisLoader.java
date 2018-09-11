package org.boot.webflux.config;

import org.boot.webflux.entity.User;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.UUID;

/**
 * @author luoliang
 * @date 2018/9/8
 * 应用启动后初始化部分数据
 */
@Component
public class RedisLoader {
    @Resource
    private ReactiveRedisConnectionFactory factory;
    @Resource
    private ReactiveRedisOperations<String, Object> redisOperations;

    @PostConstruct
    public void loadData() {
        factory.getReactiveConnection().serverCommands().flushAll()
                .thenMany(Flux.just("Thor", "Hulk", "Tony")
                        .map(name -> new User(UUID.randomUUID().toString().substring(0, 5), name, "123456"))
                        .flatMap(user -> redisOperations.opsForValue().set(user.getId(), user))
                ).thenMany(redisOperations.keys("*")
                .flatMap(redisOperations.opsForValue()::get))
                .subscribe(System.out::println);
    }
}
