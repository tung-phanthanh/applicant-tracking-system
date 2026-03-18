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

    default OnboardingTaskResponse toResponse(OnboardingTask task) {
        if (task == null) return null;
        return OnboardingTaskResponse.builder()
                .id(task.getId())
                .applicationId(task.getApplication() != null ? task.getApplication().getId() : null)
                .title(task.getTitle())
                .category(task.getCategory())
                .assignedTo(task.getAssignedTo())
                .dueDate(task.getDueDate())
                .completed(task.isCompleted())
                .sortOrder(task.getSortOrder())
                .build();
    }

    List<OnboardingTaskResponse> toResponseList(List<OnboardingTask> tasks);

    @Mapping(target = "id",          ignore = true)
    @Mapping(target = "completed",   ignore = true)
    @Mapping(target = "sortOrder",   ignore = true)
    @Mapping(target = "application", ignore = true)
    OnboardingTask toEntity(CreateOnboardingTaskRequest request);

    @Mapping(target = "id",          ignore = true)
    @Mapping(target = "application", ignore = true)
    @Mapping(target = "sortOrder",   ignore = true)
    void updateFromRequest(UpdateOnboardingTaskRequest request, @MappingTarget OnboardingTask task);
}
