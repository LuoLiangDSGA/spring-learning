package org.boot.security.repository;

import org.boot.security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author luoliang
 * @date 2018/7/8
 */
public interface UserRepository extends JpaRepository<User, Integer> {
    User findUserByUsername(String username);
}
