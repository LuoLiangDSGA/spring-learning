package org.boot.mongo.service;

import org.boot.mongo.entity.User;

/**
 * @author luoliang
 * @date 2018/10/15
 */
public interface UserService {
    /**
     * 保存用户
     * @param user
     */
    void saveUser(User user);

    /**
     * 根据名称查询用户
     * @param username
     * @return
     */
    User findUserByUsername(String username);

    /**
     * 更新用户信息
     * @param user
     */
    void updateUser(User user);

    /**
     * 根据ID删除用户
     * @param id
     */
    void deleteUserById(String id);
}
