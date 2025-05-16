package com._NguoiDev.SkillBridge.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Submission {
    @Id
    private String id;
    private LocalDateTime submissionTime;
    private int point;
    
    @Column(length = 1000)
    private String feedback;

    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username")
    private User user;

    @ManyToOne
    @JoinColumn(name = "assignmentId", referencedColumnName = "id")
    private Assignment assignment;

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL)
    private List<SubmissionAttachment> submissionAttachments;
}
