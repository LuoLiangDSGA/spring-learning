package org.spring.listener;

import org.spring.event.NotifyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author luoliang
 * @date 2019/9/17
 */
@Component
public class NotifyEventListener implements ApplicationListener<NotifyEvent> {

    @Override
    public void onApplicationEvent(NotifyEvent event) {
        System.out.println("Received notify event - " + event.getMessage());
        System.out.println("process finished.");
    }
}
