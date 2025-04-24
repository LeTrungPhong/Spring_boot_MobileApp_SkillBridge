package com._NguoiDev.SkillBridge.entity;

import com._NguoiDev.SkillBridge.embedded.AttendanceCheckId;
import com._NguoiDev.SkillBridge.enums.AttendanceStatus;
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
@Table(name = "attendance_check")
public class AttendanceCheck {
    @EmbeddedId
    private AttendanceCheckId id;
    
    @MapsId("studentId")
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
    
    @MapsId("lessonId")
    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;
    
    @Column(name = "checkin_date")
    private LocalDateTime checkinDate;
    
    @Enumerated(EnumType.STRING)
    private AttendanceStatus status;
} 