package com._NguoiDev.SkillBridge.websocket;

import com._NguoiDev.SkillBridge.dto.request.ChatRequest;
import com._NguoiDev.SkillBridge.service.ChatMessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class MySocketHandler extends TextWebSocketHandler {
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ChatMessageService chatMessageService;
    private final ObjectMapper mapper = new ObjectMapper();
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) session.getAttributes().get("user");
        String username = auth.getPrincipal().toString();
        sessions.remove(username);
        System.out.println("Ngắt kết nối WebSocket từ: " + username);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        System.out.println("Kết nối WebSocket được thiết lập: " + session.getId());
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) session.getAttributes().get("user");
        String username = auth.getPrincipal().toString();
        System.out.println("Kết nối WebSocket từ người dùng: " + username);

    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        super.handleMessage(session, message);
        try {
            String payload = message.getPayload().toString();
            ChatRequest request = mapper.readValue(payload, ChatRequest.class);
            System.out.println(request.toString());
            if (request.getType().equals("JOIN")){
                String receive = request.getReceiver();
                sessions.put(receive, session);
                System.out.println("so nguoi ket noi ws la:" +sessions.size());
            }else if (request.getType().equals("CHAT")){
                sendMessage(request, session);
            }

        } catch (JsonProcessingException e) {
            System.out.println("Lỗi khi parse tin nhắn: " + e.getMessage());
        }

    }
    public void sendMessage(ChatRequest request, WebSocketSession session) throws IOException {
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) session.getAttributes().get("user");
        String username = auth.getPrincipal().toString();
        request.setSender(username);
        chatMessageService.saveMessage(request);
        WebSocketSession webSocketSession = sessions.get(request.getReceiver());
        if(webSocketSession != null && webSocketSession.isOpen()){
            try {
                String json = mapper.writeValueAsString(request);
                webSocketSession.sendMessage(new TextMessage(json));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
