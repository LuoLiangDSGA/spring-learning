package org.boot.mongo.repository;

import org.boot.mongo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

/**
 * @author luoliang
 * @date 2019/9/24
 */
public interface UserRepository extends MongoRepository<User, String> {
    /**
     * 按名称进行查询
     *
     * @param username
     * @return
     */
    User findUserByUsername(String username);

    /**
     * 自定义查询语句，根据日期查询
     *
     * @param create
     * @param pageable
     * @return
     */
    @Query("{'gmtCreate': ?0}")
    Page<User> queryBySql(String create, Pageable pageable);
}
