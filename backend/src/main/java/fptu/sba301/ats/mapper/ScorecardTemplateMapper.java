package fptu.sba301.ats.mapper;

import fptu.sba301.ats.dto.request.CreateScorecardTemplateRequest;
import fptu.sba301.ats.dto.response.ScorecardCriterionResponse;
import fptu.sba301.ats.dto.response.ScorecardTemplateResponse;
import fptu.sba301.ats.entity.ScorecardCriterion;
import fptu.sba301.ats.entity.ScorecardTemplate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ScorecardTemplateMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "criteria", ignore = true)
    ScorecardTemplate toEntity(CreateScorecardTemplateRequest request);

    ScorecardTemplateResponse toResponse(ScorecardTemplate template);

    ScorecardCriterionResponse toResponse(ScorecardCriterion criterion);
}
