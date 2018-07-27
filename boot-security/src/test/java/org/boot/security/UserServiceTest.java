package org.boot.security;

import org.boot.security.model.Role;
import org.boot.security.model.User;
import org.boot.security.repository.UserRepository;
import org.boot.security.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author luoliang
 * @date 2018/7/25
 */
public class UserServiceTest extends BootSecurityApplicationTests {
    @Resource
    private UserRepository userRepository;

    @Test
    public void register() {
        User user = new User();
        List<Role> roles = new ArrayList<>();
        roles.add(Role.ROLE_USER);
        roles.add(Role.ROLE_ADMIN);
        user.setRoles(roles);
        user.setUsername("test");
        user.setPassword(new BCryptPasswordEncoder().encode("123456"));
        userRepository.save(user);
    }

    @Test
    public void delete() {
        userRepository.deleteById(2);
    }
}
