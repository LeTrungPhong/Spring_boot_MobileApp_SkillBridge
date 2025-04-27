package com._NguoiDev.SkillBridge.repository;

import com._NguoiDev.SkillBridge.embedded.StudentClassId;
import com._NguoiDev.SkillBridge.entity.Class;
import com._NguoiDev.SkillBridge.entity.Student;
import com._NguoiDev.SkillBridge.entity.StudentClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentClassRepository extends JpaRepository<StudentClass, StudentClassId> {
    List<StudentClass> findByStudent(Student student);
    List<StudentClass> findByClassEntity(Class classEntity);
    Optional<StudentClass> findByStudentAndClassEntity(Student student, Class classEntity);
    boolean existsByStudentAndClassEntity(Student student, Class classEntity);

    List<StudentClass> findAllByClassEntity_Id(int classEntityId);
}