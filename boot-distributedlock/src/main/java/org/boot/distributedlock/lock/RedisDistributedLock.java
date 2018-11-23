package org.boot.distributedlock.lock;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Objects;

/**
 * @author luoliang
 * @date 2018/11/22
 * <p>
 * Redis分布式锁
 */
public class RedisDistributedLock {
    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    private static final Long RELEASE_SUCCESS = 1L;

    /**
     * @param jedisPool
     * @param lockKey
     * @param requestId
     * @param expireTime
     */
    public boolean tryLock(JedisPool jedisPool, String lockKey, String requestId, int expireTime) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
            if (LOCK_SUCCESS.equals(result)) {
                return true;
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (Objects.isNull(jedis)) {
                jedis.close();
            }
        }

        return false;
    }

    public void lock() {

    }

    public void unLock() {

    }
}
