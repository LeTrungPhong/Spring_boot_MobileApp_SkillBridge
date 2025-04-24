package com._NguoiDev.SkillBridge.dto.response;

import com._NguoiDev.SkillBridge.enums.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceCheckResponse {
    private int studentId;
    private String studentName;
    private int lessonId;
    private LocalDateTime lessonDate;
    private LocalDateTime checkinDate;
    private AttendanceStatus status;
} 