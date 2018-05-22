package org.boot.redis;

import lombok.extern.slf4j.Slf4j;
import org.boot.redis.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * @author luoliang
 */
@SpringBootApplication
@EnableCaching
@Slf4j
public class BootRedisApplication implements CommandLineRunner {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedisTemplate<String, Object> objRedisTemplate;

    public static void main(String[] args) {
        SpringApplication.run(BootRedisApplication.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        log.info("----------Operate String----------");
        operateString();
        log.info("----------Operate List----------");
        operateList();
        log.info("----------Operate Hash----------");
        operateHash();
    }

    /**
     * 操作字符串
     */
    private void operateString() {
        stringRedisTemplate.opsForValue().set("author", "luoliang");
        String value = stringRedisTemplate.opsForValue().get("author");
        log.info("stringRedisTemplate输出值：{}", value);
    }

    /**
     * Redis List操作，Redis列表是简单的字符串列表，按照插入顺序排序。可以添加一个元素到列表的头部（左边）或者尾部（右边）
     */
    private void operateList() {
        String key = "website";
        ListOperations<String, String> listOperations = stringRedisTemplate.opsForList();
        //从左压入栈
        listOperations.leftPush(key, "Github");
        listOperations.leftPush(key, "CSDN");
        //从右压入栈
        listOperations.rightPush(key, "SegmentFault");
        log.info("list size:{}", listOperations.size(key));
        List<String> list = listOperations.range(key, 0, 2);
        list.forEach(log::info);
    }

    /**
     * 操作hash，存放User对象
     */
    private void operateHash() {
        String key = "user";
        HashOperations<String, String, User> hashOperations = objRedisTemplate.opsForHash();
        hashOperations.put(key, "user1", User.builder().name("Hulk").age(50).build());
        hashOperations.put(key, "user2", User.builder().name("Thor").age(1500).build());
        hashOperations.put(key, "user3", User.builder().name("Rogers").age(150).build());
        log.info("hash size:{}", hashOperations.size(key));
        log.info("--------拿到Map的key集合--------");
        Set<String> keys = hashOperations.keys(key);
        keys.forEach(log::info);
        log.info("--------拿到Map的value集合--------");
        List<User> users = hashOperations.values(key);
        users.forEach(user -> log.info(user.toString()));
        log.info("--------拿到user1的value--------");
        User user = hashOperations.get(key, "user1");
        log.info(user.toString());
    }
}
