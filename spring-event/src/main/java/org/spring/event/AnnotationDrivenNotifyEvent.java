package org.spring.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author luoliang
 * @date 2019/9/18
 */
public class AnnotationDrivenNotifyEvent extends ApplicationEvent {
    private String message;

    public AnnotationDrivenNotifyEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
