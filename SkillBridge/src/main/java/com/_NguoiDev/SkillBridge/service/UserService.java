package com._NguoiDev.SkillBridge.service;

import com._NguoiDev.SkillBridge.dto.request.UserCreationRequest;
import com._NguoiDev.SkillBridge.dto.response.UserResponse;
import com._NguoiDev.SkillBridge.embedded.AuthorityId;
import com._NguoiDev.SkillBridge.entity.Authority;
import com._NguoiDev.SkillBridge.entity.Student;
import com._NguoiDev.SkillBridge.entity.Teacher;
import com._NguoiDev.SkillBridge.entity.User;
import com._NguoiDev.SkillBridge.enums.Role;
import com._NguoiDev.SkillBridge.exception.AppException;
import com._NguoiDev.SkillBridge.exception.ErrorCode;
import com._NguoiDev.SkillBridge.mapper.StudentMapper;
import com._NguoiDev.SkillBridge.mapper.TeacherMapper;
import com._NguoiDev.SkillBridge.mapper.UserMapper;
import com._NguoiDev.SkillBridge.repository.AuthorityRepository;
import com._NguoiDev.SkillBridge.repository.StudentRepository;
import com._NguoiDev.SkillBridge.repository.TeacherRepository;
import com._NguoiDev.SkillBridge.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    PasswordEncoder passwordEncoder;
    UserRepository userRepository;
    StudentRepository studentRepository;
    TeacherRepository teacherRepository;
    AuthorityRepository authorityRepository;

    UserMapper userMapper;
    StudentMapper studentMapper;
    TeacherMapper teacherMapper;

    public UserResponse createUser(UserCreationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())){
            throw new RuntimeException("Username already exists");
        }
        boolean isStudent = request.getRole().equals(Role.ROLE_STUDENT.getCodeRole());
        boolean isTeacher = request.getRole().equals(Role.ROLE_TEACHER.getCodeRole());

        if (!isStudent&&!isTeacher){
            throw new AppException(ErrorCode.ROLE_INVALID);
        }

        if (isStudent && studentRepository.existsByEmail(request.getEmail())){
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }else if (isTeacher && teacherRepository.existsByEmail(request.getEmail())){
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        User newUser = userMapper.toUser(request);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUser.setEnabled(true);


        if (isStudent){
            Student newStudent = studentMapper.toStudent(request);
            newStudent.setUser(newUser);
            studentRepository.save(newStudent);
        }else {
            Teacher newTeacher = teacherMapper.toTeacher(request);
            newTeacher.setUser(newUser);
            teacherRepository.save(newTeacher);
        }

        AuthorityId newAuthorId = AuthorityId.builder()
                .username(newUser.getUsername())
                .authority(request.getRole())
                .build();

        authorityRepository.save(Authority.builder()
                .authorityId(newAuthorId)
                .user(newUser)
                .build());

        return userMapper.toUserResponse(request);
    }
}
