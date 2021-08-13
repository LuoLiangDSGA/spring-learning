package org.boot.websocket.core;

import javax.websocket.Session;

public interface IWsSessionManager {
    Session get(String id);

    void save(String id, Session session);

    void delete(String id);
}
