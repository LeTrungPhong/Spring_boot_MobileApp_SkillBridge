package com._NguoiDev.SkillBridge.repository;

import com._NguoiDev.SkillBridge.entity.Class;
import com._NguoiDev.SkillBridge.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Integer> {
    List<Lesson> findByClassEntity(Class classEntity);
    Lesson findLessonById(int id);

    List<Lesson> findByClassEntityId(int idClass);
}