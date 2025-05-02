package com._NguoiDev.SkillBridge.service;

import com._NguoiDev.SkillBridge.dto.request.ChatHistoryRequest;
import com._NguoiDev.SkillBridge.dto.request.ChatRequest;
import com._NguoiDev.SkillBridge.dto.response.ChatBoxResponse;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.ArrayList;
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
    private final ChatMessageRepository chatMessageRepository;

    public ChatResponse saveMessage(ChatRequest request) {
        ChatMessage chatMessage = ChatMessage.builder()
                .sender(userRepository.findByUsername(request.getSender()).orElseThrow(()->new AppException(ErrorCode.USER_NOT_EXISTED)))
                .receiver(userRepository.findByUsername(request.getReceiver()).orElseThrow(()->new AppException(ErrorCode.USER_NOT_EXISTED)))
                .message(request.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
//        if (!request.getSender().equals(SecurityContextHolder.getContext().getAuthentication().getName())){
//            throw new AppException(ErrorCode.SEND_MESSAGE_FAILED);
//        }
        return chatMapper.ToChatResponse(repository.save(chatMessage));
    }

    public List<ChatResponse> getMessage(ChatHistoryRequest chatHistoryRequest){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!username.equals(chatHistoryRequest.getUser1())&&!username.equals(chatHistoryRequest.getUser2())){
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }
        Pageable pageable = PageRequest.of(0,10, Sort.by("timestamp").descending());
        if (chatHistoryRequest.getLastTime() == null){
            chatHistoryRequest.setLastTime(LocalDateTime.now());
        }
        System.out.println(chatHistoryRequest.getLastTime());
        System.out.println(chatHistoryRequest.toString());
        return repository.getPreviousMessages(chatHistoryRequest.getUser1(), chatHistoryRequest.getUser2(), chatHistoryRequest.getLastTime(), pageable).stream().map(chatMapper::ToChatResponse).collect(Collectors.toList());
    }

    public List<ChatBoxResponse> getAllMyChatBoxes(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!userRepository.existsByUsername(username)){
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        List<ChatMessage> chatMessages = repository.getAllChatBox(username);
        List<ChatBoxResponse> chatBoxResponses = new ArrayList<>();
        for (ChatMessage chatMessage : chatMessages) {
            ChatBoxResponse chatBoxResponse = ChatBoxResponse.builder()
                    .lastMessage(chatMessage.getMessage())
                    .lastTime(chatMessage.getTimestamp())
                    .myFriend(chatMessage.getSender().getUsername().equals(username)?chatMessage.getReceiver().getUsername():chatMessage.getSender().getUsername())
                    .build();
            chatBoxResponses.add(chatBoxResponse);
        }
        return chatBoxResponses;
    }

}
