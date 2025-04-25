package com._NguoiDev.SkillBridge.repository;

import com._NguoiDev.SkillBridge.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, String> {
    List<Assignment> findAllByaClassId(int aClassId);

    Optional<Assignment> getAssignmentsById(String id);
}
