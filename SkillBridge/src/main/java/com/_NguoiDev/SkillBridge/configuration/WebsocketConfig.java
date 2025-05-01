//package com._NguoiDev.SkillBridge.configuration;
//
//
//import com._NguoiDev.SkillBridge.controller.ChannelInterceptorAdapter;
//import com._NguoiDev.SkillBridge.service.AuthenticationService;
//import com.nimbusds.jose.JWSVerifier;
//import com.nimbusds.jose.crypto.MACVerifier;
//import com.nimbusds.jwt.SignedJWT;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.simp.config.ChannelRegistration;
//import org.springframework.messaging.simp.config.MessageBrokerRegistry;
//import org.springframework.messaging.simp.stomp.StompCommand;
//import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
//import org.springframework.messaging.support.ChannelInterceptor;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
//import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
//import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
//
//import java.util.Collections;
//
//@Configuration
//@EnableWebSocketMessageBroker
//@RequiredArgsConstructor
//public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {
//    private final  ChannelInterceptorAdapter channelInterceptorAdapter;
//
//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
////        WebSocketMessageBrokerConfigurer.super.configureClientInboundChannel(registration);
//        registration.interceptors(channelInterceptorAdapter);
//    }
//
//
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
////        WebSocketMessageBrokerConfigurer.super.registerStompEndpoints(registry);
//        System.out.println("⚙️ Registering STOMP endpoint");
//        registry.addEndpoint("/ws")
//                .setAllowedOrigins("*")
//                .withSockJS()
//                .setWebSocketEnabled(true)
//                .setSuppressCors(false);
//    }
//
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry registry) {
////        WebSocketMessageBrokerConfigurer.super.configureMessageBroker(registry);
//        registry.setApplicationDestinationPrefixes("/app/chat");
//        registry.enableSimpleBroker("/topic", "/queue");
//    }
//
//
//}
