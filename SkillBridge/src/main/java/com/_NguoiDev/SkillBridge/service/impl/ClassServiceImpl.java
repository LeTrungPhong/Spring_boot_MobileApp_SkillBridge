package com._NguoiDev.SkillBridge.service.impl;

import com._NguoiDev.SkillBridge.dto.request.ClassCreateRequest;
import com._NguoiDev.SkillBridge.dto.request.LessonRequest;
import com._NguoiDev.SkillBridge.dto.response.ClassResponse;
import com._NguoiDev.SkillBridge.dto.response.LessonResponse;
import com._NguoiDev.SkillBridge.entity.Class;
import com._NguoiDev.SkillBridge.entity.Lesson;
import com._NguoiDev.SkillBridge.entity.Teacher;
import com._NguoiDev.SkillBridge.exception.AppException;
import com._NguoiDev.SkillBridge.exception.ErrorCode;
import com._NguoiDev.SkillBridge.repository.ClassRepository;
import com._NguoiDev.SkillBridge.repository.LessonRepository;
import com._NguoiDev.SkillBridge.repository.TeacherRepository;
import com._NguoiDev.SkillBridge.service.ClassService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassServiceImpl implements ClassService {

    private final ClassRepository classRepository;
    private final TeacherRepository teacherRepository;
    private final LessonRepository lessonRepository;

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    public ClassResponse createClass(ClassCreateRequest request) {
        Teacher teacher = teacherRepository.findByUserUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(()->new AppException(ErrorCode.TEACHER_NOT_EXISTED));
        // Create class
        Class classEntity = Class.builder()
                .name(request.getName())
                .number_of_weeks(request.getNumberOfWeeks())
                .createdAt(LocalDateTime.now())
                .teacher(teacher)
                .lessons(new ArrayList<>())
                .build();
        
        // Save class
        Class savedClass = classRepository.save(classEntity);
        
        // Generate lessons for each week
        List<Lesson> lessons = generateLessonsForClass(savedClass, request.getStartTime(), 
                request.getEndTime(), request.getNumberOfWeeks());
        
        savedClass.setLessons(lessons);
        lessonRepository.saveAll(lessons);
        
        return mapToClassResponse(savedClass);
    }

    private List<Lesson> generateLessonsForClass(Class classEntity, LocalDateTime startDateTime,
                                               LocalDateTime endDateTime, int numberOfWeeks) {
        List<Lesson> lessons = new ArrayList<>();
        
        // Get the day of week and date from the start time
        LocalDateTime firstLessonDate = startDateTime;
        
        // Generate lessons for each week
        for (int weekNumber = 0; weekNumber < numberOfWeeks; weekNumber++) {
            // Calculate the date for this week's lesson by adding 7 days for each week
            LocalDateTime lessonStartDateTime = firstLessonDate.plusWeeks(weekNumber);
            LocalDateTime lessonEndDateTime = endDateTime.plusWeeks(weekNumber);
            
            // Create lesson for this week
            Lesson lesson = Lesson.builder()
                    .classEntity(classEntity)
                    .lessonDate(lessonStartDateTime.toLocalDate().atStartOfDay()) // Just the date part at midnight
                    .startTime(lessonStartDateTime)
                    .endTime(lessonEndDateTime)
                    .isCompleted(false)
                    .room("TBD") // Default room, can be updated later
                    .notes("Lesson for week " + (weekNumber + 1))
                    .build();
            
            lessons.add(lesson);
        }
        
        return lessons;
    }

    @Override
    public ClassResponse getClassById(int id) {
        Class classEntity = classRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Class not found with id: " + id));
        return mapToClassResponse(classEntity);
    }

    @Override
    public List<ClassResponse> getAllClasses() {
        return classRepository.findAll().stream()
                .map(this::mapToClassResponse)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    public List<ClassResponse> getClassesByTeacher() {
        Teacher teacher = teacherRepository.findByUserUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(()->new AppException(ErrorCode.TEACHER_NOT_EXISTED));
        
        return classRepository.findByTeacher(teacher).stream()
                .map(this::mapToClassResponse)
                .collect(Collectors.toList());
    }

    private LessonResponse mapToLessonResponse(Lesson lesson) {
        return LessonResponse.builder()
                .id(lesson.getId())
                .endTime(lesson.getEndTime())
                .isCompleted(lesson.isCompleted())
                .lessonDate(lesson.getLessonDate())
                .startTime(lesson.getStartTime())
                .notes(lesson.getNotes())
                .room(lesson.getRoom())
                .build();
    }


    private ClassResponse mapToClassResponse(Class classEntity) {
        List<LessonResponse> lessonResponses = new ArrayList<>();
        if (classEntity.getLessons() != null) {
            lessonResponses = classEntity.getLessons().stream()
                    .map(this::mapToLessonResponse)
                    .collect(Collectors.toList());
        }
        
        return ClassResponse.builder()
                .id(classEntity.getId())
                .name(classEntity.getName())
                .numberOfWeeks(classEntity.getNumber_of_weeks())
                .createdAt(classEntity.getCreatedAt())
                .lessons(lessonResponses)
                .build();
    }
} 