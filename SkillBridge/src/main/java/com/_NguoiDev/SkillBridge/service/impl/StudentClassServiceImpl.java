package com._NguoiDev.SkillBridge.service.impl;

import com._NguoiDev.SkillBridge.dto.request.ClassEnrollmentRequest;
import com._NguoiDev.SkillBridge.dto.response.ClassEnrollmentResponse;
import com._NguoiDev.SkillBridge.embedded.StudentClassId;
import com._NguoiDev.SkillBridge.entity.Class;
import com._NguoiDev.SkillBridge.entity.Student;
import com._NguoiDev.SkillBridge.entity.StudentClass;
import com._NguoiDev.SkillBridge.repository.ClassRepository;
import com._NguoiDev.SkillBridge.repository.StudentClassRepository;
import com._NguoiDev.SkillBridge.repository.StudentRepository;
import com._NguoiDev.SkillBridge.service.StudentClassService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentClassServiceImpl implements StudentClassService {

    private final StudentClassRepository studentClassRepository;
    private final StudentRepository studentRepository;
    private final ClassRepository classRepository;

    @Override
    @Transactional
    public ClassEnrollmentResponse enrollStudentInClass(ClassEnrollmentRequest request) {
        // Check if student exists
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + request.getStudentId()));
        
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
        
        // Return response
        return mapToEnrollmentResponse(savedEnrollment);
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