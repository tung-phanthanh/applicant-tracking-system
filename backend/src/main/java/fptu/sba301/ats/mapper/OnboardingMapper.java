package fptu.sba301.ats.mapper;

import fptu.sba301.ats.dto.request.OnboardingItemRequest;
import fptu.sba301.ats.dto.response.OnboardingChecklistResponse;
import fptu.sba301.ats.dto.response.OnboardingItemResponse;
import fptu.sba301.ats.entity.OnboardingChecklist;
import fptu.sba301.ats.entity.OnboardingItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OnboardingMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "checklist", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    OnboardingItem toEntity(OnboardingItemRequest request);

    OnboardingItemResponse toResponse(OnboardingItem item);

    @Mapping(target = "candidateName", ignore = true)
    @Mapping(target = "totalItems", ignore = true)
    @Mapping(target = "completedItems", ignore = true)
    OnboardingChecklistResponse toResponse(OnboardingChecklist checklist);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "checklist", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    void updateEntity(@MappingTarget OnboardingItem item, OnboardingItemRequest request);
}
