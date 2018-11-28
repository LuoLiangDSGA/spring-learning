package org.spring.scope.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

/**
 * @author luoliang
 * @date 2018/11/27
 */
@Service
@Scope(WebApplicationContext.SCOPE_SESSION)
public class SessionService {
    private UUID uuid;

    public SessionService() {
        this.uuid = UUID.randomUUID();
    }

    public void printId() {
        System.out.println("SessionBean:" + uuid);
    }
}
