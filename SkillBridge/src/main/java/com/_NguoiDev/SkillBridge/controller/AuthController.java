package com._NguoiDev.SkillBridge.controller;

import com._NguoiDev.SkillBridge.dto.request.AuthenticationRequest;
import com._NguoiDev.SkillBridge.dto.response.ApiResponse;
import com._NguoiDev.SkillBridge.dto.response.AuthenticationResponse;
import com._NguoiDev.SkillBridge.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
    AuthenticationService authenticationService;
    @PostMapping("/log-in")
    public ApiResponse<AuthenticationResponse> LogIn(@RequestBody AuthenticationRequest authenticationRequest) {
        AuthenticationResponse response = authenticationService.Authentication(authenticationRequest);
        return ApiResponse.<AuthenticationResponse>builder()
                .code(1000)
                .message("success")
                .result(response)
                .build();
    }


}
