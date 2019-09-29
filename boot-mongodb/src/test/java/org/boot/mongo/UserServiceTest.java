package org.boot.mongo;

import lombok.extern.slf4j.Slf4j;
import org.boot.mongo.entity.User;
import org.boot.mongo.repository.UserRepository;
import org.boot.mongo.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author luoliang
 * @date 2018/10/16
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class UserServiceTest {
    @Resource
    private UserService userService;
    @Resource
    private UserRepository userRepository;
    @Resource
    private MongoTemplate mongoTemplate;

    @Test
    public void saveUser() {
        User user = new User();
        user.setUsername("mongodb");
        user.setPassword("root");
        userService.saveUser(user);
    }

    @Test
    public void findUserByUsername() {
        User user = userService.findUserByUsername("mongodb");
        if (Objects.nonNull(user)) {
            log.debug("user is: {}", user.toString());
        }
    }

    @Test
    public void updateUser() {
        User user = new User();
        user.setUsername("mongodb");
        user.setPassword("rootroot");
        userService.updateUser(user);
    }

    @Test
    public void testSave2() {
//        userRepository.save();
    }

    @Test
    public void deleteUserById() {
        User user = mongoTemplate.findAndRemove(Query.query(Criteria.where("id").is("5d906c945067a10b7b286a10")),
                User.class);
        log.info(user.toString());
//        userService.deleteUserById("5d89ecce6df3be6a5672d899");
    }
}