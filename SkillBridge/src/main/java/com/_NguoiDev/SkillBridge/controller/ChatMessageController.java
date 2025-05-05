package com._NguoiDev.SkillBridge.controller;

import com._NguoiDev.SkillBridge.dto.request.ChatHistoryRequest;
import com._NguoiDev.SkillBridge.dto.request.ChatRequest;
import com._NguoiDev.SkillBridge.dto.response.ApiResponse;
import com._NguoiDev.SkillBridge.dto.response.ChatBoxResponse;
import com._NguoiDev.SkillBridge.dto.response.ChatResponse;
import com._NguoiDev.SkillBridge.entity.ChatMessage;
import com._NguoiDev.SkillBridge.repository.ChatMessageRepository;
import com._NguoiDev.SkillBridge.service.ChatMessageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatMessageController {
    ChatMessageService chatMessageService;

    @PostMapping("/api/chat/send")
    public ApiResponse<ChatResponse> send(@RequestBody ChatRequest chatRequest) {

        return ApiResponse.<ChatResponse>builder()
                .message("success")
                .code(1000)
                .result(chatMessageService.saveMessage(chatRequest))
                .build();
    }

    @GetMapping("/api/message")
    public ApiResponse<List<ChatResponse>> getMessage(@RequestParam String user1,
                                                      @RequestParam String user2,
                                                      @RequestParam(required = false)
                                                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastTime) {
        ChatHistoryRequest request = ChatHistoryRequest.builder()
                .user1(user1)
                .user2(user2)
                .lastTime(lastTime)
                .build();

        return ApiResponse.<List<ChatResponse>>builder()
                .code(1000)
                .message("success")
                .result(chatMessageService.getMessage(request))
                .build();
    }

    @GetMapping("/api/chat")
    public ApiResponse<List<ChatBoxResponse>> getAllMyChatBoxes() {
        return ApiResponse.<List<ChatBoxResponse>>builder()
                .code(1000)
                .message("success")
                .result(chatMessageService.getAllMyChatBoxes())
                .build();
    }

    @GetMapping("/api/lastmessage")
    public ApiResponse<ChatResponse> getMessage(@RequestParam String user1,
                                                @RequestParam String user2) {
        ChatHistoryRequest request = ChatHistoryRequest.builder()
                .user1(user1)
                .user2(user2)
                .build();

        return ApiResponse.<ChatResponse>builder()
                .code(1000)
                .message("success")
                .result(chatMessageService.getLastMessage(request))
                .build();
    }
}
