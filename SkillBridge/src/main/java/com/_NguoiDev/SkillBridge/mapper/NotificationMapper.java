package com._NguoiDev.SkillBridge.mapper;

import com._NguoiDev.SkillBridge.dto.request.UserDeviceTokenRequest;
import com._NguoiDev.SkillBridge.dto.response.NotificationResponse;
import com._NguoiDev.SkillBridge.entity.MyNotification;
import com._NguoiDev.SkillBridge.entity.UserDeviceToken;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationResponse toNotificationResponse(MyNotification myNotification);
}
