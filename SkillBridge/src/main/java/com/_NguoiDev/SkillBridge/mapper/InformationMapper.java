package com._NguoiDev.SkillBridge.mapper;

import com._NguoiDev.SkillBridge.controller.InformationResponse;
import com._NguoiDev.SkillBridge.entity.Student;
import com._NguoiDev.SkillBridge.entity.Submission;
import com._NguoiDev.SkillBridge.entity.Teacher;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InformationMapper {
    InformationResponse toInformationResponse(Student student);
    InformationResponse toInformationResponse(Teacher teacher);
}
