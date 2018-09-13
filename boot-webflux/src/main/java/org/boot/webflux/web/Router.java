package org.boot.webflux.web;

import org.boot.webflux.service.UserHandler;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;

import javax.annotation.Resource;

/**
 * @author luoliang
 * @date 2018/9/4
 */
@SpringBootConfiguration
public class Router {
    @Resource
    private UserHandler userHandler;

    @Bean
    public RouterFunction<?> routerFunction() {
        return RouterFunctions.route(RequestPredicates.GET("/hello"), userHandler::hello)
                .andRoute(RequestPredicates.POST("/login"), userHandler::login);
    }
}
