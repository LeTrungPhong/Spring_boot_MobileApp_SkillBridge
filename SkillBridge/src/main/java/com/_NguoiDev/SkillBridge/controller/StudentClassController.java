package com._NguoiDev.SkillBridge.controller;

import com._NguoiDev.SkillBridge.dto.request.ClassEnrollmentRequest;
import com._NguoiDev.SkillBridge.dto.response.ClassEnrollmentResponse;
import com._NguoiDev.SkillBridge.service.impl.StudentClassServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class StudentClassController {

    private final StudentClassServiceImpl studentClassService;

    @PostMapping
    public ResponseEntity<ClassEnrollmentResponse> enrollStudentInClass(@RequestBody ClassEnrollmentRequest request) {
        return new ResponseEntity<>(studentClassService.enrollStudentInClass(request), HttpStatus.CREATED);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<ClassEnrollmentResponse>> getClassesByStudent(@PathVariable int studentId) {
        return ResponseEntity.ok(studentClassService.getClassesByStudent(studentId));
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<List<ClassEnrollmentResponse>> getStudentsByClass(@PathVariable int classId) {
        return ResponseEntity.ok(studentClassService.getStudentsByClass(classId));
    }

    @DeleteMapping("/student/{studentId}/class/{classId}")
    public ResponseEntity<Void> removeStudentFromClass(@PathVariable int studentId, @PathVariable int classId) {
        studentClassService.removeStudentFromClass(studentId, classId);
        return ResponseEntity.noContent().build();
    }
} 