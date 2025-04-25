package com._NguoiDev.SkillBridge.service;

import com._NguoiDev.SkillBridge.dto.request.AssignmentRequest;
import com._NguoiDev.SkillBridge.dto.response.AssignmentResponse;
import com._NguoiDev.SkillBridge.entity.Assignment;
import com._NguoiDev.SkillBridge.entity.Attachment;
import com._NguoiDev.SkillBridge.entity.Class;
import com._NguoiDev.SkillBridge.entity.Teacher;
import com._NguoiDev.SkillBridge.exception.AppException;
import com._NguoiDev.SkillBridge.exception.ErrorCode;
import com._NguoiDev.SkillBridge.mapper.AssignmentMapper;
import com._NguoiDev.SkillBridge.repository.AssignmentRepository;
import com._NguoiDev.SkillBridge.repository.AttachRepository;
import com._NguoiDev.SkillBridge.repository.ClassRepository;
import com._NguoiDev.SkillBridge.repository.TeacherRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AssignmentService {
    @NonFinal
    @Value("${file.upload-dir}")
    protected String FILE_DIR;
    AssignmentRepository assignmentRepository;
    AttachRepository attachRepository;
    AssignmentMapper assignmentMapper;
    ClassRepository classRepository;
    TeacherRepository teacherRepository;
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    @Transactional
    public void createAssignment(AssignmentRequest assignmentRequest, int classId) throws IOException {
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
    }

    public List<AssignmentResponse> getAllAssignments(int idClass)  {
        List<Assignment> assignments = assignmentRepository.findAllByaClassId(idClass);
        List<AssignmentResponse> assignmentResponses = new ArrayList<>();
        for (Assignment assignment : assignments) {
            assignmentResponses.add(assignmentMapper.toAssignmentResponse(assignment));
        }
        return assignmentResponses;
    }

    public AssignmentResponse getAssignmentById(String id)  {
        Assignment assignment = assignmentRepository.getAssignmentsById(id)
                .orElseThrow(()->new AppException(ErrorCode.ASSIGNMENT_NOT_FOUND));
        AssignmentResponse assignmentResponse = assignmentMapper.toAssignmentResponse(assignment);
        List<Attachment> attachments = attachRepository.findAllByAssignmentId(assignmentResponse.getId());
        List<String> urls = new ArrayList<>();
        for (Attachment attachment : attachments) {
            urls.add(attachment.getFileName());
        }
        assignmentResponse.setFilesName(urls);
        return assignmentResponse;
    }

    public Resource downloadAssignment(int classId, String assignmentId, String fileName) throws MalformedURLException {
        Path filePath = Paths.get(FILE_DIR+"/"+classId+"/"+assignmentId+"/"+fileName).normalize();
        if (!Files.exists(filePath)){
            throw new AppException(ErrorCode.FILE_NOT_FOUND);
        }
        return new UrlResource(filePath.toUri());
    }
}
