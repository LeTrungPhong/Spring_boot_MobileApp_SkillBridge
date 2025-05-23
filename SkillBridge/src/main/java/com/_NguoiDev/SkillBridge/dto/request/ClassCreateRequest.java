package com._NguoiDev.SkillBridge.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClassCreateRequest {
    private String name;
    private LocalTime startTime;
    private int numberOfWeeks;
//    private int teacherId;
    Map<String, LessonRangeRequest> dateStudy;
} 