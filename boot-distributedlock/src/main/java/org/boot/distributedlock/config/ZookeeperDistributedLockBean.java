package org.boot.distributedlock.config;

import org.boot.distributedlock.lock.ZookeeperDistributedLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @author luoliang
 * @date 2018/11/21
 */
@SpringBootConfiguration
public class ZookeeperDistributedLockBean {
    @Value("${config.zk.address}")
    private String zkAddress;
    @Value("${config.zk.timeout}")
    private Integer timeout;

    @Bean
    public ZookeeperDistributedLock initZkDistributedLock() {
        return new ZookeeperDistributedLock(zkAddress, "test_lock", timeout);
    }
}
