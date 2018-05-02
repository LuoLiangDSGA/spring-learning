package org.boot.protostuff.entity;

import lombok.Builder;
import lombok.Data;

/**
 * Created by IntelliJ IDEA.
 *
 * @author luoliang
 * @date 2018/5/2
 **/
@Data
@Builder
public class User {
    private String id;

    private String name;

    private Integer age;

    private String desc;
}
