package com._NguoiDev.SkillBridge.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeacherCreateRequest {
    private String name;
    private String email;
    private String phone;
    private String username;
    private String password;
} 