package org.spring;

import org.spring.event.NotifyEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author luoliang
 */
@SpringBootApplication
@EnableAsync
public class SpringEventApplication implements CommandLineRunner {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public static void main(String[] args) {
        SpringApplication.run(SpringEventApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String message = "start publish application event. ";
        System.out.println(message);
        applicationEventPublisher.publishEvent(new NotifyEvent(this, message));
        System.out.println("publish finished.");
    }
}
