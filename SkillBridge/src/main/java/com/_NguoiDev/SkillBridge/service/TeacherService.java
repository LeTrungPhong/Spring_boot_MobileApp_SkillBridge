package com._NguoiDev.SkillBridge.service;

import com._NguoiDev.SkillBridge.dto.request.TeacherCreateRequest;
import com._NguoiDev.SkillBridge.dto.response.TeacherResponse;

import java.util.List;

public interface TeacherService {
    TeacherResponse createTeacher(TeacherCreateRequest request);
    TeacherResponse getTeacherById(int id);
    List<TeacherResponse> getAllTeachers();
    TeacherResponse updateTeacher(int id, TeacherCreateRequest request);
    void deleteTeacher(int id);
} 