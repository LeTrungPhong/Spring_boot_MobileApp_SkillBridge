package com._NguoiDev.SkillBridge.controller;

import com._NguoiDev.SkillBridge.dto.request.AttendanceCheckRequest;
import com._NguoiDev.SkillBridge.dto.request.ClassAttendanceRequest;
import com._NguoiDev.SkillBridge.dto.response.AttendanceCheckResponse;
import com._NguoiDev.SkillBridge.entity.Class;
import com._NguoiDev.SkillBridge.entity.Lesson;
import com._NguoiDev.SkillBridge.exception.AppException;
import com._NguoiDev.SkillBridge.exception.ErrorCode;
import com._NguoiDev.SkillBridge.repository.ClassRepository;
import com._NguoiDev.SkillBridge.repository.LessonRepository;
import com._NguoiDev.SkillBridge.service.impl.AttendanceCheckServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceCheckController {

    private final AttendanceCheckServiceImpl attendanceCheckService;
    private final ClassRepository classRepository;
    private final LessonRepository lessonRepository;

    @PostMapping
    public ResponseEntity<AttendanceCheckResponse> checkAttendance(@RequestBody AttendanceCheckRequest request) {
        return new ResponseEntity<>(attendanceCheckService.checkAttendance(request), HttpStatus.CREATED);
    }
    
    @PostMapping("/check-by-class")
    public ResponseEntity<AttendanceCheckResponse> checkAttendanceByClass(@RequestBody ClassAttendanceRequest request) {
        // Find class
        Class classEntity = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new EntityNotFoundException("Class not found with id: " + request.getClassId()));
        
        // Find the current/next lesson for this class
        Lesson currentLesson = findCurrentOrUpcomingLesson(classEntity);
        if (currentLesson == null) {
            throw new AppException(ErrorCode.LESSON_NOT_FOUND);
        }
        
        // Create attendance check request with lesson ID
        AttendanceCheckRequest attendanceRequest = AttendanceCheckRequest.builder()
                .studentId(request.getStudentId())
                .lessonId(currentLesson.getId())
                .status(request.getStatus())
                .build();
        
        // Check attendance
        return new ResponseEntity<>(attendanceCheckService.checkAttendance(attendanceRequest), HttpStatus.CREATED);
    }

    @GetMapping("/student/{studentId}/{classId}")
    public ResponseEntity<List<AttendanceCheckResponse>> getAttendancesByStudent(@PathVariable int studentId, @PathVariable int classId) {
        return ResponseEntity.ok(attendanceCheckService.getAttendancesByStudent(studentId, classId));
    }

    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<List<AttendanceCheckResponse>> getAttendancesByLesson(@PathVariable int lessonId) {
        return ResponseEntity.ok(attendanceCheckService.getAttendancesByLesson(lessonId));
    }
    
    /**
     * Find the current lesson or the next upcoming lesson for a class
     */
    private Lesson findCurrentOrUpcomingLesson(Class classEntity) {
        LocalDateTime now = LocalDateTime.now();
        
        // First try to find a lesson happening right now
        for (Lesson lesson : classEntity.getLessons()) {
            if (lesson.getStartTime().isBefore(now) && lesson.getEndTime().isAfter(now)) {
                return lesson;
            }
        }
        
        // If no current lesson, find the next upcoming lesson
        Lesson nextLesson = null;
        LocalDateTime earliestStartTime = null;
        
        for (Lesson lesson : classEntity.getLessons()) {
            if (lesson.getStartTime().isAfter(now)) {
                if (earliestStartTime == null || lesson.getStartTime().isBefore(earliestStartTime)) {
                    earliestStartTime = lesson.getStartTime();
                    nextLesson = lesson;
                }
            }
        }
        
        return nextLesson;
    }
} 