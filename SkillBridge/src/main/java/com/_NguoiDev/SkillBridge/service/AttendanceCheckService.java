package com._NguoiDev.SkillBridge.service;

import com._NguoiDev.SkillBridge.dto.request.AttendanceCheckRequest;
import com._NguoiDev.SkillBridge.dto.response.AttendanceCheckResponse;

import java.util.List;

public interface AttendanceCheckService {
    AttendanceCheckResponse checkAttendance(AttendanceCheckRequest request);
    List<AttendanceCheckResponse> getAttendancesByStudent(int studentId);
    List<AttendanceCheckResponse> getAttendancesByLesson(int lessonId);
} 