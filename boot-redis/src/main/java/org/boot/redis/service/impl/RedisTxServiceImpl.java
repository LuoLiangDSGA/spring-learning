package org.boot.redis.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.boot.redis.service.RedisTxService;
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
public class RedisTxServiceImpl implements RedisTxService {

    @Override
    public void save(String key, String value) {

    }

    @Override
    @Cacheable(cacheNames = "users")
    public String get(String key) {

    }
}
