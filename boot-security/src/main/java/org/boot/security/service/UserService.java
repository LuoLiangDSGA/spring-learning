package org.boot.security.service;

import org.boot.security.model.User;

/**
 * @author luoliang
 * @date 2018/7/14
 */
public interface UserService {
    User register(User user);

    String login(String username, String password);

    String refresh(String oldToken);
}
