package fptu.sba301.ats.mapper;

import fptu.sba301.ats.dto.request.CreateScorecardTemplateRequest;
import fptu.sba301.ats.dto.response.ScorecardTemplateResponse;
import fptu.sba301.ats.entity.ScorecardCriterion;
import fptu.sba301.ats.entity.ScorecardTemplate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ScorecardMapper {

    // ScorecardTemplate → ScorecardTemplateResponse
    @Mapping(target = "departmentId",   source = "department.id")
    @Mapping(target = "departmentName", source = "department.name")
    @Mapping(target = "criteria",       source = "criteria")
    ScorecardTemplateResponse toResponse(ScorecardTemplate template);

    List<ScorecardTemplateResponse> toResponseList(List<ScorecardTemplate> templates);

    // CriterionDto → ScorecardCriterion  (template set in service)
    @Mapping(target = "id",       ignore = true)
    @Mapping(target = "template", ignore = true)
    ScorecardCriterion toCriterion(CreateScorecardTemplateRequest.CriterionDto dto);

    // ScorecardCriterion → CriterionResponse
    ScorecardTemplateResponse.CriterionResponse toCriterionResponse(ScorecardCriterion criterion);

    // Update existing entity from request (id and criteria managed separately in service)
    @Mapping(target = "id",         ignore = true)
    @Mapping(target = "criteria",   ignore = true)
    @Mapping(target = "department", ignore = true)
    void updateFromRequest(CreateScorecardTemplateRequest request, @MappingTarget ScorecardTemplate template);
}
