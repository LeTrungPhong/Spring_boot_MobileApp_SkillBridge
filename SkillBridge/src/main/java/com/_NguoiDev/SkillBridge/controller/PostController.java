package com._NguoiDev.SkillBridge.controller;

import com._NguoiDev.SkillBridge.dto.request.PostRequest;
import com._NguoiDev.SkillBridge.dto.response.PostResponse;
import com._NguoiDev.SkillBridge.exception.AppException;
import com._NguoiDev.SkillBridge.exception.ErrorCode;
import com._NguoiDev.SkillBridge.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    
    private final PostService postService;
    
    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @RequestBody PostRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        System.out.println("authentication: " + authentication.getName());
        PostResponse response = postService.createPost(request, authentication.getName());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping("/class/{classId}")
    public ResponseEntity<List<PostResponse>> getPostsByClassId(@PathVariable int classId) {
        List<PostResponse> posts = postService.getPostsByClassId(classId);
        return ResponseEntity.ok(posts);
    }
    
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable int postId) {
        PostResponse post = postService.getPostById(postId);
        return ResponseEntity.ok(post);
    }
    
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable int postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        postService.deletePost(postId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
} 