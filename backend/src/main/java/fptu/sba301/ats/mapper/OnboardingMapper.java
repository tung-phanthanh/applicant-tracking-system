package fptu.sba301.ats.mapper;

import fptu.sba301.ats.dto.request.CreateOnboardingTaskRequest;
import fptu.sba301.ats.dto.request.UpdateOnboardingTaskRequest;
import fptu.sba301.ats.dto.response.OnboardingTaskResponse;
import fptu.sba301.ats.entity.OnboardingTask;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface OnboardingMapper {

    @Mapping(target = "applicationId", source = "application.id")
    OnboardingTaskResponse toResponse(OnboardingTask task);

    List<OnboardingTaskResponse> toResponseList(List<OnboardingTask> tasks);

    // CreateOnboardingTaskRequest → OnboardingTask (id, completed, sortOrder, application set in service)
    @Mapping(target = "id",          ignore = true)
    @Mapping(target = "completed",   ignore = true)
    @Mapping(target = "sortOrder",   ignore = true)
    @Mapping(target = "application", ignore = true)
    OnboardingTask toEntity(CreateOnboardingTaskRequest request);

    // Patch existing task from update request (nulls are ignored via strategy)
    @Mapping(target = "id",          ignore = true)
    @Mapping(target = "application", ignore = true)
    @Mapping(target = "sortOrder",   ignore = true)
    void updateFromRequest(UpdateOnboardingTaskRequest request, @MappingTarget OnboardingTask task);
}
