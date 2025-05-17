package com._NguoiDev.SkillBridge.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String body;
    private LocalDateTime createdAt;
    @Column(nullable = true)
    private Integer postId;
    @Column(nullable = true)
    private String assignmentId;

    @ManyToOne
    @JoinColumn(name = "classId")
    private Class aClass;
}
