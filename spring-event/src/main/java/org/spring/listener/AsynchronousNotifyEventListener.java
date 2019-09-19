package org.spring.listener;

import org.spring.event.AsynchronousNotifyEvent;
import org.spring.event.NotifyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author luoliang
 * @date 2019/9/18
 */
@Component
public class AsynchronousNotifyEventListener implements ApplicationListener<AsynchronousNotifyEvent> {

    @Override
    public void onApplicationEvent(AsynchronousNotifyEvent event) {
        System.out.println("Received notify event - " + event.getMessage());
        try {
            for (int i = 0; i < 5; i++) {
                System.out.println("process " + i + " 条数据");
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("process finished.");
    }
}
