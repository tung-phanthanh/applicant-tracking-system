package fptu.sba301.ats.service;

import fptu.sba301.ats.dto.request.CreateOnboardingTaskRequest;
import fptu.sba301.ats.dto.request.UpdateOnboardingTaskRequest;
import fptu.sba301.ats.dto.response.OnboardingProgressResponse;
import fptu.sba301.ats.dto.response.OnboardingTaskResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OnboardingService {
    OnboardingProgressResponse getByApplication(UUID applicationId);
    Page<OnboardingTaskResponse> getTasksPaged(UUID applicationId, Pageable pageable);
    Page<OnboardingTaskResponse> getAllTasks(Pageable pageable);
    OnboardingTaskResponse createTask(CreateOnboardingTaskRequest request);
    OnboardingTaskResponse updateTask(UUID id, UpdateOnboardingTaskRequest request);
    OnboardingTaskResponse toggleTask(UUID id);
}
