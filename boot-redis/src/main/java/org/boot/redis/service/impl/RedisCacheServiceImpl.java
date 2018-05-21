package org.boot.redis.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.boot.redis.entity.User;
import org.boot.redis.service.RedisCacheService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Created by IntelliJ IDEA.
 *
 * @author luoliang
 * @date 2018/5/21
 **/
@Service
@Slf4j
@CacheConfig(cacheNames = "users")
public class RedisCacheServiceImpl implements RedisCacheService {

    @Override
    @CachePut(key = "#p0.id")
    public User save(User user) {
        log.info("-----执行数据库更新操作");
        log.info("-----数据库更新完成，返回结果");

        return user;
    }

    @Override
    @Cacheable(key = "#p0")
    public User get(String id) {
        log.info("-----执行数据库查询操作");
        User user = User.builder().id(id).name("spring").age(18).build();
        log.info("-----数据库查询完成，返回结果");
        return user;
    }

    @Override
    @CacheEvict(key = "#p0")
    public void delete(String id) {
        log.info("-----执行数据库删除操作");
        log.info("-----数据库删除完成，返回结果");
    }
}
