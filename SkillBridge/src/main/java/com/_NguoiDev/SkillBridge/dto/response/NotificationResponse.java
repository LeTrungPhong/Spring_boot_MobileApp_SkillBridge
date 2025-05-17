package com._NguoiDev.SkillBridge.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {
    private String title;
    private String body;
    private LocalDateTime createdAt;
    private int classId;
    private String className;
    private String type;
    private String assignmentId;
    private int postId;


}