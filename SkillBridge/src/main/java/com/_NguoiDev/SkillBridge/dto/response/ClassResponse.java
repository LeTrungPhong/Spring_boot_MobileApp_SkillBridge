package com._NguoiDev.SkillBridge.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClassResponse {
    private int id;
    private String name;
    private int numberOfWeeks;
    private LocalDateTime createdAt;
    private String createdBy;
    private List<LessonResponse> lessons;
} 