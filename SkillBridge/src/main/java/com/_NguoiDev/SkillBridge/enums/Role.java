package com._NguoiDev.SkillBridge.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
    ROLE_STUDENT("STUDENT", "Sinh viên"),
    ROLE_TEACHER("TEACHER", "Giáo viên");

    private final String codeRole;
    private final String nameRole;
}
