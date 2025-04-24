package com._NguoiDev.SkillBridge.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Class {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    private int number_of_weeks;
    
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    private String name;
    
    @OneToMany(mappedBy = "classEntity", cascade = CascadeType.ALL)
    private List<Lesson> lessons;
    
    @OneToMany(mappedBy = "classEntity", cascade = CascadeType.ALL)
    private Set<StudentClass> enrolledStudents;

    @OneToMany(mappedBy = "aClass", cascade = CascadeType.ALL)
    private List<Assignment> assignments;
} 