package org.boot.redis.service;

import org.boot.redis.entity.User;

/**
 * Created by IntelliJ IDEA.
 *
 * @author luoliang
 * @date 2018/5/21
 **/
public interface RedisCacheService {
    User save(User user);

    User get(String id);

    void delete(String id);
}
