package org.boot.websocket.web;

import org.boot.websocket.model.Greeting;
import org.boot.websocket.model.HelloMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class MessageController {
    private final Logger logger = LoggerFactory.getLogger(MessageController.class);
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    //  客户端可以有多种不同的方式，发送消息给server，包括 SUBSCRIBE和 SEND.
    //  @SubscribeMapping("/topic/topic1") 标注的方法，只会处理SUBSCRIBE发送的消息。
    //  @MessageMapping("/topic/topic1") 标注的方法，只会处理SEND发送的消息。
    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(HelloMessage message) throws InterruptedException {
        Thread.sleep(1000);
        return new Greeting("Hello," + HtmlUtils.htmlEscape(message.getName()) + "!");
    }

    @SubscribeMapping("/subscribe")
    public String subscribeByWebsocket() {
        LocalDateTime dateTime = LocalDateTime.now();
        return getFormat(dateTime);
    }

    @MessageMapping("/helloToUser")
    @SendToUser("/queue/notify")
    public Greeting sendToUser(HelloMessage message, Principal principal) {
        String name = principal.getName();
        logger.info("get user name: {}", name);
        return new Greeting("Hello," + HtmlUtils.htmlEscape(message.getName()) + "!");
    }

//    @MessageMapping("/helloToUser")
//    public void sendToUser(HelloMessage message, StompHeaderAccessor stompHeaderAccessor) {
//        Principal user = stompHeaderAccessor.getUser();
//        logger.info("get user: {}", user);
//        simpMessagingTemplate.convertAndSendToUser(user.getName(), "/queue/notify", new Greeting("Hello," + HtmlUtils.htmlEscape(message.getName()) + "!"));
//    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleException(Throwable exception) {
        return exception.getMessage();
    }

    //    @Scheduled(cron = "0/1 * * * * ?")
    public void push() {
        logger.info("execute push, destination: {}", "/topic/subscribe");
        // 判断连接是否断掉
//        if (!isAlive) return;
        LocalDateTime dateTime = LocalDateTime.now();
        simpMessagingTemplate.convertAndSend("/topic/subscribe", getFormat(dateTime));
    }

    private String getFormat(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}




