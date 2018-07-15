package org.boot.security.web;

import org.boot.security.model.User;
import org.boot.security.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author luoliang
 * @date 2018/7/15
 */
@RestController
@RequestMapping("/users")
public class UserController {
    @Resource
    private UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PostAuthorize("returnObject.username == principal.username or hasRole('ROLE_ADMIN')")
    @GetMapping("/{username}")
    public User getUserByUsername(@PathVariable String username) {
        return userRepository.findUserByUsername(username);
    }
}
