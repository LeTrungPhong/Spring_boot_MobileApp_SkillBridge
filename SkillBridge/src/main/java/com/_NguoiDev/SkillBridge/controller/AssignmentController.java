package com._NguoiDev.SkillBridge.controller;

import com._NguoiDev.SkillBridge.dto.request.AssignmentRequest;
import com._NguoiDev.SkillBridge.dto.response.ApiResponse;
import com._NguoiDev.SkillBridge.dto.response.AssignmentResponse;
import com._NguoiDev.SkillBridge.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    public ApiResponse<List<AssignmentResponse>> getAllAssignment(@PathVariable int classId) throws IOException {
        return ApiResponse.<List<AssignmentResponse>>builder()
                .code(1000)
                .message("success")
                .result(assignmentService.getAllAssignments(classId))
                .build();
    }

    @GetMapping("/{classId}/{assignmentId}")
    public ApiResponse<AssignmentResponse> getAssignment( @PathVariable String assignmentId){
        return ApiResponse.<AssignmentResponse>builder()
                .code(1000)
                .message("success")
                .result(assignmentService.getAssignmentById(assignmentId))
                .build();
    }

    @GetMapping("/{classId}/{assignmentId}/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable int classId , @PathVariable String assignmentId, @PathVariable String fileName) throws MalformedURLException {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment, fileName: \""+ fileName+"\"")
                .body(assignmentService.downloadAssignment(classId, assignmentId, fileName));
    }
}
