package com._NguoiDev.SkillBridge.controller;

import com._NguoiDev.SkillBridge.dto.response.ApiResponse;
import com._NguoiDev.SkillBridge.dto.response.LessonResponse;
import com._NguoiDev.SkillBridge.entity.Lesson;
import com._NguoiDev.SkillBridge.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/lesson")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @GetMapping("/{idClass}")
    public List<LessonResponse> getLessonByIdClass(@PathVariable int idClass) {
        return lessonService.getLessonByIdClass(idClass);
    }
    
}
