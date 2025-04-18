package com._NguoiDev.SkillBridge.mapper;

import com._NguoiDev.SkillBridge.dto.request.UserCreationRequest;
import com._NguoiDev.SkillBridge.dto.response.UserResponse;
import com._NguoiDev.SkillBridge.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);
    UserResponse toUserResponse(UserCreationRequest user);
}
