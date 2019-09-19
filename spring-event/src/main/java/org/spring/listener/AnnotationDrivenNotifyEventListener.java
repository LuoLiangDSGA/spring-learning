package org.spring.listener;

import org.spring.event.AnnotationDrivenNotifyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author luoliang
 * @date 2019/9/18
 */
@Component
public class AnnotationDrivenNotifyEventListener {

    @Async
    @EventListener
    public void receive(AnnotationDrivenNotifyEvent event) {
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
