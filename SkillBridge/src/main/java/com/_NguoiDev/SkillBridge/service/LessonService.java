package com._NguoiDev.SkillBridge.service;

import com._NguoiDev.SkillBridge.dto.response.LessonResponse;
import com._NguoiDev.SkillBridge.entity.Lesson;

import java.util.List;

public interface LessonService {
    List<LessonResponse> getLessonByIdClass(int idClass);
}
