package org.boot.web;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.boot.config.valid.QueryAction;
import org.boot.config.valid.UpdateAction;
import org.boot.domain.User;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fantasy
 * @date 2020/7/30
 */
@RestController
@Validated
public class UserController {

    @PostMapping("/users")
    public ResponseEntity<String> addUser(@Validated(value = UpdateAction.class) @RequestBody User user) {
        System.out.println(user.toString());
        return ResponseEntity.ok("validate success");
    }

    @GetMapping("/v1/users")
    public ResponseEntity<String> getUsers(@NotBlank String name) {
        System.out.println(name);
        return ResponseEntity.ok("validate success");
    }

    @GetMapping("/v2/users")
    public ResponseEntity<String> getUsersUseGroup(@Validated(value = QueryAction.class) User user) {
        System.out.println(user.toString());
        return ResponseEntity.ok("validate success");
    }
}
