package com._NguoiDev.SkillBridge.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LessonResponse {
    private int id;
    private boolean isCompleted;
    private LocalDateTime lessonDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String notes;
    private String room;
} 