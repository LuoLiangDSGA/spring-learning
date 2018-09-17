package org.boot.webflux.service;

import com.alibaba.fastjson.JSON;
import org.boot.webflux.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.ReactiveRedisConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author luoliang
 * @date 2018/9/4
 */
@Service
public class UserHandler {
    private final static Logger log = LoggerFactory.getLogger(UserHandler.class);
    @Resource
    private ReactiveRedisConnection connection;

    public Mono<ServerResponse> hello(ServerRequest request) {
        return ServerResponse
                .ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(BodyInserters.fromObject("Hello, World"));
    }

    /**
     * 登录
     *
     * @param request
     * @return
     */
    public Mono<ServerResponse> login(ServerRequest request) {
        Mono<Map> body = request.bodyToMono(Map.class);
        return body.flatMap(map -> {
            String username = (String) map.get("username");
            String password = (String) map.get("password");
            log.debug("username:{},password:{}", username, password);
            return connection.stringCommands().get(
                    ByteBuffer.wrap(username.getBytes()))
                    .flatMap(byteBuffer -> {
                        byte[] bytes = new byte[byteBuffer.remaining()];
                        byteBuffer.get(bytes, 0, bytes.length);
                        String userStr;
                        userStr = new String(bytes, StandardCharsets.UTF_8);
                        log.debug(userStr);
                        User user = JSON.parseObject(userStr, User.class);
                        Map<String, String> result = new HashMap<>(2);
                        if (Objects.isNull(user.getPassword()) || !user.getPassword().equals(password)) {
                            result.put("message", "账号或密码错误");
                            log.debug("账号或密码错误");
                            return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                                    .body(BodyInserters.fromObject(result));
                        } else {
                            result.put("message", "登录成功");
                            log.debug("登录成功");
                            return ServerResponse.ok()
                                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                                    .body(BodyInserters.fromObject(result));
                        }
                    });
        });
    }
}
