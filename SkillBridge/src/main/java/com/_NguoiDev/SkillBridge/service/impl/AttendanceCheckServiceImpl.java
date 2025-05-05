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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceCheckServiceImpl implements AttendanceCheckService {

    private final AttendanceCheckRepository attendanceCheckRepository;
    private final StudentRepository studentRepository;
    private final LessonRepository lessonRepository;
    
    // Define constant for late threshold (30 minutes in minutes)
    private static final int LATE_THRESHOLD_MINUTES = 30;

    @Override
    @Transactional
    public AttendanceCheckResponse checkAttendance(AttendanceCheckRequest request) {
        // Check if student exists
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_EXISTED));
        
        // Check if lesson exists
        Lesson lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found with id: " + request.getLessonId()));
        
        // Get current time for check-in
        LocalDateTime now = LocalDateTime.now();
        
        // Determine status based on request or calculate based on check-in time
        AttendanceStatus status = determineAttendanceStatus(request.getStatus(), lesson, now);
        
        // Create or update attendance check
        AttendanceCheck attendanceCheck;
        if (attendanceCheckRepository.existsByStudentAndLesson(student, lesson)) {
            // Update existing attendance
            attendanceCheck = attendanceCheckRepository.findByStudentAndLesson(student, lesson)
                    .orElseThrow(() -> new IllegalStateException("Attendance check not found despite existence check"));
            attendanceCheck.setStatus(status);
            attendanceCheck.setCheckinDate(now);
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
                    .checkinDate(now)
                    .status(status)
                    .build();
        }
        
        // Save attendance check
        AttendanceCheck savedAttendanceCheck = attendanceCheckRepository.save(attendanceCheck);
        
        // Return response
        return mapToAttendanceCheckResponse(savedAttendanceCheck);
    }
    
    /**
     * Determines attendance status based on request status or check-in time
     * If a specific status is requested (like EXCUSED), use that.
     * Otherwise, determine if the student is PRESENT or LATE based on check-in time
     */
    private AttendanceStatus determineAttendanceStatus(AttendanceStatus requestStatus, Lesson lesson, LocalDateTime checkInTime) {
        // If EXCUSED is specifically requested, honor that
        if (requestStatus == AttendanceStatus.EXCUSED) {
            return AttendanceStatus.EXCUSED;
        }
        
        // If it's an explicit ABSENT request, honor that
        if (requestStatus == AttendanceStatus.ABSENT) {
            return AttendanceStatus.ABSENT;
        }
        
        // Otherwise, determine status based on check-in time
        LocalDateTime lessonStartTime = lesson.getStartTime();
        
        // Calculate minutes late
        long minutesLate = Duration.between(lessonStartTime, checkInTime).toMinutes();
        
        // If more than 30 minutes late, mark as LATE
        if (minutesLate > LATE_THRESHOLD_MINUTES) {
            return AttendanceStatus.LATE;
        }
        
        // Otherwise, mark as PRESENT
        return AttendanceStatus.PRESENT;
    }

    @Override
    public List<AttendanceCheckResponse> getAttendancesByStudent(int studentId, int classId) {
        // Check if student exists
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_EXISTED));

        List<AttendanceCheckResponse> listAttendance = attendanceCheckRepository.findByStudentIdAndClassId(studentId, classId).stream()
                .map(this::mapToAttendanceCheckResponse)
                .collect(Collectors.toList());

//        for (AttendanceCheckResponse attendanceCheckResponse : listAttendance) {
//            if (lessonRepository.findLessonById(attendanceCheckResponse.getLessonId()).getClassEntity().getId() != classId) {
//                listAttendance.remove(attendanceCheckResponse);
//            }
//        }
        
        // Get attendances for student
        return listAttendance;
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