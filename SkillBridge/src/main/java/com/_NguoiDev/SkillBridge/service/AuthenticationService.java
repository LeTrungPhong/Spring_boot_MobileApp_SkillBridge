package com._NguoiDev.SkillBridge.service;

import com._NguoiDev.SkillBridge.dto.request.AuthenticationRequest;
import com._NguoiDev.SkillBridge.dto.response.AuthenticationResponse;
import com._NguoiDev.SkillBridge.entity.User;
import com._NguoiDev.SkillBridge.repository.UserRepository;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
public class AuthenticationService {
    UserRepository userRepository;
    public AuthenticationResponse Authentication(AuthenticationRequest request) {
        User userLogIn = userRepository.findByUsername(request.getUsername())
                .orElseThrow(()-> new RuntimeException("User not found"));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(request.getPassword(), userLogIn.getPassword());
        if (!authenticated){
            throw new RuntimeException("Wrong password");
        }
        return AuthenticationResponse.builder()
                .authenticated(authenticated)
                .build();

    }
}
