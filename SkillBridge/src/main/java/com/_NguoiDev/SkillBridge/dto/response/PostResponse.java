package com._NguoiDev.SkillBridge.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostResponse {
    private int id;
    private int classId;
    private String className;
    private int teacherId;
    private String teacherName;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private List<CommentResponse> comments;
} 