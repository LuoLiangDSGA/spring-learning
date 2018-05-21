package org.boot.redis.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 *
 * @author luoliang
 * @date 2018/5/17
 * 要存储的对象
 **/
@Data
@Builder
public class User implements Serializable {
    private String id;

    private String name;

    private Integer age;
}
