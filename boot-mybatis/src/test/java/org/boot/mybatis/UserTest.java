package org.boot.mybatis;

import org.boot.mybatis.mapper.UserMapper;
import org.boot.mybatis.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author luoliang
 * @date 2018/12/25
 */
public class UserTest extends BootMybatisApplicationTests {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserTest.class);
    @Resource
    private UserMapper userMapper;

    @Test
    public void testInsertUser() {
        User user = new User();
        user.setName("thor");
        user.setPassword("1234");
        user.setAddress("Cheng Du");
        user.setEmail("1234@gmail.com");
        user.setState(1);
        userMapper.insert(user);
    }

    @Test
    public void testUpdateUser() {
        User user = new User();
        user.setId(1);
        user.setName("thor");
        user.setPassword("123456");
        user.setAddress("Cheng Du");
        user.setEmail("1234@gmail.com");
        user.setState(1);
        userMapper.update(user);
    }

    @Test
    public void testFindById() {
        User user = userMapper.findById(1);
        Assert.assertNotEquals(user, null);
        LOGGER.info(user.toString());
    }

    @Test
    public void testDeleteUser() {
        userMapper.delete(2);
    }

    @Test
    public void testSelectList() {
        List<User> list = userMapper.selectList(1);
        LOGGER.info(list.toString());
    }
}
