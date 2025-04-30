package com._NguoiDev.SkillBridge.service;

import com._NguoiDev.SkillBridge.dto.request.ChatHistoryRequest;
import com._NguoiDev.SkillBridge.dto.request.ChatRequest;
import com._NguoiDev.SkillBridge.dto.response.ChatResponse;
import com._NguoiDev.SkillBridge.entity.ChatMessage;
import com._NguoiDev.SkillBridge.exception.AppException;
import com._NguoiDev.SkillBridge.exception.ErrorCode;
import com._NguoiDev.SkillBridge.mapper.ChatMapper;
import com._NguoiDev.SkillBridge.repository.ChatMessageRepository;
import com._NguoiDev.SkillBridge.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatMessageService {
    ChatMessageRepository repository;
    UserRepository userRepository;
    ChatMapper chatMapper;

    public ChatResponse saveMessage(ChatRequest request) {
        ChatMessage chatMessage = ChatMessage.builder()
                .sender(userRepository.findByUsername(request.getSender()).orElseThrow(()->new AppException(ErrorCode.USER_NOT_EXISTED)))
                .receiver(userRepository.findByUsername(request.getReceiver()).orElseThrow(()->new AppException(ErrorCode.USER_NOT_EXISTED)))
                .message(request.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return chatMapper.ToChatResponse(repository.save(chatMessage));
    }

    public List<ChatResponse> getMessage(ChatHistoryRequest chatHistoryRequest){
        Pageable pageable = PageRequest.of(0,10, Sort.by("timestamp").descending());
        if (chatHistoryRequest.getLastTime() == null){
            chatHistoryRequest.setLastTime(LocalDateTime.now());
        }
        System.out.println(chatHistoryRequest.getLastTime());
        System.out.println(chatHistoryRequest.toString());
        return repository.getPreviousMessages(chatHistoryRequest.getUser1(), chatHistoryRequest.getUser2(), chatHistoryRequest.getLastTime()).stream().map(chatMapper::ToChatResponse).collect(Collectors.toList());
    }



}
