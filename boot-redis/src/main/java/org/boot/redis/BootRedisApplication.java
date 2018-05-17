package org.boot.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;

/**
 * @author luoliang
 */
@SpringBootApplication
@Slf4j
public class BootRedisApplication implements CommandLineRunner {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public static void main(String[] args) {
        SpringApplication.run(BootRedisApplication.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        log.info("----------操作字符串----------");
        operateString();
    }

    /**
     * 操作字符串
     */
    private void operateString() {
        stringRedisTemplate.opsForValue().set("author", "luoliang");
        String value = stringRedisTemplate.opsForValue().get("author");
        log.info("stringRedisTemplate输出值：{}", value);
    }
}
