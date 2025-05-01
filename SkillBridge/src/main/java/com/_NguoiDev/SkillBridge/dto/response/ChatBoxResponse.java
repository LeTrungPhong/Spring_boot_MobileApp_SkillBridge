package com._NguoiDev.SkillBridge.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatBoxResponse {
    private String myFriend;
    private String lastMessage;
    private LocalDateTime lastTime;
}
