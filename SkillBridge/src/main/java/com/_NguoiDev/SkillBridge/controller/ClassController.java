package com._NguoiDev.SkillBridge.controller;

import com._NguoiDev.SkillBridge.dto.request.AssignmentRequest;
import com._NguoiDev.SkillBridge.dto.request.ClassCreateRequest;
import com._NguoiDev.SkillBridge.dto.response.ApiResponse;
import com._NguoiDev.SkillBridge.dto.response.ClassResponse;
import com._NguoiDev.SkillBridge.service.AssignmentService;
import com._NguoiDev.SkillBridge.service.impl.ClassServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class ClassController {

    private final ClassServiceImpl classService;
    private final AssignmentService assignmentService;

    @PostMapping
    public ApiResponse<ClassResponse> createClass(@RequestBody ClassCreateRequest request) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Username: " + auth.getName());
        System.out.println("Authorities: " + auth.getAuthorities());
        return ApiResponse.<ClassResponse>builder()
                .code(1000)
                .message("success")
                .result(classService.createClass(request))
                .build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassResponse> getClassById(@PathVariable int id) {
        return ResponseEntity.ok(classService.getClassById(id));
    }

    @GetMapping
    public ResponseEntity<List<ClassResponse>> getAllClasses() {
        return ResponseEntity.ok(classService.getAllClasses());
    }

    @GetMapping("/teacher")
    public ApiResponse<List<ClassResponse>> getClassesByTeacher() {
        return ApiResponse.<List<ClassResponse>>builder()
                .code(1000)
                .message("success")
                .result(classService.getClassesByTeacher())
                .build();
    }

} 