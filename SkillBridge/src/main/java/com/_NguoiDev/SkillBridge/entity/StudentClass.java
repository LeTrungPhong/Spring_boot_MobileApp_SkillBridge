package com._NguoiDev.SkillBridge.entity;

import com._NguoiDev.SkillBridge.embedded.StudentClassId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "student_class")
public class StudentClass {
    @EmbeddedId
    private StudentClassId id;
    
    @MapsId("studentId")
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
    
    @MapsId("classId")
    @ManyToOne
    @JoinColumn(name = "class_id")
    private Class classEntity;
    
    @Column(name = "joined_at")
    private LocalDateTime joinedAt;
    
    private boolean active;
} 