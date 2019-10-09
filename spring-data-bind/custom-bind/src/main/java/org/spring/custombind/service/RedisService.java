package org.spring.custombind.service;

import org.spring.custombind.model.User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * @author luoliang
 * @date 2019/10/8
 * 模拟redis操作业务类
 */
@Service
public class RedisService {

    public Object get(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        return User.builder().id(key).name("二哈").build();
    }

    public void set(String key, Object value) {
        // todo
    }
}
