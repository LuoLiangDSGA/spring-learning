package org.boot.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author luoliang
 */
@SpringBootApplication
@Slf4j
public class BootRedisApplication implements CommandLineRunner {
    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;

    public static void main(String[] args) {
        SpringApplication.run(BootRedisApplication.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        log.info("----------Operate String----------");
        operateString();
        log.info("----------Operate List----------");
        operateList();
    }

    /**
     * 操作字符串
     */
    private void operateString() {
        redisTemplate.opsForValue().set("author", "luoliang");
        String value = redisTemplate.opsForValue().get("author");
        log.info("stringRedisTemplate输出值：{}", value);
    }

    /**
     * Redis List操作，Redis列表是简单的字符串列表，按照插入顺序排序。可以添加一个元素到列表的头部（左边）或者尾部（右边）
     */
    private void operateList() {
        String key = "website";
        ListOperations<String, String> listOperations = redisTemplate.opsForList();
        listOperations.leftPush(key, "Github");
        listOperations.leftPush(key, "CSDN");
        listOperations.leftPush(key, "SegmentFault");
        List<String> list = listOperations.range(key, 0, 2);
        list.forEach(log::info);
    }
}
