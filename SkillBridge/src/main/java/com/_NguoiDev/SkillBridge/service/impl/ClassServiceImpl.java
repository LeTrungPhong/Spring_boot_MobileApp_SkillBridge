package com._NguoiDev.SkillBridge.service.impl;

import com._NguoiDev.SkillBridge.dto.request.ClassCreateRequest;
import com._NguoiDev.SkillBridge.dto.request.LessonRangeRequest;
import com._NguoiDev.SkillBridge.dto.request.LessonRequest;
import com._NguoiDev.SkillBridge.dto.response.ClassResponse;
import com._NguoiDev.SkillBridge.dto.response.LessonResponse;
import com._NguoiDev.SkillBridge.entity.*;
import com._NguoiDev.SkillBridge.entity.Class;
import com._NguoiDev.SkillBridge.exception.AppException;
import com._NguoiDev.SkillBridge.exception.ErrorCode;
import com._NguoiDev.SkillBridge.repository.*;
import com._NguoiDev.SkillBridge.service.ClassService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassServiceImpl implements ClassService {

    private final ClassRepository classRepository;
    private final TeacherRepository teacherRepository;
    private final LessonRepository lessonRepository;
    private final StudentRepository studentRepository;
    private final StudentClassRepository studentClassRepository;

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
                .build();
        List<Lesson> lessons =  generateLessonsForClass(classEntity, request.getDateStudy(), request.getNumberOfWeeks());
        classEntity.setLessons(lessons);
        // Save class
        Class savedClass = classRepository.save(classEntity);

        
        return mapToClassResponse(savedClass);
    }

    private List<Lesson> generateLessonsForClass(Class classEntity, Map<String, LessonRangeRequest> dateStudy, int numberOfWeeks) {
        LocalDate today = LocalDate.now();
        List<Lesson> lessons = new ArrayList<>();
        for( int week = 0; week < numberOfWeeks; week++) {
            for (Map.Entry<String, LessonRangeRequest> day: dateStudy.entrySet()){
                DayOfWeek dayOfWeek = DayOfWeek.valueOf(day.getKey().toUpperCase());
                LocalTime startTime =day.getValue().getStartTime();
                LocalTime endTime =day.getValue().getEndTime();

                LocalDate lessonDate = today.plusWeeks(week);
                int dayToAdd = dayOfWeek.getValue()-lessonDate.getDayOfWeek().getValue();
                if (dayToAdd <= 0) {
                    dayToAdd+=7;
                }
                lessonDate = lessonDate.plusDays(dayToAdd);

                Lesson newLesson = Lesson.builder()
                        .classEntity(classEntity)
                        .lessonDate(lessonDate.atStartOfDay())
                        .startTime(lessonDate.atTime(startTime))
                        .endTime(lessonDate.atTime(endTime))
                        .isCompleted(false)
                        .room("TBD") // Default room, can be updated later
                        .notes("Lesson for week " + (week + 1))
                        .build();
                lessons.add(newLesson);
            }
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

    @Override
    public List<ClassResponse> getClassesByStudent() {
        Student student = studentRepository.findByUserUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(()->new AppException(ErrorCode.STUDENT_NOT_EXISTED));

        List<StudentClass> listStudentClass = studentClassRepository.findByStudent(student);
        List<ClassResponse> listClassResponse = new ArrayList<>();
        for (StudentClass studentClass : listStudentClass) {
            listClassResponse.add(mapToClassResponse(studentClass.getClassEntity()));
        }
        return listClassResponse;
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
                .createdBy(SecurityContextHolder.getContext().getAuthentication().getName())
                .lessons(lessonResponses)
                .build();
    }
} 