package fptu.sba301.ats.mapper;

import fptu.sba301.ats.dto.response.InterviewScoreResponse;
import fptu.sba301.ats.entity.InterviewScore;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface InterviewScoreMapper {

    @Mapping(target = "interviewId",     source = "interview.id")
    @Mapping(target = "interviewerId",   source = "interviewer.id")
    @Mapping(target = "interviewerName", source = "interviewer.fullName")
    @Mapping(target = "criterionId",     source = "criterion.id")
    @Mapping(target = "criterionName",   source = "criterion.name")
    InterviewScoreResponse toResponse(InterviewScore score);

    List<InterviewScoreResponse> toResponseList(List<InterviewScore> scores);
}
