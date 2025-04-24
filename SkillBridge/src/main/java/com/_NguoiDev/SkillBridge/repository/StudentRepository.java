package com._NguoiDev.SkillBridge.repository;

import com._NguoiDev.SkillBridge.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    public Boolean existsByEmail(String email);
    public Optional<Student> findByUserUsername(String username);
}
