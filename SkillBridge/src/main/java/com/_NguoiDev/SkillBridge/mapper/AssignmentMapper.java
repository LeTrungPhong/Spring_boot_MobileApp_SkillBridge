package com._NguoiDev.SkillBridge.mapper;

import com._NguoiDev.SkillBridge.dto.request.AssignmentRequest;
import com._NguoiDev.SkillBridge.dto.response.AssignmentAllSubmitResponse;
import com._NguoiDev.SkillBridge.dto.response.AssignmentResponse;
import com._NguoiDev.SkillBridge.entity.Assignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AssignmentMapper {
    Assignment toAssignment(AssignmentRequest assignmentRequest);
    @Mapping(target = "classId", source = "AClass.id")
    AssignmentResponse toAssignmentResponse(Assignment assignment);
    @Mapping(target = "classId", source = "AClass.id")
    AssignmentAllSubmitResponse toAssignmentAllSubmitResponse(Assignment assignment);
}
