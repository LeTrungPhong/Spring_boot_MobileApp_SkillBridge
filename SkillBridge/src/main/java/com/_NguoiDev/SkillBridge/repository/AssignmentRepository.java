package com._NguoiDev.SkillBridge.repository;

import com._NguoiDev.SkillBridge.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, String> {
    List<Assignment> findAllByaClassId(int aClassId);

    Optional<Assignment> getAssignmentsById(String id);

    @Query(value = "select a.* from assignment a " +
            "where a.class_id=:idClass and a.id IN ( " +
            "  select ua.assignment_id from user_assignment ua  " +
            "  where ua.username = :username" +
            ")",nativeQuery = true)
    List<Assignment> findAllStudentAssignmentByClass(@Param("idClass") int idClass,
                                                     @Param("username") String username);
}
