package com._NguoiDev.SkillBridge.configuration;

import com._NguoiDev.SkillBridge.websocket.AuthHandshakeInterceptor;
import com._NguoiDev.SkillBridge.websocket.MySocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfigure implements WebSocketConfigurer {
    private final AuthHandshakeInterceptor authHandshakeInterceptor;
    private final MySocketHandler mySocketHandler;
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(mySocketHandler, "/websocket")
                .addInterceptors(authHandshakeInterceptor)
                .setAllowedOrigins("*");
    }

}
