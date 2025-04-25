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
public class AssignmentResponse {
    private String id;
    private String title;
    private LocalDateTime deadLine;
    private String createBy;
    private List<String> filesName;

}
