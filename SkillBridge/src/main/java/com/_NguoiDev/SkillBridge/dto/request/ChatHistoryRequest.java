package com._NguoiDev.SkillBridge.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatHistoryRequest {
    private String user1;
    private String user2;
    private LocalDateTime lastTime;
}
