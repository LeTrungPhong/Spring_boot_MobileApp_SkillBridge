package com._NguoiDev.SkillBridge.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Assignment {
    @Id
    private String id;
    private String title;
    private String description;
    private LocalDateTime deadLine;
    private String createBy;
    private LocalDateTime createAt;

    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Attachment> attachments;

    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL)
    private List<Submission> submissions;

    @ManyToOne
    @JoinColumn(name = "classId")
    private Class aClass;
}
