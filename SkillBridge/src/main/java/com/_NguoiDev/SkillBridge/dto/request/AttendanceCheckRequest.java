package com._NguoiDev.SkillBridge.dto.request;

import com._NguoiDev.SkillBridge.enums.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceCheckRequest {
    private int studentId;
    private int lessonId;
    private AttendanceStatus status;
} 