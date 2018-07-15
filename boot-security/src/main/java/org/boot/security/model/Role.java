package org.boot.security.model;

import org.springframework.security.core.GrantedAuthority;

/**
 * @author luoliang
 * @date 2018/7/8
 */
public enum Role implements GrantedAuthority {
    /**
     * 管理员
     */
    ROLE_ADMIN,
    /**
     * 用户
     */
    ROLE_USER;

    @Override
    public String getAuthority() {
        return name();
    }
}
