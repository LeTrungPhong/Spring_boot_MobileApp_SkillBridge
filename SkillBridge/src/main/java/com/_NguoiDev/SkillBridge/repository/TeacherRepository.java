package com._NguoiDev.SkillBridge.repository;

import com._NguoiDev.SkillBridge.entity.Student;
import com._NguoiDev.SkillBridge.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Integer> {
    public boolean existsByEmail(String email);
}
