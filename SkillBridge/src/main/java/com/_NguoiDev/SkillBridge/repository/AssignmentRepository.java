package com._NguoiDev.SkillBridge.repository;

import com._NguoiDev.SkillBridge.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, String> {
    List<Assignment> findAllByaClassId(int aClassId);
}
