package org.boot.websocket.core;

import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LocalWsSessionManager implements IWsSessionManager{
    private Map<String, Session> cache = new ConcurrentHashMap<>();

    public Session get(String id) {
        return cache.get(id);
    }

    public void save(String id, Session session) {
        cache.put(id, session);
    }

    public void delete(String id) {
        cache.remove(id);
    }
}
