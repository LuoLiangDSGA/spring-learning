package org.boot.redis.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.boot.redis.service.RedisTxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by IntelliJ IDEA.
 *
 * @author luoliang
 * @date 2018/5/21
 **/
@Service
@Slf4j
public class RedisTxServiceImpl implements RedisTxService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(String key, String value) {
        log.info("RedisTxService save key={},value={}", key, value);
        redisTemplate.opsForValue().set(key, value);
        throw new RuntimeException("----------发生异常");
    }

    @Override
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
