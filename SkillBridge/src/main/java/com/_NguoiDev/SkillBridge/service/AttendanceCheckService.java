package com._NguoiDev.SkillBridge.service;

import com._NguoiDev.SkillBridge.dto.request.AttendanceCheckRequest;
import com._NguoiDev.SkillBridge.dto.response.AttendanceCheckResponse;
import com._NguoiDev.SkillBridge.entity.AttendanceCheck;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AttendanceCheckService {
    AttendanceCheckResponse checkAttendance(AttendanceCheckRequest request);
    List<AttendanceCheckResponse> getAttendancesByStudent(int studentId, int classId);
    List<AttendanceCheckResponse> getAttendancesByLesson(int lessonId);
} 