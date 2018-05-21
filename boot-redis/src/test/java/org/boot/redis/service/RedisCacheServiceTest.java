package org.boot.redis.service;

import lombok.extern.slf4j.Slf4j;
import org.boot.redis.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Created by IntelliJ IDEA.
 *
 * @author luoliang
 * @date 2018/5/21
 **/
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
public class RedisCacheServiceTest {
    @Resource
    private RedisCacheService redisCacheService;

    @Test
    public void testGet() {
        User user = redisCacheService.get("1111111");
        log.info(user.toString());
    }

    @Test
    public void testSave() {
        User user = User.builder().id("1111111").name("spring").age(20).build();
        redisCacheService.save(user);
    }

    @Test
    public void testDelete() {
        redisCacheService.delete("1111111");
    }
}
