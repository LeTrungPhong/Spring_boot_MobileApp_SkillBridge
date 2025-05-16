package com._NguoiDev.SkillBridge.controller;

import com._NguoiDev.SkillBridge.dto.request.AssignmentRequest;
import com._NguoiDev.SkillBridge.dto.request.GradeSubmissionRequest;
import com._NguoiDev.SkillBridge.dto.request.SubmissionRequest;
import com._NguoiDev.SkillBridge.dto.response.ApiResponse;
import com._NguoiDev.SkillBridge.dto.response.AssignmentAllSubmitResponse;
import com._NguoiDev.SkillBridge.dto.response.AssignmentResponse;
import com._NguoiDev.SkillBridge.dto.response.SubmissionResponse;
import com._NguoiDev.SkillBridge.service.AssignmentService;
import com._NguoiDev.SkillBridge.service.SubmissionService;
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
import java.util.Objects;

@RestController
@RequestMapping("/api/assignment")
@RequiredArgsConstructor
public class AssignmentController {

    private final SubmissionService submissionService;
    private final AssignmentService assignmentService;
    @PostMapping("/{classId}")
    public ApiResponse<Void> createAssignment(@ModelAttribute AssignmentRequest request,@PathVariable int classId) throws IOException {
        assignmentService.createAssignment(request, classId);
        return ApiResponse.<Void>builder().code(1000).message("success").build();
    }

    //get All Assignment của giáo viên lớp đó
    @GetMapping("/teacher/{classId}")
    public ApiResponse<List<AssignmentResponse>> getAllAssignment(@PathVariable int classId) throws IOException {
        return ApiResponse.<List<AssignmentResponse>>builder()
                .code(1000)
                .message("success")
                .result(assignmentService.getAllAssignments(classId))
                .build();
    }

    @GetMapping("/{classId}")
    public ApiResponse<List<AssignmentResponse>> getAssignmentsByClassAndStudent(@PathVariable int classId){
        return ApiResponse.<List<AssignmentResponse>>builder()
                .result(assignmentService.getAllStudentAssignmentsByClass(classId))
                .code(1000)
                .message("success")
                .build();
    }

    @GetMapping("/{classId}/{assignmentId}/teacher/{username}")
    public ApiResponse<AssignmentResponse> getOneAssignment(@PathVariable String assignmentId, @PathVariable String username, @PathVariable int classId){
        return ApiResponse.<AssignmentResponse>builder()
                .code(1000)
                .message("success")
                .result(assignmentService.getOneAssignmentById(classId, assignmentId, username))
                .build();
    }

    @GetMapping("/{classId}/{assignmentId}")
    public ApiResponse<AssignmentResponse> getOneAssignment(@PathVariable String assignmentId, @PathVariable int classId){
        return ApiResponse.<AssignmentResponse>builder()
                .code(1000)
                .message("success")
                .result(assignmentService.getOneAssignmentById(classId, assignmentId, null))
                .build();
    }

    @GetMapping("/{classId}/{assignmentId}/all")
    public ApiResponse<AssignmentAllSubmitResponse> getOneAssignmentAllSubmit(@PathVariable String assignmentId, @PathVariable int classId){
        return ApiResponse.<AssignmentAllSubmitResponse>builder()
                .code(1000)
                .message("success")
                .result(assignmentService.getAssignmentListSubmitById(assignmentId,  classId))
                .build();
    }


    @PostMapping("{classId}/{assignmentId}")
    public ApiResponse<SubmissionResponse> Submission(@ModelAttribute SubmissionRequest request, @PathVariable int classId, @PathVariable String assignmentId) throws IOException {
        return ApiResponse.<SubmissionResponse>builder()
                .code(1000)
                .message("success")
                .result(submissionService.submit(request, classId, assignmentId))
                .build();
    }

    @GetMapping("/{classId}/{assignmentId}/{fileName}")
    public ResponseEntity<Resource> downloadAssignmentFile(@PathVariable int classId , @PathVariable String assignmentId, @PathVariable String fileName) throws MalformedURLException {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment, fileName: \""+ fileName+"\"")
                .body(assignmentService.downloadAssignment(classId, assignmentId, fileName));
    }

    @GetMapping("/{classId}/{assignmentId}/result/{fileName}")
    public ResponseEntity<Resource> downloadSubmitFile(@PathVariable int classId , @PathVariable String assignmentId, @PathVariable String fileName, @RequestBody(required = false) String username) throws MalformedURLException {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment, fileName: \""+ fileName+"\"")
                .body(submissionService.downloadSubmission(classId, assignmentId, fileName, username));
    }

    @DeleteMapping("/{classId}/{assignmentId}")
    public ApiResponse<Void> deleteAssignment(@PathVariable int classId, @PathVariable String assignmentId){
        assignmentService.DeleteAssignment(assignmentId, classId);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("success")
                .build();
    }

    @DeleteMapping("/{classId}/{assignmentId}/{submissionId}")
    public ApiResponse<Void> revertSubmit(@PathVariable String assignmentId, @PathVariable String classId, @PathVariable String submissionId){
        submissionService.DeleteSubmission(submissionId, assignmentId);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("success")
                .build();
    }

    @GetMapping()
    public ApiResponse<List<AssignmentResponse>> getAllMyAssignments(){
        return ApiResponse.<List<AssignmentResponse>>builder()
                .code(1000)
                .message("success")
                .result(assignmentService.getAllMyAssignments())
                .build();
    }

    @GetMapping("/myCreate")
    public ApiResponse<List<AssignmentResponse>> getAllMyAssignmentsCreate(){
        return ApiResponse.<List<AssignmentResponse>>builder()
                .code(1000)
                .message("success")
                .result(assignmentService.getAllMyCreateAssignments())
                .build();
    }

    @GetMapping("/{classId}/{assignmentId}/submissions")
    public ApiResponse<List<SubmissionResponse>> getSubmissionsByAssignment(@PathVariable int classId, @PathVariable String assignmentId) {
        return ApiResponse.<List<SubmissionResponse>>builder()
                .code(1000)
                .message("success")
                .result(submissionService.getSubmissionsByAssignmentId(assignmentId))
                .build();
    }
    
    @GetMapping("/{classId}/{assignmentId}/submission/{submissionId}")
    public ApiResponse<SubmissionResponse> getSubmissionById(@PathVariable int classId, 
                                                            @PathVariable String assignmentId,
                                                            @PathVariable String submissionId) {
        return ApiResponse.<SubmissionResponse>builder()
                .code(1000)
                .message("success")
                .result(submissionService.getSubmissionById(submissionId))
                .build();
    }
    
    @PutMapping("/{classId}/{assignmentId}/submission/{submissionId}/grade")
    public ApiResponse<SubmissionResponse> gradeSubmission(@PathVariable int classId, 
                                                          @PathVariable String assignmentId,
                                                          @PathVariable String submissionId,
                                                          @RequestBody GradeSubmissionRequest request) {
        return ApiResponse.<SubmissionResponse>builder()
                .code(1000)
                .message("success")
                .result(submissionService.gradeSubmission(submissionId, request.getPoint(), request.getFeedback()))
                .build();
    }

}
