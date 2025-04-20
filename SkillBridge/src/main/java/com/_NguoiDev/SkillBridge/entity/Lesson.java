package com._NguoiDev.SkillBridge.entity;

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
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @ManyToOne
    @JoinColumn(name = "class_id")
    private Class classEntity;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "is_completed")
    private boolean isCompleted;
    
    @Column(name = "lesson_date")
    private LocalDateTime lessonDate;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    private String notes;
    
    private String room;
} 