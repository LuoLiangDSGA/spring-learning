package org.boot.transaction.dao;

import org.boot.transaction.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author luoliang
 * @date 2020/3/30
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

}
