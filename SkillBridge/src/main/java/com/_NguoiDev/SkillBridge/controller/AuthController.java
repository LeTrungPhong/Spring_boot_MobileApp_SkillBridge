package com._NguoiDev.SkillBridge.controller;

import com._NguoiDev.SkillBridge.dto.request.AuthenticationRequest;
import com._NguoiDev.SkillBridge.dto.request.LogoutRequest;
import com._NguoiDev.SkillBridge.dto.request.RefreshRequest;
import com._NguoiDev.SkillBridge.dto.response.ApiResponse;
import com._NguoiDev.SkillBridge.dto.response.AuthenticationResponse;
import com._NguoiDev.SkillBridge.dto.response.IntrospectResponse;
import com._NguoiDev.SkillBridge.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
    AuthenticationService authenticationService;
    @PostMapping("/log-in")
    public ApiResponse<AuthenticationResponse> LogIn(@RequestBody AuthenticationRequest authenticationRequest) throws JOSEException {
        AuthenticationResponse response = authenticationService.Authentication(authenticationRequest);
        return ApiResponse.<AuthenticationResponse>builder()
                .code(1000)
                .message("success")
                .result(response)
                .build();
    }

    @PostMapping("log-out")
    public ApiResponse<Void> Logout(@RequestBody LogoutRequest logoutRequest) throws JOSEException, ParseException {
        authenticationService.Logout(logoutRequest);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("success")
                .build();
    }

    @PostMapping("auth/refresh")
    public ApiResponse<AuthenticationResponse> refreshRequest(@RequestBody RefreshRequest request) throws ParseException, JOSEException {
        AuthenticationResponse authenticationReponse = authenticationService.refreshToken(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(authenticationReponse)
                .code(1000)
                .message("success")
                .build();
    }


}
