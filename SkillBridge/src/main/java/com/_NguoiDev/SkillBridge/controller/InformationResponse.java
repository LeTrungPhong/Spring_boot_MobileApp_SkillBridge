package com._NguoiDev.SkillBridge.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InformationResponse {
    private int id;
    private String name;
    private String role;
    private String email;
    private String username;
    private String phone;
}
