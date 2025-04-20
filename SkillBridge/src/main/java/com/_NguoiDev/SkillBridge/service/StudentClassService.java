package com._NguoiDev.SkillBridge.service;

import com._NguoiDev.SkillBridge.dto.request.ClassEnrollmentRequest;
import com._NguoiDev.SkillBridge.dto.response.ClassEnrollmentResponse;

import java.util.List;

public interface StudentClassService {
    ClassEnrollmentResponse enrollStudentInClass(ClassEnrollmentRequest request);
    List<ClassEnrollmentResponse> getClassesByStudent(int studentId);
    List<ClassEnrollmentResponse> getStudentsByClass(int classId);
    void removeStudentFromClass(int studentId, int classId);
} 