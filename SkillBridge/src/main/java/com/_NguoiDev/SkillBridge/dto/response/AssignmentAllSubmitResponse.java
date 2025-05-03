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
public class AssignmentAllSubmitResponse {
    private String id;
    private String title;
    private String description;
    private LocalDateTime deadLine;
    private String createBy;
    private String classId;
    private List<String> filesName;
    private List<SubmissionResponse> submissionResponses;
}
