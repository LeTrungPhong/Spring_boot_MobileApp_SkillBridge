package com._NguoiDev.SkillBridge.service.impl;

import com._NguoiDev.SkillBridge.dto.request.CommentRequest;
import com._NguoiDev.SkillBridge.dto.response.CommentResponse;
import com._NguoiDev.SkillBridge.entity.Comment;
import com._NguoiDev.SkillBridge.entity.Post;
import com._NguoiDev.SkillBridge.entity.Student;
import com._NguoiDev.SkillBridge.entity.Teacher;
import com._NguoiDev.SkillBridge.entity.User;
import com._NguoiDev.SkillBridge.exception.AppException;
import com._NguoiDev.SkillBridge.exception.ErrorCode;
import com._NguoiDev.SkillBridge.repository.CommentRepository;
import com._NguoiDev.SkillBridge.repository.PostRepository;
import com._NguoiDev.SkillBridge.repository.StudentRepository;
import com._NguoiDev.SkillBridge.repository.TeacherRepository;
import com._NguoiDev.SkillBridge.repository.UserRepository;
import com._NguoiDev.SkillBridge.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;

    @Override
    public CommentResponse createComment(CommentRequest request, String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
        
        Comment comment = Comment.builder()
                .content(request.getContent())
                .post(post)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();
        
        Comment savedComment = commentRepository.save(comment);
        
        return mapToResponse(savedComment);
    }

    @Override
    public List<CommentResponse> getCommentsByPostId(int postId) {
        // Check if post exists
        postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
        
        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CommentResponse getCommentById(int commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
        
        return mapToResponse(comment);
    }

    @Override
    public void deleteComment(int commentId, String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
        
        // Users can only delete their own comments
        if (!comment.getUser().getUsername().equals(username)) {
            Optional<Teacher> teacherOpt = teacherRepository.findByUserUsername(user.getUsername());
            
            // Allow teachers to delete any comment in their class posts
            if (teacherOpt.isPresent()) {
                Teacher teacher = teacherOpt.get();
                Post post = comment.getPost();
                if (post.getTeacher().getId() != teacher.getId()) {
                    throw new AppException(ErrorCode.ACCESS_DENIED);
                }
            } else {
                throw new AppException(ErrorCode.ACCESS_DENIED);
            }
        }
        
        commentRepository.delete(comment);
    }
    
    private CommentResponse mapToResponse(Comment comment) {
        String userFullName;
        String role;
        
        User user = comment.getUser();
        Optional<Teacher> teacherOpt = teacherRepository.findByUserUsername(user.getUsername());
        
        if (teacherOpt.isPresent()) {
            userFullName = teacherOpt.get().getName();
            role = "TEACHER";
        } else {
            Optional<Student> studentOpt = studentRepository.findByUserUsername(user.getUsername());
            if (studentOpt.isPresent()) {
                userFullName = studentOpt.get().getName();
                role = "STUDENT";
            } else {
                userFullName = "Unknown User";
                role = "UNKNOWN";
            }
        }
        
        return CommentResponse.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .content(comment.getContent())
                .username(comment.getUser().getUsername())
                .userFullName(userFullName)
                .role(role)
                .createdAt(comment.getCreatedAt())
                .build();
    }
} 