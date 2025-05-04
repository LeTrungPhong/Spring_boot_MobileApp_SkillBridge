package com._NguoiDev.SkillBridge.repository;

import com._NguoiDev.SkillBridge.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByPostIdOrderByCreatedAtDesc(int postId);
    List<Comment> findByUserUsername(String username);
} 