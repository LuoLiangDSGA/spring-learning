package org.boot.websocket.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeFailureException;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.security.Principal;
import java.util.Map;

@SpringBootConfiguration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);
    private final static long HEART_BEAT = 10000;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                // 首次连接
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String username = accessor.getNativeHeader("username").get(0);
                    String password = accessor.getNativeHeader("password").get(0);
                    if ("admin".equals(username) && "admin".equals(password)) {
                        Principal principal = () -> username;
                        accessor.setUser(principal);
                        return message;
                    } else {
                        return null;
                    }
                }
                return message;
            }

            @Override
            public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
                ChannelInterceptor.super.postSend(message, channel, sent);
            }

            @Override
            public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
                ChannelInterceptor.super.afterSendCompletion(message, channel, sent, ex);
            }

            @Override
            public boolean preReceive(MessageChannel channel) {
                return ChannelInterceptor.super.preReceive(channel);
            }

            @Override
            public Message<?> postReceive(Message<?> message, MessageChannel channel) {
                return ChannelInterceptor.super.postReceive(message, channel);
            }

            @Override
            public void afterReceiveCompletion(Message<?> message, MessageChannel channel, Exception ex) {
                ChannelInterceptor.super.afterReceiveCompletion(message, channel, ex);
            }
        });
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        HttpSessionHandshakeInterceptor httpSessionHandshakeInterceptor = new HttpSessionHandshakeInterceptor();
        httpSessionHandshakeInterceptor.setCreateSession(true);
        registry.addEndpoint("/websocket") // 设置访问端点
                .setHandshakeHandler(new HandshakeHandler() {
                    @Override
                    public boolean doHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws HandshakeFailureException {
                        return true;
                    }
                }).addInterceptors(httpSessionHandshakeInterceptor)
                .setAllowedOrigins("*") // 允许跨域
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 此线程用于检测客户端和服务端的心跳
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("ws-heartbeat-thread-");
        scheduler.initialize();
        registry.enableSimpleBroker("/topic", "/queue").setHeartbeatValue(new long[]{HEART_BEAT, HEART_BEAT}).setTaskScheduler(scheduler);
        registry.setApplicationDestinationPrefixes("/app");
    }
}
