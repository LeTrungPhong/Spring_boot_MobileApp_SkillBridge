package com._NguoiDev.SkillBridge.mapper;

import com._NguoiDev.SkillBridge.dto.response.ChatResponse;
import com._NguoiDev.SkillBridge.entity.ChatMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatMapper {
    @Mapping(target = "sender", source = "sender.username")
    @Mapping(target = "receiver", source = "receiver.username")
    ChatResponse ToChatResponse(ChatMessage chatMessage) ;
}
