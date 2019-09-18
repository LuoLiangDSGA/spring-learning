package org.spring.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author luoliang
 * @date 2019/9/17
 */
public class NotifyEvent extends ApplicationEvent {
    private String message;

    public NotifyEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
