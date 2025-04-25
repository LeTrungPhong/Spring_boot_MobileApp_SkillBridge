package com._NguoiDev.SkillBridge.mapper;

import com._NguoiDev.SkillBridge.dto.request.AssignmentRequest;
import com._NguoiDev.SkillBridge.dto.response.AssignmentAllSubmitResponse;
import com._NguoiDev.SkillBridge.dto.response.AssignmentResponse;
import com._NguoiDev.SkillBridge.entity.Assignment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AssignmentMapper {
    Assignment toAssignment(AssignmentRequest assignmentRequest);
    AssignmentResponse toAssignmentResponse(Assignment assignment);
    AssignmentAllSubmitResponse toAssignmentAllSubmitResponse(Assignment assignment);
}
