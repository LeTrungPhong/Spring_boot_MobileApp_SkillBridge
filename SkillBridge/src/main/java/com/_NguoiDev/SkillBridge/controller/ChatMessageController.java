package com._NguoiDev.SkillBridge.controller;

import com._NguoiDev.SkillBridge.dto.request.ChatHistoryRequest;
import com._NguoiDev.SkillBridge.dto.request.ChatRequest;
import com._NguoiDev.SkillBridge.dto.response.ApiResponse;
import com._NguoiDev.SkillBridge.dto.response.ChatResponse;
import com._NguoiDev.SkillBridge.entity.ChatMessage;
import com._NguoiDev.SkillBridge.repository.ChatMessageRepository;
import com._NguoiDev.SkillBridge.service.ChatMessageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/api/chat")
    public ApiResponse<List<ChatResponse>> getMessage(@RequestBody ChatHistoryRequest request) {
        return ApiResponse.<List<ChatResponse>>builder()
                .code(1000)
                .message("success")
                .result(chatMessageService.getMessage(request))
                .build();
    }


}
