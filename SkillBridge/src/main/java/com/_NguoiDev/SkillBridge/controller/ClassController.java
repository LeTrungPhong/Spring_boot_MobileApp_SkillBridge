package com._NguoiDev.SkillBridge.controller;

import com._NguoiDev.SkillBridge.dto.request.ClassCreateRequest;
import com._NguoiDev.SkillBridge.dto.response.ClassResponse;
import com._NguoiDev.SkillBridge.service.impl.ClassServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class ClassController {

    private final ClassServiceImpl classService;

    @PostMapping
    public ResponseEntity<ClassResponse> createClass(@RequestBody ClassCreateRequest request) {
        return new ResponseEntity<>(classService.createClass(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassResponse> getClassById(@PathVariable int id) {
        return ResponseEntity.ok(classService.getClassById(id));
    }

    @GetMapping
    public ResponseEntity<List<ClassResponse>> getAllClasses() {
        return ResponseEntity.ok(classService.getAllClasses());
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<ClassResponse>> getClassesByTeacher(@PathVariable int teacherId) {
        return ResponseEntity.ok(classService.getClassesByTeacher(teacherId));
    }
} 