package org.boot.redis.service;

/**
 * Created by IntelliJ IDEA.
 *
 * @author luoliang
 * @date 2018/5/21
 **/
public interface RedisTxService {
    /**
     * 存储String
     *
     * @return
     */
    void save(String key, String value);

    String get(String key);
}
