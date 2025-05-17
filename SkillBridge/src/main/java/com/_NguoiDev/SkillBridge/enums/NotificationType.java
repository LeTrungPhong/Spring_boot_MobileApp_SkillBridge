package com._NguoiDev.SkillBridge.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationType {
    POST("POST"),
    ASSIGNMENT("ASSIGNMENT"),
    UNKNOW("UNKNOW");

    private final String nameType;
}
