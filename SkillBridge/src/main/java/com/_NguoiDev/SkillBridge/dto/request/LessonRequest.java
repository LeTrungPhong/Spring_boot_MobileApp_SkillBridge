package com._NguoiDev.SkillBridge.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LessonRequest {
    private LocalDateTime endTime;
    private boolean isCompleted;
    private LocalDateTime lessonDate;
    private LocalDateTime startTime;
    private String notes;
    private String room;
} 