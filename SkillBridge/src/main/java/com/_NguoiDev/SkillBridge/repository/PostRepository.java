package com._NguoiDev.SkillBridge.repository;

import com._NguoiDev.SkillBridge.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findByClassEntityId(int classId);
    List<Post> findByTeacherId(int teacherId);
    List<Post> findByClassEntityIdOrderByCreatedAtDesc(int classId);
} 