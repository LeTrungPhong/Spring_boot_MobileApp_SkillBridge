package com._NguoiDev.SkillBridge.service;

import com._NguoiDev.SkillBridge.dto.request.UserDeviceTokenRequest;
import com._NguoiDev.SkillBridge.entity.User;
import com._NguoiDev.SkillBridge.entity.UserDeviceToken;
import com._NguoiDev.SkillBridge.exception.AppException;
import com._NguoiDev.SkillBridge.exception.ErrorCode;
import com._NguoiDev.SkillBridge.repository.UserDeviceTokenRepository;
import com._NguoiDev.SkillBridge.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserDeviceTokenService {
    UserDeviceTokenRepository userDeviceTokenRepository;
    UserRepository userRepository;

    public void SaveDeviceToken(UserDeviceTokenRequest deviceToken) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User u = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        UserDeviceToken userDeviceToken = UserDeviceToken.builder()
                .user(u)
                .fcmToken(deviceToken.getFcmToken())
                .build();
        userDeviceTokenRepository.save(userDeviceToken);
    }

    public void deleteDeviceToken() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userDeviceTokenRepository.delete(userDeviceTokenRepository.findByUserUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }

}
