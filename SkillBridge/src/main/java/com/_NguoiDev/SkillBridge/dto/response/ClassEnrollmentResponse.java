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
public class ClassEnrollmentResponse {
    private int studentId;
    private String studentName;
    private int classId;
    private String className;
    private LocalDateTime joinedAt;
    private boolean active;
} 