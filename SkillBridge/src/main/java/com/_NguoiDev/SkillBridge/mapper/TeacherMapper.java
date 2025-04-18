package com._NguoiDev.SkillBridge.mapper;

import com._NguoiDev.SkillBridge.dto.request.UserCreationRequest;
import com._NguoiDev.SkillBridge.entity.Student;
import com._NguoiDev.SkillBridge.entity.Teacher;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TeacherMapper {
    Teacher toTeacher(UserCreationRequest request);
}
