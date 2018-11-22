package org.boot.distributedlock.lock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;

/**
 * @author luoliang
 * @date 2018/11/20
 */
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
     * 锁名称
     */
    private String lockName;
    /**
     * 当前线程创建的序列Node
     */
    private ThreadLocal<String> nodeId = new ThreadLocal<>();
    private final static byte[] DATA = new byte[0];

    public ZookeeperDistributedLock(String zkAddress, String lockName, Integer timeout) {
        this.lockName = lockName;
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
        String myNode = null;
        try {
            //临时创建子节点
            myNode = zk.create(root + "/" + lockName, DATA, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            LOGGER.debug(Thread.currentThread().getName() + myNode, "===========> created");
            // 取出所有子节点
            List<String> childrenNodes = zk.getChildren(root, false);
            TreeSet<String> sortedNodes = new TreeSet<>();
            // 通过TreeSet排序
            childrenNodes.forEach(s -> sortedNodes.add(root + "/" + s));
            String smallNode = sortedNodes.first();
            String preNode = sortedNodes.lower(myNode);
            if (smallNode.equals(myNode)) {
                LOGGER.debug(Thread.currentThread().getName() + "-------" + myNode + "-------" + "get lock");
                // 表示获得锁
                nodeId.set(myNode);
                return;
            }
            CountDownLatch latch = new CountDownLatch(1);
            Stat stat = zk.exists(preNode, new LockWatcher(latch));
            if (Objects.nonNull(stat)) {
                LOGGER.debug(Thread.currentThread().getName() + "-------" + myNode + "-------" + "wait for" + root + "/" + preNode + " release lock");
                latch.await();
                nodeId.set(myNode);
//                latch = null;
            }
        } catch (KeeperException | InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public void unlock() {
        try {
            LOGGER.debug(Thread.currentThread().getName() + "-------" + nodeId.get() + " unlock");
            if (Objects.nonNull(nodeId)) {
                zk.delete(nodeId.get(), -1);
            }
            nodeId.remove();
        } catch (InterruptedException | KeeperException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
