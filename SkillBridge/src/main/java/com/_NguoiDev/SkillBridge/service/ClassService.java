package com._NguoiDev.SkillBridge.service;

import com._NguoiDev.SkillBridge.dto.request.ClassCreateRequest;
import com._NguoiDev.SkillBridge.dto.response.ClassResponse;

import java.util.List;

public interface ClassService {
    ClassResponse createClass(ClassCreateRequest request);
    ClassResponse getClassById(int id);
    List<ClassResponse> getAllClasses();
    List<ClassResponse> getClassesByTeacher();
    List<ClassResponse> getClassesByStudent();
} 