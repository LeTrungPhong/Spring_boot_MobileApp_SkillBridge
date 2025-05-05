package com._NguoiDev.SkillBridge.controller;

import com._NguoiDev.SkillBridge.dto.request.UserCreationRequest;
import com._NguoiDev.SkillBridge.dto.response.ApiResponse;
import com._NguoiDev.SkillBridge.dto.response.UserResponse;
import com._NguoiDev.SkillBridge.entity.User;
import com._NguoiDev.SkillBridge.repository.UserRepository;
import com._NguoiDev.SkillBridge.service.UserService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;
    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@RequestBody UserCreationRequest request) {
        UserResponse userResponse = userService.createUser(request);
        return ApiResponse.<UserResponse>builder()
                .code(1000)
                .message("success")
                .result(userResponse)
                .build();
    }

    @GetMapping("/user/{username}")
    public ApiResponse<?> getUser(@PathVariable String username) {
        List<User> listUser = userService.findAllUsernameNotAndHasMessage(username);
        List<UserResponse> responseList = listUser.stream()
                .map(this::convertUserToDTO)
                .toList();

        return ApiResponse.<List<UserResponse>>builder()
                .code(1000)
                .message("success")
                .result(responseList)
                .build();
    }

    public UserResponse convertUserToDTO(User user) {
        return  UserResponse
                .builder()
                .username(user.getUsername())
                .build();
    }

    @GetMapping("/api/user/search/{username}")
    public ApiResponse<List<UserResponse>> searchUser(@PathVariable String username) {
        List<User> listUser = userService.findUserByUsername(username);
        List<UserResponse> listResult = listUser.stream().map(this::convertUserToDTO).toList();
        return ApiResponse.<List<UserResponse>>builder()
                .code(1000)
                .message("success")
                .result(listResult)
                .build();
    }
}
