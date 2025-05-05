package com._NguoiDev.SkillBridge.service.impl;

import com._NguoiDev.SkillBridge.dto.request.PostRequest;
import com._NguoiDev.SkillBridge.dto.response.PostResponse;
import com._NguoiDev.SkillBridge.entity.Class;
import com._NguoiDev.SkillBridge.entity.Post;
import com._NguoiDev.SkillBridge.entity.Teacher;
import com._NguoiDev.SkillBridge.entity.User;
import com._NguoiDev.SkillBridge.exception.AppException;
import com._NguoiDev.SkillBridge.exception.ErrorCode;
import com._NguoiDev.SkillBridge.repository.ClassRepository;
import com._NguoiDev.SkillBridge.repository.PostRepository;
import com._NguoiDev.SkillBridge.repository.TeacherRepository;
import com._NguoiDev.SkillBridge.repository.UserRepository;
import com._NguoiDev.SkillBridge.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final ClassRepository classRepository;
    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;

    @Override
    public PostResponse createPost(PostRequest request, String username) {
        System.out.println("username: " + username);
        Teacher teacher = teacherRepository.findByUserUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.TEACHER_NOT_EXISTED));
        
        Class classEntity = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));
        
        // Verify that this teacher teaches this class
        if (classEntity.getTeacher().getId() != teacher.getId()) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }
        
        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .classEntity(classEntity)
                .teacher(teacher)
                .createdAt(LocalDateTime.now())
                .build();
        
        Post savedPost = postRepository.save(post);
        
        return mapToResponse(savedPost);
    }

    @Override
    public List<PostResponse> getPostsByClassId(int classId) {
        classRepository.findById(classId)
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));
        
        return postRepository.findByClassEntityIdOrderByCreatedAtDesc(classId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PostResponse getPostById(int postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
        
        return mapToResponse(post);
    }

    @Override
    public void deletePost(int postId, String username) {
        System.out.println("username: " + username);
        Teacher teacher = teacherRepository.findByUserUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.TEACHER_NOT_EXISTED));
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
        
        // Verify ownership
        if (post.getTeacher().getId() != teacher.getId()) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }
        
        postRepository.delete(post);
    }
    
    private PostResponse mapToResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .classId(post.getClassEntity().getId())
                .className(post.getClassEntity().getName())
                .teacherId(post.getTeacher().getId())
                .teacherName(post.getTeacher().getName())
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .build();
    }
} 