package org.boot.security.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author luoliang
 * @date 2018/7/8
 */
@Entity
@Data
public class User {
    @Id
    @GeneratedValue
    private Integer id;

    @Size(min = 1, max = 32, message = "Minimum username length: 4 characters，the maximum 32 characters ")
    @Column(unique = true, nullable = false)
    private String username;

    @Size(min = 6, message = "Minimum password length: 8 characters")
    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Role> roles;
}
