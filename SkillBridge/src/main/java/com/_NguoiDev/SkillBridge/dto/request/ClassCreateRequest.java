package com._NguoiDev.SkillBridge.dto.request;

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
public class ClassCreateRequest {
    private String name;
    private int numberOfWeeks;
    private int teacherId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
} 