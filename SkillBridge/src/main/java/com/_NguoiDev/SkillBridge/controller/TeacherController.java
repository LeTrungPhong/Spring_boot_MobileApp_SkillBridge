package com._NguoiDev.SkillBridge.controller;

import com._NguoiDev.SkillBridge.dto.request.TeacherCreateRequest;
import com._NguoiDev.SkillBridge.dto.response.TeacherResponse;
import com._NguoiDev.SkillBridge.service.impl.TeacherServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherServiceImpl teacherService;

    @PostMapping
    public ResponseEntity<TeacherResponse> createTeacher(@RequestBody TeacherCreateRequest request) {
        return new ResponseEntity<>(teacherService.createTeacher(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeacherResponse> getTeacherById(@PathVariable int id) {
        return ResponseEntity.ok(teacherService.getTeacherById(id));
    }

    @GetMapping
    public ResponseEntity<List<TeacherResponse>> getAllTeachers() {
        return ResponseEntity.ok(teacherService.getAllTeachers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeacherResponse> updateTeacher(@PathVariable int id, @RequestBody TeacherCreateRequest request) {
        return ResponseEntity.ok(teacherService.updateTeacher(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable int id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.noContent().build();
    }
} 