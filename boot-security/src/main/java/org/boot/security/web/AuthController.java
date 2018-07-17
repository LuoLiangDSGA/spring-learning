package org.boot.security.web;

import org.boot.security.model.User;
import org.boot.security.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author luoliang
 * @date 2018/7/9
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Resource
    private UserService userService;

    @PostMapping("login")
    public ResponseEntity<String> authLogin(String username, String password) {
        String token = userService.login(username, password);

        return ResponseEntity.ok(token);
    }

    @PostMapping("register")
    public ResponseEntity<User> userRegister(@RequestBody User addUser) {
        User user = userService.register(addUser);

        return ResponseEntity.ok(user);
    }

    @GetMapping("refresh")
    public ResponseEntity<String> refreshAndGetAuthenticationToken(HttpServletRequest request) {
        String oldToken = request.getHeader("token");
        String refreshToken = userService.refresh(oldToken);

        return ResponseEntity.ok(refreshToken);
    }
}
