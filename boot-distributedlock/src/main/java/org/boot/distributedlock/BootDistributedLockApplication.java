package org.boot.distributedlock;

import org.boot.distributedlock.lock.ZookeeperDistributedLock;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

/**
 * @author luoliang
 */
@SpringBootApplication
public class BootDistributedLockApplication implements CommandLineRunner {
    @Resource
    private ZookeeperDistributedLock zookeeperDistributedLock;

    public static void main(String[] args) {
        SpringApplication.run(BootDistributedLockApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        zookeeperDistributedLock.lock();
        // do sth
        zookeeperDistributedLock.unlock();
    }
}
