package org.boot.webflux.service;

import org.boot.webflux.entity.User;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author luoliang
 * @date 2018/9/8
 */
public interface UserService {
    Mono<Boolean> register(String id, String username);

    Mono<ServerResponse> login(String username, String password);

    Flux<User> getAll();
}
