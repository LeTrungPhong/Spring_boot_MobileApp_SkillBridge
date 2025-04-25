package com._NguoiDev.SkillBridge.repository;

import com._NguoiDev.SkillBridge.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachRepository extends JpaRepository<Attachment, Integer> {
    List<Attachment> findAllByAssignmentId(String assignmentId);
}
