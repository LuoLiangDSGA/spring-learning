package org.boot.mongo.service.impl;

import org.boot.mongo.entity.User;
import org.boot.mongo.service.UserService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author luoliang
 * @date 2018/10/15
 */
@Service
public class UserServiceImpl implements UserService {
    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public void saveUser(User user) {

    }

    @Override
    public User findUserByUsername(String username) {
        return null;
    }

    @Override
    public void updateUser(User user) {

    }

    @Override
    public void deleteUserById(Long id) {

    }
}
