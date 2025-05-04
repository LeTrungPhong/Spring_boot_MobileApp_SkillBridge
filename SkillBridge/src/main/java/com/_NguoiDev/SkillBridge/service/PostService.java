package com._NguoiDev.SkillBridge.service;

import com._NguoiDev.SkillBridge.dto.request.PostRequest;
import com._NguoiDev.SkillBridge.dto.response.PostResponse;
import org.springframework.stereotype.Service;

import java.util.List;

public interface PostService {
    PostResponse createPost(PostRequest request, String username);
    List<PostResponse> getPostsByClassId(int classId);
    PostResponse getPostById(int postId);
    void deletePost(int postId, String username);
} 