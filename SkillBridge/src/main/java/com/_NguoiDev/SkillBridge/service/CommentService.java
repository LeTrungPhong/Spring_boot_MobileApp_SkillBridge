package com._NguoiDev.SkillBridge.service;

import com._NguoiDev.SkillBridge.dto.request.CommentRequest;
import com._NguoiDev.SkillBridge.dto.response.CommentResponse;

import java.util.List;

public interface CommentService {
    CommentResponse createComment(CommentRequest request, String username);
    List<CommentResponse> getCommentsByPostId(int postId);
    CommentResponse getCommentById(int commentId);
    void deleteComment(int commentId, String username);
} 