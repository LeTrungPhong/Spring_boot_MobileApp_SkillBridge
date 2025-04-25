package com._NguoiDev.SkillBridge.repository;

import com._NguoiDev.SkillBridge.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, String> {
    Optional<Submission> getSubmissionById(String id);
    Optional<Submission> getSubmissionByUserUsername(String username);
    List<Submission> findAllByAssignmentId(String assignmentId);
}
