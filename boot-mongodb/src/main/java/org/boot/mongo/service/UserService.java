package org.boot.mongo.service;

import org.boot.mongo.entity.User;

/**
 * @author luoliang
 * @date 2018/10/15
 */
public interface UserService {
    void saveUser(User user);

    User findUserByUsername(String username);

    void updateUser(User user);

    void deleteUserById(Long id);
}
