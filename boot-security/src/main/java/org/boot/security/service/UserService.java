package org.boot.security.service;

import org.boot.security.model.User;

/**
 * @author luoliang
 * @date 2018/7/14
 */
public interface UserService {
    /**
     * 注册
     *
     * @param user
     * @return
     */
    User register(User user);

    /**
     * 登录
     *
     * @param username
     * @param password
     * @return
     */
    String login(String username, String password);

    /**
     * 刷新jwt
     *
     * @param oldToken
     * @return
     */
    String refresh(String oldToken);
}
