package com._NguoiDev.SkillBridge.service.impl;

import com._NguoiDev.SkillBridge.dto.response.LessonResponse;
import com._NguoiDev.SkillBridge.entity.Lesson;
import com._NguoiDev.SkillBridge.repository.LessonRepository;
import com._NguoiDev.SkillBridge.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;


    @Override
    public List<LessonResponse> getLessonByIdClass(int idClass) {
        return lessonRepository.findByClassEntityId(idClass).stream()
                .map(this::mapToLLessonResponse)
                .collect(Collectors.toList());
    }

    private LessonResponse mapToLLessonResponse(Lesson lessonEntity) {
        return LessonResponse.builder()
                .id(lessonEntity.getId())
                .isCompleted(lessonEntity.isCompleted())
                .lessonDate(lessonEntity.getLessonDate())
                .startTime(lessonEntity.getStartTime())
                .endTime(lessonEntity.getEndTime())
                .notes(lessonEntity.getNotes())
                .room(lessonEntity.getRoom())
                .build();
    }
}
