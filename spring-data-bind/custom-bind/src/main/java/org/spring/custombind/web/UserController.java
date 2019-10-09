package org.spring.custombind.web;

import org.spring.custombind.annotation.Token;
import org.spring.custombind.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author luoliang
 * @date 2019/10/8
 */
@RestController
public class UserController {

    @GetMapping("/user")
    public ResponseEntity<User> getUser(@Token User user) {
        return ResponseEntity.ok(user);
    }

}
