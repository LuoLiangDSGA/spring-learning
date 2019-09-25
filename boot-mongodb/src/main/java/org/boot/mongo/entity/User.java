package org.boot.mongo.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;

/**
 * @author luoliang
 * @date 2018/10/15
 */
@Data
@Document(collection = "user") // 集合名称
public class User implements Serializable {
    private static final long serialVersionUID = -7520384490152472164L;

    @Id
    private String id;
    @Field
    @Indexed
    private String username;
    @Field
    private String password;
    @CreatedDate
    private Date gmtCreate;
}
