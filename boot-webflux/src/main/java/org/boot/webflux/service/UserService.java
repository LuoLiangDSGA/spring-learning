package org.boot.webflux.service;

import org.boot.webflux.entity.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author luoliang
 * @date 2018/9/8
 */
public interface UserService {
    /**
     * 用户注册
     *
     * @param id
     * @param username
     * @return
     */
    Mono<Boolean> add(String id, String username);

    /**
     * 用户登录
     *
     * @param username
     * @param password
     * @return
     */
    Mono<User> find(String username, String password);

    /**
     * 获取所有用户
     *
     * @return
     */
    Flux<User> getAll();

    Mono<Boolean> remove(String id);


}
