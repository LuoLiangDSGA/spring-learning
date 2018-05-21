package org.boot.redis.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by IntelliJ IDEA.
 *
 * @author luoliang
 * @date 2018/5/21
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Slf4j
public class RedisTxServiceTest {
    @Resource
    private RedisTxService redisTxService;

    @Test
    public void testSave() {
        String key = "author";
        redisTxService.save(key, "xxx");
    }

    @Test
    public void testGet() {
        String key = "author";
        String value = redisTxService.get(key);
        log.info("Get value={} by key={}", value, key);
    }
}
