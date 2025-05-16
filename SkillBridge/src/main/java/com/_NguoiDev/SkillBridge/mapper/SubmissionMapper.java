package com._NguoiDev.SkillBridge.mapper;

import com._NguoiDev.SkillBridge.dto.request.SubmissionRequest;
import com._NguoiDev.SkillBridge.dto.response.SubmissionResponse;
import com._NguoiDev.SkillBridge.entity.Submission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubmissionMapper {
    Submission toSubmission(SubmissionRequest submissionRequest);
    @Mapping(target = "submissionBy", source = "user.username")
    @Mapping(target = "point", source = "point")
    @Mapping(target = "feedback", source = "feedback")
    SubmissionResponse toSubmissionResponse(Submission submission);
}
