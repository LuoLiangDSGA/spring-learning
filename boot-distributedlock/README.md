## 分布式锁
在同一个jvm进程中时，可以使用J.U.C提供的一些锁来解决多个线程竞争同一个共享资源时候的线程安全问题，但是当多个不同机器上的不同jvm进程共同竞争同一个共享资源时候，J.U.C包的锁就无能无力了，这时候就需要分布式锁了。常见的有使用zk的最小版本，redis的set函数，数据库锁来实现。


### Redis分布式锁
为了确保分布式锁可用，我们至少要确保锁的实现同时满足以下四个条件：
1. 互斥性。在任意时刻，只有一个客户端能持有锁。
2. 不会发生死锁。即使有一个客户端在持有锁的期间崩溃而没有主动解锁，也能保证后续其他客户端能加锁。
3. 具有容错性。只要大部分的Redis节点正常运行，客户端就可以加锁和解锁。
4. 解铃还须系铃人。加锁和解锁必须是同一个客户端，客户端自己不能把别人加的锁给解了。

> 加锁代码
```java
jedis.set(String key, String value, String nxxx, String expx, int time);
```

这个set()方法一共有五个形参：
- 第一个为key，我们使用key来当锁，因为key是唯一的。
- 第二个为value，我们传的是requestId，很多童鞋可能不明白，有key作为锁不就够了吗，为什么还要用到value？原因就是我们在上面讲到可靠性时，分布式锁要满足第四个条件解铃还须系铃人，通过给value赋值为requestId，我们就知道这把锁是哪个请求加的了，在解锁的时候就可以有依据。requestId可以使用UUID.randomUUID().toString()方法生成。
- 第三个为nxxx，这个参数我们填的是NX，意思是SET IF NOT EXIST，即当key不存在时，我们进行set操作；若key已经存在，则不做任何操作；
- 第四个为expx，这个参数我们传的是PX，意思是我们要给这个key加一个过期的设置，具体时间由第五个参数决定。
- 第五个为time，与第四个参数相呼应，代表key的过期时间。

> 解锁代码
```java
String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));
```
为了保证原子性，这里使用了lua脚本，eval命令执行Lua代码的时候，Lua代码将被当成一个命令去执行，并且直到eval命令执行完成，Redis才会执行其他命令。

### Zookeeper分布式锁
Zookeeper（业界简称zk）是一种提供配置管理、分布式协同以及命名的中心化服务，这些提供的功能都是分布式系统中非常底层且必不可少的基本功能，但是如果自己实现这些功能而且要达到高吞吐、低延迟同时还要保持一致性和可用性，实际上非常困难。因此zookeeper提供了这些功能，开发者在zookeeper之上构建自己的各种分布式系统。

> 步骤
1. 客户端连接zookeeper，并在/locks下创建临时的且有序的子节点，第一个客户端对应的子节点为/locks/lock-0000000000，第二个为/locks/lock-0000000001，以此类推。
2. 客户端获取/locks下的子节点列表，判断自己创建的子节点是否为当前子节点列表中序号最小的子节点，如果是则认为获得锁，否则监听/locks的子节点变更消息，获得子节点变更通知后重复此步骤直至获得锁；
3. 执行业务代码；
4. 完成业务流程后，删除对应的子节点释放锁。

### 参考
https://wudashan.cn/2017/10/23/Redis-Distributed-Lock-Implement/