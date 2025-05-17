package com._NguoiDev.SkillBridge.mapper;

import com._NguoiDev.SkillBridge.dto.request.UserDeviceTokenRequest;
import com._NguoiDev.SkillBridge.dto.response.NotificationResponse;
import com._NguoiDev.SkillBridge.entity.MyNotification;
import com._NguoiDev.SkillBridge.entity.UserDeviceToken;
import com._NguoiDev.SkillBridge.enums.NotificationType;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    @Mapping(target = "classId", source = "AClass.id")
    @Mapping(target = "className", source = "AClass.name")
    @Mapping(target = "assignmentId", source = "assignmentId")
    @Mapping(target = "postId", source = "postId")
    @Mapping(target = "type", expression = "java(getType(myNotification))")
    NotificationResponse toNotificationResponse(MyNotification myNotification);

    default String getType(MyNotification notification) {
        String type = null;
        if (notification.getAssignmentId() != null) {
            type=NotificationType.ASSIGNMENT.getNameType();
        } else if (notification.getPostId() != null) {
            type=NotificationType.POST.getNameType();
        } else {
            type=NotificationType.UNKNOW.getNameType();
        }
        return type;
    }
}
