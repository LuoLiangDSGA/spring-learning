package org.boot.redis.entity;

import lombok.Data;

/**
 * Created by IntelliJ IDEA.
 *
 * @author luoliang
 * @date 2018/5/17
 * 要存储的对象
 **/
@Data
public class User {
    private String name;

    private Integer age;
}
