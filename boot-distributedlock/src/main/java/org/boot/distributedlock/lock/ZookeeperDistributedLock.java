package org.boot.distributedlock.lock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

/**
 * @author luoliang
 * @date 2018/11/20
 */
@Component
public class ZookeeperDistributedLock {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperDistributedLock.class);
    /**
     * zk客户端
     */
    private ZooKeeper zk;
    /**
     * zk目录结构的最外层
     */
    private String root = "/locks";
    /**
     * 用来同步zk链接到了服务端
     */
    private CountDownLatch connectedSignal = new CountDownLatch(1);
    /**
     * zk地址，yaml中配置
     */
    @Value("config.zk.address")
    private String zkAddress;
    /**
     * zk超时
     */
    @Value("config.zk.timeout")
    private Integer timeout;
    private final static byte[] DATA = new byte[0];

    public ZookeeperDistributedLock() {
        try {
            zk = new ZooKeeper(zkAddress, timeout, watchedEvent -> {
                LOGGER.debug("receive watcher:", watchedEvent);
                if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected) {
                    LOGGER.debug("zookeeper connection established...");
                    connectedSignal.countDown();
                }
            });
            connectedSignal.await();
            Stat stat = zk.exists(root, false);
            if (Objects.isNull(stat)) {
                //创建根节点
                zk.create(root, DATA, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (IOException | InterruptedException | KeeperException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    class LockWatcher implements Watcher {
        private CountDownLatch latch;

        public LockWatcher(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void process(WatchedEvent watchedEvent) {
            if (watchedEvent.getType() == Event.EventType.NodeDeleted) {
                latch.countDown();
            }
        }
    }

    public void lock() {

    }

    public void unlock() {

    }
}
