package com._NguoiDev.SkillBridge.controller;

import com._NguoiDev.SkillBridge.dto.request.UserDeviceTokenRequest;
import com._NguoiDev.SkillBridge.dto.response.ApiResponse;
import com._NguoiDev.SkillBridge.entity.UserDeviceToken;
import com._NguoiDev.SkillBridge.repository.UserDeviceTokenRepository;
import com._NguoiDev.SkillBridge.service.UserDeviceTokenService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserDeviceTokenController {
    UserDeviceTokenService userDeviceTokenService;
    @PostMapping("/fcmtoken")
    public ApiResponse<Void> saveFcmToken(@RequestBody UserDeviceTokenRequest request) {
        userDeviceTokenService.SaveDeviceToken(request);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("success")
                .build();
    }

    @DeleteMapping("/fcmtoken")
    public ApiResponse<Void> deleteFcmToken(@RequestBody UserDeviceTokenRequest request) {
        userDeviceTokenService.deleteDeviceToken(request);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("success")
                .build();
    }
}
