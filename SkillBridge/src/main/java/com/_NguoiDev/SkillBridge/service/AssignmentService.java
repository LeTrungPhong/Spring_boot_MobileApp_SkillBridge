package com._NguoiDev.SkillBridge.service;

import com._NguoiDev.SkillBridge.dto.request.AssignmentRequest;
import com._NguoiDev.SkillBridge.dto.response.AssignmentAllSubmitResponse;
import com._NguoiDev.SkillBridge.dto.response.AssignmentResponse;
import com._NguoiDev.SkillBridge.entity.*;
import com._NguoiDev.SkillBridge.entity.Class;
import com._NguoiDev.SkillBridge.exception.AppException;
import com._NguoiDev.SkillBridge.exception.ErrorCode;
import com._NguoiDev.SkillBridge.mapper.AssignmentMapper;
import com._NguoiDev.SkillBridge.repository.*;
import com.google.firebase.messaging.FirebaseMessagingException;
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
import org.springframework.transaction.annotation.Transactional;
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
public class AssignmentService {
    private final UserRepository userRepository;
    @NonFinal
    @Value("${file.upload-dir}")
    protected String FILE_DIR;
    AssignmentRepository assignmentRepository;
    AttachRepository attachRepository;
    AssignmentMapper assignmentMapper;
    ClassRepository classRepository;
    TeacherRepository teacherRepository;
    SubmissionRepository submissionRepository;
    SubmissionService submissionService;
    StudentClassRepository studentClassRepository;

    NotificationService notificationService;
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    @Transactional
    public void createAssignment(AssignmentRequest assignmentRequest, int classId) throws IOException, FirebaseMessagingException {
        Teacher teacher = teacherRepository.findByUserUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(()->new AppException(ErrorCode.TEACHER_NOT_EXISTED));
        Class Aclass = classRepository.findById(classId).orElseThrow(()-> new AppException(ErrorCode.CLASS_NOT_FOUND));
        if (teacher.getId()!=Aclass.getTeacher().getId()) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }
        String id = UUID.randomUUID().toString();
        Assignment assignment = assignmentMapper.toAssignment(assignmentRequest);
        assignment.setCreateAt(LocalDateTime.now());
        assignment.setCreateBy(SecurityContextHolder.getContext().getAuthentication().getName());
        assignment.setId(id);
        assignment.setAClass(Aclass);
        List<Attachment> attachments = new ArrayList<>();
        for (MultipartFile file:assignmentRequest.getFiles()){
            String fileName = file.getOriginalFilename();
            Path filePath = Paths.get(FILE_DIR+"/"+classId+"/"+id,fileName);
            Files.createDirectories(filePath.getParent());
            file.transferTo(filePath.toFile());
            Attachment attachment = Attachment.builder()
                    .fileName(fileName)
                    .fileType(file.getContentType())
                    .filePath(filePath.toString())
                    .assignment(assignment)
                    .build();
            attachments.add(attachment);
        }
        assignment.setAttachments(attachments);
        assignmentRepository.save(assignment);
        List<User> students = studentClassRepository.findAllByClassEntity_Id(classId).stream().map(value -> value.getStudent().getUser()).toList();
        for (User user : students) {
            Set<Assignment> assignments = user.getAssignments();
            assignments.add(assignment);
            user.setAssignments(assignments);
            userRepository.save(user);
        }

        MyNotification notification = MyNotification.builder()
                .title("Bạn có 1 bài tập mới trong lớp " + Aclass.getName())
                .body(teacher.getName() + " đã đăng 1 bài tập mới")
                .createdAt(LocalDateTime.now())
                .assignmentId(assignment.getId())
                .aClass(Aclass)
                .build();


        notificationService.notify(notification, Aclass.getId());

    }

    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    public List<AssignmentResponse> getAllAssignments(int idClass)  {
        Class aclass = classRepository.findById(idClass).orElseThrow(()->new AppException(ErrorCode.CLASS_NOT_FOUND));
        if (!aclass.getTeacher().getUser().getUsername().equals(SecurityContextHolder.getContext().getAuthentication().getName())){
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }
        List<Assignment> assignments = assignmentRepository.findAllByaClassId(idClass);
        List<AssignmentResponse> assignmentResponses = new ArrayList<>();
        for (Assignment assignment : assignments) {
            AssignmentResponse a = assignmentMapper.toAssignmentResponse(assignment);
            a.setFilesName(assignment.getAttachments().stream().map(Attachment::getFileName).toList());
            assignmentResponses.add(a);
        }
        return assignmentResponses;
    }

    public List<AssignmentResponse> getAllStudentAssignmentsByClass(int idClass)  {
        List<Assignment> assignments = assignmentRepository.findAllStudentAssignmentByClass(idClass, SecurityContextHolder.getContext().getAuthentication().getName());
        List<AssignmentResponse> assignmentResponses = new ArrayList<>();
        for (Assignment assignment : assignments) {
            AssignmentResponse a = assignmentMapper.toAssignmentResponse(assignment);
            a.setFilesName(assignment.getAttachments().stream().map(Attachment::getFileName).toList());
            assignmentResponses.add(a);
        }
        return assignmentResponses;
    }

    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    public List<AssignmentResponse> getAllMyCreateAssignments(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Integer> listClass = classRepository.findAllByTeacherUserUsername(username).stream().map(Class::getId).toList();
        List<AssignmentResponse> result = new ArrayList<>();
        for (Integer id : listClass) {
            result.addAll(getAllAssignments(id));
        }
        return result;
    }

    public List<AssignmentResponse> getAllMyAssignments(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Set<Assignment> assignments = userRepository.findByUsername(username).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED))
                .getAssignments();
        List<AssignmentResponse> assignmentResponses = new ArrayList<>();
        for (Assignment assignment : assignments) {
            AssignmentResponse a = assignmentMapper.toAssignmentResponse(assignment);
            a.setFilesName(assignment.getAttachments().stream().map(Attachment::getFileName).toList());
            assignmentResponses.add(a);
        }
        return assignmentResponses;
    }

    public AssignmentResponse getOneAssignmentById(int classId, String id, String username)  {
        if (username==null){
            username = SecurityContextHolder.getContext().getAuthentication().getName();
        }else{
            teacherRepository.findByUserUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                    .orElseThrow(()->new AppException(ErrorCode.ACCESS_DENIED));
        }
        System.out.println(username);
        Assignment assignment = assignmentRepository.getAssignmentsById(id)
                .orElseThrow(()->new AppException(ErrorCode.ASSIGNMENT_NOT_FOUND));
        if (assignment.getAClass().getId()!=classId){
            throw new AppException(ErrorCode.ASSIGNMENT_NOT_FOUND);
        }
        AssignmentResponse assignmentResponse = assignmentMapper.toAssignmentResponse(assignment);
        List<Attachment> attachments = attachRepository.findAllByAssignmentId(assignmentResponse.getId());
        List<String> urls = new ArrayList<>();
        for (Attachment attachment : attachments) {
            urls.add(attachment.getFileName());
        }
        assignmentResponse.setFilesName(urls);

        Submission submission = submissionRepository.getSubmissionByAssignmentIdAndUserUsername(id, username).orElse(null);
        if (submission == null) { assignmentResponse.setSubmission(null); }
        else
            assignmentResponse.setSubmission(submissionService.getSubmissionById(submission.getId()));
        return assignmentResponse;
    }

    //from notification
    public AssignmentResponse getAssignmentByAssignmentId(String id)  {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Assignment assignment = assignmentRepository.getAssignmentsById(id)
                .orElseThrow(()->new AppException(ErrorCode.ASSIGNMENT_NOT_FOUND));
        AssignmentResponse assignmentResponse = assignmentMapper.toAssignmentResponse(assignment);
        List<Attachment> attachments = attachRepository.findAllByAssignmentId(assignmentResponse.getId());
        List<String> urls = new ArrayList<>();
        for (Attachment attachment : attachments) {
            urls.add(attachment.getFileName());
        }
        assignmentResponse.setFilesName(urls);

        Submission submission = submissionRepository.getSubmissionByAssignmentIdAndUserUsername(id, username).orElse(null);
        if (submission == null) { assignmentResponse.setSubmission(null); }
        else
            assignmentResponse.setSubmission(submissionService.getSubmissionById(submission.getId()));
        return assignmentResponse;
    }

    public AssignmentAllSubmitResponse getAssignmentListSubmitById(String id, int classId)  {
        teacherRepository.findByUserUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(()->new AppException(ErrorCode.ACCESS_DENIED));
        Assignment assignment = assignmentRepository.getAssignmentsById(id)
                .orElseThrow(()->new AppException(ErrorCode.ASSIGNMENT_NOT_FOUND));
        if (assignment.getAClass().getId()!=classId){
            throw new AppException(ErrorCode.ASSIGNMENT_NOT_FOUND);
        }
        if (!assignment.getAClass().getTeacher().getUser().getUsername().equals(SecurityContextHolder.getContext().getAuthentication().getName())){
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }
        AssignmentAllSubmitResponse assignmentResponse = assignmentMapper.toAssignmentAllSubmitResponse(assignment);
        List<Attachment> attachments = attachRepository.findAllByAssignmentId(assignmentResponse.getId());
        List<String> urls = new ArrayList<>();
        for (Attachment attachment : attachments) {
            urls.add(attachment.getFileName());
        }
        assignmentResponse.setFilesName(urls);

        List<Submission> submissions = submissionRepository.findAllByAssignmentId(id);
        if (submissions.isEmpty()) { assignmentResponse.setSubmissionResponses(null); }
        else
            assignmentResponse.setSubmissionResponses(submissions.stream().map(value -> submissionService.getSubmissionById(value.getId())).toList());
        return assignmentResponse;
    }

    public Resource downloadAssignment(int classId, String assignmentId, String fileName) throws MalformedURLException {
        Path filePath = Paths.get(FILE_DIR+"/"+classId+"/"+assignmentId+"/"+fileName).normalize();
        if (!Files.exists(filePath)){
            throw new AppException(ErrorCode.FILE_NOT_FOUND);
        }
        return new UrlResource(filePath.toUri());
    }

    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public void DeleteAssignment(String assignmentId, int classId)  {
        if (!classRepository.findById(classId).orElseThrow(()->new AppException(ErrorCode.CLASS_NOT_FOUND)).getTeacher().getUser().getUsername().equals(SecurityContextHolder.getContext().getAuthentication().getName())){
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }
        List<User> users = userRepository.findAllByAssignments_Id(assignmentId);
        Assignment assignment = assignmentRepository.getAssignmentsById(assignmentId).orElseThrow(()->new AppException(ErrorCode.ASSIGNMENT_NOT_FOUND));
        for (User user : users) {
            Set<Assignment> assignments= user.getAssignments();
            assignments.remove(assignment);
            user.setAssignments(assignments);
            userRepository.save(user);
        }
        assignmentRepository.delete(assignment);
    }
}
