package com._NguoiDev.SkillBridge.repository;

import com._NguoiDev.SkillBridge.entity.SubmissionAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubmissionAttachmentRepository extends JpaRepository<SubmissionAttachment, Integer> {
}
