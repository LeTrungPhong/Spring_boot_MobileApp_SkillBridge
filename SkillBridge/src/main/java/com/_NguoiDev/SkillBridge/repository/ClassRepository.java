package com._NguoiDev.SkillBridge.repository;

import com._NguoiDev.SkillBridge.entity.Class;
import com._NguoiDev.SkillBridge.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassRepository extends JpaRepository<Class, Integer> {
    List<Class> findByTeacher(Teacher teacher);
    List<Class> findAllByTeacherUserUsername(String username);
} 