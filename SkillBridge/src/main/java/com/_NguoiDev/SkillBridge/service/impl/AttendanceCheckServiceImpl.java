package com._NguoiDev.SkillBridge.service.impl;

import com._NguoiDev.SkillBridge.dto.request.AttendanceCheckRequest;
import com._NguoiDev.SkillBridge.dto.response.AttendanceCheckResponse;
import com._NguoiDev.SkillBridge.embedded.AttendanceCheckId;
import com._NguoiDev.SkillBridge.entity.AttendanceCheck;
import com._NguoiDev.SkillBridge.entity.Lesson;
import com._NguoiDev.SkillBridge.entity.Student;
import com._NguoiDev.SkillBridge.enums.AttendanceStatus;
import com._NguoiDev.SkillBridge.exception.AppException;
import com._NguoiDev.SkillBridge.exception.ErrorCode;
import com._NguoiDev.SkillBridge.repository.AttendanceCheckRepository;
import com._NguoiDev.SkillBridge.repository.LessonRepository;
import com._NguoiDev.SkillBridge.repository.StudentRepository;
import com._NguoiDev.SkillBridge.service.AttendanceCheckService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceCheckServiceImpl implements AttendanceCheckService {

    private final AttendanceCheckRepository attendanceCheckRepository;
    private final StudentRepository studentRepository;
    private final LessonRepository lessonRepository;

    @Override
    @Transactional
    public AttendanceCheckResponse checkAttendance(AttendanceCheckRequest request) {
        // Check if student exists
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_EXISTED));
        
        // Check if lesson exists
        Lesson lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found with id: " + request.getLessonId()));
        
        // Create or update attendance check
        AttendanceCheck attendanceCheck;
        if (attendanceCheckRepository.existsByStudentAndLesson(student, lesson)) {
            // Update existing attendance
            attendanceCheck = attendanceCheckRepository.findByStudentAndLesson(student, lesson)
                    .orElseThrow(() -> new IllegalStateException("Attendance check not found despite existence check"));
            attendanceCheck.setStatus(request.getStatus());
            attendanceCheck.setCheckinDate(LocalDateTime.now());
        } else {
            // Create new attendance check
            AttendanceCheckId attendanceCheckId = AttendanceCheckId.builder()
                    .studentId(student.getId())
                    .lessonId(lesson.getId())
                    .build();
            
            attendanceCheck = AttendanceCheck.builder()
                    .id(attendanceCheckId)
                    .student(student)
                    .lesson(lesson)
                    .checkinDate(LocalDateTime.now())
                    .status(request.getStatus() != null ? request.getStatus() : AttendanceStatus.PRESENT)
                    .build();
        }
        
        // Save attendance check
        AttendanceCheck savedAttendanceCheck = attendanceCheckRepository.save(attendanceCheck);
        
        // Return response
        return mapToAttendanceCheckResponse(savedAttendanceCheck);
    }

    @Override
    public List<AttendanceCheckResponse> getAttendancesByStudent(int studentId) {
        // Check if student exists
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_EXISTED));
        
        // Get attendances for student
        return attendanceCheckRepository.findByStudent(student).stream()
                .map(this::mapToAttendanceCheckResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceCheckResponse> getAttendancesByLesson(int lessonId) {
        // Check if lesson exists
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found with id: " + lessonId));
        
        // Get attendances for lesson
        return attendanceCheckRepository.findByLesson(lesson).stream()
                .map(this::mapToAttendanceCheckResponse)
                .collect(Collectors.toList());
    }
    
    private AttendanceCheckResponse mapToAttendanceCheckResponse(AttendanceCheck attendanceCheck) {
        return AttendanceCheckResponse.builder()
                .studentId(attendanceCheck.getStudent().getId())
                .studentName(attendanceCheck.getStudent().getName())
                .lessonId(attendanceCheck.getLesson().getId())
                .lessonDate(attendanceCheck.getLesson().getLessonDate())
                .checkinDate(attendanceCheck.getCheckinDate())
                .status(attendanceCheck.getStatus())
                .build();
    }
} 