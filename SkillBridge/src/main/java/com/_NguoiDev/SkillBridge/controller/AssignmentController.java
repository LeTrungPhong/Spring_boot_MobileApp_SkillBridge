package com._NguoiDev.SkillBridge.controller;

import com._NguoiDev.SkillBridge.dto.request.AssignmentRequest;
import com._NguoiDev.SkillBridge.dto.response.ApiResponse;
import com._NguoiDev.SkillBridge.dto.response.AssignmentResponse;
import com._NguoiDev.SkillBridge.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/assignment")
@RequiredArgsConstructor
public class AssignmentController {
    private final AssignmentService assignmentService;
    @PostMapping("/{classId}")
    public ApiResponse<Void> createAssignment(@ModelAttribute AssignmentRequest request,@PathVariable int classId) throws IOException {
        assignmentService.createAssignment(request, classId);
        return ApiResponse.<Void>builder().code(1000).message("success").build();
    }

    @GetMapping("/{classId}")
    public ApiResponse<List<AssignmentResponse>> getAssignment(@PathVariable int classId) throws IOException {
        return ApiResponse.<List<AssignmentResponse>>builder()
                .code(1000)
                .message("success")
                .result(assignmentService.getAllAssignments(classId))
                .build();
    }
}
