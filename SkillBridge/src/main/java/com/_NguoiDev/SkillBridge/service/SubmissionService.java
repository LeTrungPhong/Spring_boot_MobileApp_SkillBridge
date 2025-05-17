package com._NguoiDev.SkillBridge.service;

import com._NguoiDev.SkillBridge.dto.request.SubmissionRequest;
import com._NguoiDev.SkillBridge.dto.response.SubmissionResponse;
import com._NguoiDev.SkillBridge.entity.Assignment;
import com._NguoiDev.SkillBridge.entity.Class;
import com._NguoiDev.SkillBridge.entity.Submission;
import com._NguoiDev.SkillBridge.entity.SubmissionAttachment;
import com._NguoiDev.SkillBridge.entity.Teacher;
import com._NguoiDev.SkillBridge.entity.User;
import com._NguoiDev.SkillBridge.exception.AppException;
import com._NguoiDev.SkillBridge.exception.ErrorCode;
import com._NguoiDev.SkillBridge.mapper.SubmissionMapper;
import com._NguoiDev.SkillBridge.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubmissionService {
    SubmissionRepository submissionRepository;
    UserRepository userRepository;
    SubmissionMapper submissionMapper;
    AssignmentRepository assignmentRepository;
    TeacherRepository teacherRepository;
    ClassRepository classRepository;

    @NonFinal
    @Value("${file.upload-dir}")
    protected String FILE_DIR;
    public SubmissionResponse submit(SubmissionRequest request, int classId, String assinmentId) throws IOException {
        User currentUser = userRepository.findById(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED));
        Assignment assignment = assignmentRepository.getAssignmentsById(assinmentId)
                .orElseThrow(()->new AppException(ErrorCode.ASSIGNMENT_NOT_FOUND));
        if (assignment.getAClass().getId() != classId) {
            throw new AppException(ErrorCode.ASSIGNMENT_NOT_FOUND);
        }
        System.out.println(currentUser.getUsername()+"submission");
        Submission submission = submissionMapper.toSubmission(request);
        submission.setAssignment(assignment);
        submission.setUser(currentUser);
        submission.setSubmissionTime(LocalDateTime.now());
        submission.setId(UUID.randomUUID().toString());
        List<SubmissionAttachment> submissionAttachments = new ArrayList<>();
        for (MultipartFile file: request.getFiles()) {
            String fileName = file.getOriginalFilename();
            Path filePath = Paths.get(FILE_DIR+"/" + classId+"/"+assinmentId+"/"+currentUser.getUsername()+"/"+fileName);
            Files.createDirectories(filePath.getParent());
            file.transferTo(filePath.toFile());
            SubmissionAttachment submissionAttachment = SubmissionAttachment.builder()
                    .fileName(fileName)
                    .fileType(file.getContentType())
                    .filePath(filePath.toString())
                    .submission(submission)
                    .build();
            submissionAttachments.add(submissionAttachment);
        }
        submission.setSubmissionAttachments(submissionAttachments);
        return getSubmissionById(submissionRepository.save(submission).getId());
    }

    public SubmissionResponse getSubmissionById(String submissionId) {
        Submission submission = submissionRepository.getSubmissionById(submissionId).orElse(null);
        if (submission == null) { return null;}
        SubmissionResponse submissionResponse = submissionMapper.toSubmissionResponse(submission);
        List<String> filesNames = submission.getSubmissionAttachments().stream().map(SubmissionAttachment::getFileName).toList();
        submissionResponse.setFilesNames(filesNames);
        Assignment assignment = submission.getAssignment();
        if (submission.getSubmissionTime().isAfter(assignment.getDeadLine())){
            submissionResponse.setStatus(2);
        }else{
            submissionResponse.setStatus(1);
        }
        return submissionResponse;
    }

    public Resource downloadSubmission(int classId, String assignmentId, String fileName, String username) throws MalformedURLException {
        if (username==null){
            username = SecurityContextHolder.getContext().getAuthentication().getName();
        }else {
            System.out.println(SecurityContextHolder.getContext().getAuthentication().getName());
            teacherRepository.findByUserUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                    .orElseThrow(()->new AppException(ErrorCode.ACCESS_DENIED));
            if (!Objects.equals(classRepository.findById(classId).orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND))
                    .getTeacher().getUser().getUsername(), SecurityContextHolder.getContext().getAuthentication().getName())){
                throw new AppException(ErrorCode.ACCESS_DENIED);
            }
        }

        Path filePath = Paths.get(FILE_DIR+"/"+classId+"/"+assignmentId+"/"+username+"/"+fileName).normalize();
        if (!Files.exists(filePath)){
            throw new AppException(ErrorCode.FILE_NOT_FOUND);
        }
        return new UrlResource(filePath.toUri());
    }

    public void DeleteSubmission(String submissionId, String assignmentId) {
        Submission submission = submissionRepository.getSubmissionById(submissionId).orElseThrow(()->new AppException(ErrorCode.SUBMISSION_NOT_FOUND));
        if (!submission.getAssignment().getId().equals(assignmentId)){
            throw new AppException(ErrorCode.ASSIGNMENT_NOT_FOUND);
        }
        if (!submission.getUser().getUsername().equals(SecurityContextHolder.getContext().getAuthentication().getName())){
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }
        submissionRepository.delete(submission);
    }

    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    public SubmissionResponse gradeSubmission(String submissionId, int point, String feedback) {
        User currentUser = userRepository.findById(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        
        // Verify user is a teacher
        Teacher teacher = teacherRepository.findByUserUsername(currentUser.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.ACCESS_DENIED));
        
        Submission submission = submissionRepository.getSubmissionById(submissionId)
                .orElseThrow(() -> new AppException(ErrorCode.SUBMISSION_NOT_FOUND));
        
        // Verify that the teacher is teaching the class associated with the assignment
        Assignment assignment = submission.getAssignment();
        Class aClass = assignment.getAClass();
        
        if (aClass.getTeacher().getId() != teacher.getId()) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }
        
        // Grade must be between 0 and 100
        if (point < 0 || point > 100) {
            throw new AppException(ErrorCode.INVALID_GRADE);
        }
        
        submission.setPoint(point);
        submission.setFeedback(feedback);
        submissionRepository.save(submission);
        
        return getSubmissionById(submissionId);
    }
    
    public List<SubmissionResponse> getSubmissionsByAssignmentId(String assignmentId) {
        User currentUser = userRepository.findById(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
                
        Assignment assignment = assignmentRepository.getAssignmentsById(assignmentId)
                .orElseThrow(() -> new AppException(ErrorCode.ASSIGNMENT_NOT_FOUND));
        
        // If the user is a teacher, verify they teach this class
        boolean isTeacher = teacherRepository.findByUserUsername(currentUser.getUsername()).isPresent();
        if (isTeacher) {
            Teacher teacher = teacherRepository.findByUserUsername(currentUser.getUsername()).get();
            if (assignment.getAClass().getTeacher().getId() != teacher.getId()) {
                throw new AppException(ErrorCode.ACCESS_DENIED);
            }
            
            // Return all submissions for the assignment
            List<Submission> submissions = submissionRepository.findAllByAssignmentId(assignmentId);
            return submissions.stream()
                .map(submission -> getSubmissionById(submission.getId()))
                .collect(Collectors.toList());
        } else {
            // Students can only see their own submissions
            Optional<Submission> submission = submissionRepository.getSubmissionByAssignmentIdAndUserUsername(
                assignmentId, currentUser.getUsername());
            
            if (submission.isPresent()) {
                return List.of(getSubmissionById(submission.get().getId()));
            }
            
            return List.of();
        }
    }
}
