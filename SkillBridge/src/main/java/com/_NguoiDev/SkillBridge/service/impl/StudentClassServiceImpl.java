package com._NguoiDev.SkillBridge.service.impl;

import com._NguoiDev.SkillBridge.dto.request.ClassEnrollmentRequest;
import com._NguoiDev.SkillBridge.dto.response.ClassEnrollmentResponse;
import com._NguoiDev.SkillBridge.embedded.AttendanceCheckId;
import com._NguoiDev.SkillBridge.embedded.StudentClassId;
import com._NguoiDev.SkillBridge.entity.*;
import com._NguoiDev.SkillBridge.entity.Class;
import com._NguoiDev.SkillBridge.enums.AttendanceStatus;
import com._NguoiDev.SkillBridge.exception.AppException;
import com._NguoiDev.SkillBridge.exception.ErrorCode;
import com._NguoiDev.SkillBridge.repository.AttendanceCheckRepository;
import com._NguoiDev.SkillBridge.repository.ClassRepository;
import com._NguoiDev.SkillBridge.repository.StudentClassRepository;
import com._NguoiDev.SkillBridge.repository.StudentRepository;
import com._NguoiDev.SkillBridge.service.StudentClassService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentClassServiceImpl implements StudentClassService {

    private final StudentClassRepository studentClassRepository;
    private final StudentRepository studentRepository;
    private final ClassRepository classRepository;
    private final AttendanceCheckRepository attendanceCheckRepository;

    @Override
    @Transactional
    public ClassEnrollmentResponse enrollStudentInClass(ClassEnrollmentRequest request) {
        Student student = studentRepository.findByUserUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(()->new AppException(ErrorCode.STUDENT_NOT_EXISTED));
        
        // Check if class exists
        Class classEntity = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new EntityNotFoundException("Class not found with id: " + request.getClassId()));
        
        // Check if student is already enrolled in this class
        if (studentClassRepository.existsByStudentAndClassEntity(student, classEntity)) {
            throw new IllegalArgumentException("Student is already enrolled in this class");
        }
        
        // Create StudentClassId (composite key)
        StudentClassId studentClassId = StudentClassId.builder()
                .studentId(student.getId())
                .classId(classEntity.getId())
                .build();
        
        // Create and save enrollment
        StudentClass studentClass = StudentClass.builder()
                .id(studentClassId)
                .student(student)
                .classEntity(classEntity)
                .joinedAt(LocalDateTime.now())
                .active(true)
                .build();
        
        StudentClass savedEnrollment = studentClassRepository.save(studentClass);
        
        // Create attendance records for each lesson in the class
        createAttendanceRecordsForStudent(student, classEntity);
        
        // Return response
        return mapToEnrollmentResponse(savedEnrollment);
    }

    /**
     * Creates attendance records for a student for all lessons in a class
     */
    private void createAttendanceRecordsForStudent(Student student, Class classEntity) {
        List<AttendanceCheck> attendanceChecks = new ArrayList<>();
        
        if (classEntity.getLessons() != null && !classEntity.getLessons().isEmpty()) {
            for (Lesson lesson : classEntity.getLessons()) {
                // Skip if attendance record already exists
                if (attendanceCheckRepository.existsByStudentAndLesson(student, lesson)) {
                    continue;
                }
                
                // Create the attendance check ID
                AttendanceCheckId attendanceCheckId = AttendanceCheckId.builder()
                        .studentId(student.getId())
                        .lessonId(lesson.getId())
                        .build();
                
                // Create the attendance check with initial status as ABSENT
                AttendanceCheck attendanceCheck = AttendanceCheck.builder()
                        .id(attendanceCheckId)
                        .student(student)
                        .lesson(lesson)
                        .status(AttendanceStatus.ABSENT) // Default status
                        .build();
                
                attendanceChecks.add(attendanceCheck);
            }
            
            // Save all attendance checks if there are any to save
            if (!attendanceChecks.isEmpty()) {
                attendanceCheckRepository.saveAll(attendanceChecks);
            }
        }
    }

    @Override
    public List<ClassEnrollmentResponse> getClassesByStudent(int studentId) {
        // Check if student exists
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + studentId));
        
        // Get enrollments for student
        return studentClassRepository.findByStudent(student).stream()
                .map(this::mapToEnrollmentResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClassEnrollmentResponse> getStudentsByClass(int classId) {
        // Check if class exists
        Class classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new EntityNotFoundException("Class not found with id: " + classId));
        
        // Get students in class
        return studentClassRepository.findByClassEntity(classEntity).stream()
                .map(this::mapToEnrollmentResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeStudentFromClass(int studentId, int classId) {
        // Check if student exists
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + studentId));
        
        // Check if class exists
        Class classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new EntityNotFoundException("Class not found with id: " + classId));
        
        // Find enrollment
        StudentClass enrollment = studentClassRepository.findByStudentAndClassEntity(student, classEntity)
                .orElseThrow(() -> new EntityNotFoundException("Student is not enrolled in this class"));
        
        // Delete attendance records for this student and class
        if (classEntity.getLessons() != null) {
            for (Lesson lesson : classEntity.getLessons()) {
                AttendanceCheckId attendanceCheckId = new AttendanceCheckId(student.getId(), lesson.getId());
                attendanceCheckRepository.findById(attendanceCheckId)
                        .ifPresent(attendanceCheckRepository::delete);
            }
        }
        
        // Delete enrollment
        studentClassRepository.delete(enrollment);
    }
    
    private ClassEnrollmentResponse mapToEnrollmentResponse(StudentClass studentClass) {
        return ClassEnrollmentResponse.builder()
                .studentId(studentClass.getStudent().getId())
                .studentName(studentClass.getStudent().getName())
                .classId(studentClass.getClassEntity().getId())
                .className(studentClass.getClassEntity().getName())
                .joinedAt(studentClass.getJoinedAt())
                .active(studentClass.isActive())
                .build();
    }
} 