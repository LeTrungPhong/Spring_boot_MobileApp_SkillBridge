package com._NguoiDev.SkillBridge.service.impl;

import com._NguoiDev.SkillBridge.dto.request.TeacherCreateRequest;
import com._NguoiDev.SkillBridge.dto.response.TeacherResponse;
import com._NguoiDev.SkillBridge.entity.Authority;
import com._NguoiDev.SkillBridge.entity.Teacher;
import com._NguoiDev.SkillBridge.entity.User;
import com._NguoiDev.SkillBridge.repository.AuthorityRepository;
import com._NguoiDev.SkillBridge.repository.TeacherRepository;
import com._NguoiDev.SkillBridge.repository.UserRepository;
import com._NguoiDev.SkillBridge.service.TeacherService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;

    @Override
    @Transactional
    public TeacherResponse createTeacher(TeacherCreateRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        }

        // Check if email already exists
        if (teacherRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }

        // Create user account
        User user = User.builder()
                .username(request.getUsername())
                .password(request.getPassword()) // In a real app, this should be encrypted
                .enabled(true)
                .authorities(new ArrayList<>())
                .build();

        // Create teacher
        Teacher teacher = Teacher.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .user(user)
                .build();

        // Save teacher (will cascade to user)
        Teacher savedTeacher = teacherRepository.save(teacher);

        // Add ROLE_TEACHER authority
        Authority authority = Authority.builder()
                .user(user)
                .authority("ROLE_TEACHER")
                .build();
        authorityRepository.save(authority);

        return mapToTeacherResponse(savedTeacher);
    }

    @Override
    public TeacherResponse getTeacherById(int id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Teacher not found with id: " + id));
        return mapToTeacherResponse(teacher);
    }

    @Override
    public List<TeacherResponse> getAllTeachers() {
        return teacherRepository.findAll().stream()
                .map(this::mapToTeacherResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TeacherResponse updateTeacher(int id, TeacherCreateRequest request) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Teacher not found with id: " + id));

        // Check if the new email is unique (unless it's the same as the current one)
        if (!teacher.getEmail().equals(request.getEmail()) && teacherRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }

        // Update teacher information
        teacher.setName(request.getName());
        teacher.setEmail(request.getEmail());
        teacher.setPhone(request.getPhone());

        // Save updated teacher
        Teacher updatedTeacher = teacherRepository.save(teacher);
        return mapToTeacherResponse(updatedTeacher);
    }

    @Override
    @Transactional
    public void deleteTeacher(int id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Teacher not found with id: " + id));
        
        // Delete teacher (will cascade to delete user and authorities)
        teacherRepository.delete(teacher);
    }

    private TeacherResponse mapToTeacherResponse(Teacher teacher) {
        return TeacherResponse.builder()
                .id(teacher.getId())
                .name(teacher.getName())
                .email(teacher.getEmail())
                .phone(teacher.getPhone())
                .build();
    }
} 