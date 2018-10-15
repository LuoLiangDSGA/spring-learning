package org.boot.mongo.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author luoliang
 * @date 2018/10/15
 */
@Data
public class User implements Serializable {
    private static final long serialVersionUID = -7520384490152472164L;

    private Long id;

    private String username;

    private String password;
}
