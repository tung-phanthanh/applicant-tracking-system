package fptu.sba301.ats.mapper;

import fptu.sba301.ats.dto.request.CreateScorecardTemplateRequest;
import fptu.sba301.ats.dto.response.ScorecardCriterionResponse;
import fptu.sba301.ats.dto.response.ScorecardTemplateResponse;
import fptu.sba301.ats.entity.ScorecardCriterion;
import fptu.sba301.ats.entity.ScorecardTemplate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @org.mapstruct.Builder(disableBuilder = true))
public interface ScorecardTemplateMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "department.id", source = "departmentId")
    ScorecardTemplate toEntity(CreateScorecardTemplateRequest request);

    @Mapping(target = "departmentId", source = "department.id")
    @Mapping(target = "criteria", ignore = true)
    ScorecardTemplateResponse toResponse(ScorecardTemplate template);

    ScorecardCriterionResponse toResponse(ScorecardCriterion criterion);
}
