package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.request.CreateOnboardingTaskRequest;
import fptu.sba301.ats.dto.request.UpdateOnboardingTaskRequest;
import fptu.sba301.ats.dto.response.OnboardingProgressResponse;
import fptu.sba301.ats.dto.response.OnboardingTaskResponse;
import fptu.sba301.ats.entity.Application;
import fptu.sba301.ats.entity.OnboardingTask;
import fptu.sba301.ats.exception.BusinessException;
import fptu.sba301.ats.mapper.OnboardingMapper;
import fptu.sba301.ats.repository.OnboardingTaskRepository;
import fptu.sba301.ats.service.OnboardingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OnboardingServiceImpl implements OnboardingService {

    private final OnboardingTaskRepository taskRepository;
    private final OnboardingMapper onboardingMapper;
    private final EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public OnboardingProgressResponse getByApplication(UUID applicationId) {
        List<OnboardingTask> tasks = taskRepository.findByApplicationIdOrderBySortOrder(applicationId);
        long total = tasks.size();
        long completed = tasks.stream().filter(OnboardingTask::isCompleted).count();
        double percent = total > 0 ? Math.round((double) completed / total * 100.0) : 0;

        return OnboardingProgressResponse.builder()
                .totalTasks((int) total)
                .completedTasks((int) completed)
                .progressPercent(percent)
                .tasks(onboardingMapper.toResponseList(tasks))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OnboardingTaskResponse> getTasksPaged(UUID applicationId, Pageable pageable) {
        return taskRepository.findByApplicationId(applicationId, pageable)
                .map(onboardingMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OnboardingTaskResponse> getAllTasks(Pageable pageable) {
        return taskRepository.findAll(pageable)
                .map(onboardingMapper::toResponse);
    }

    @Override
    public OnboardingTaskResponse createTask(CreateOnboardingTaskRequest request) {
        long count = taskRepository.countByApplicationId(request.getApplicationId());
        OnboardingTask task = onboardingMapper.toEntity(request);
        Application application = entityManager.getReference(Application.class, request.getApplicationId());
        task.setApplication(application);
        task.setSortOrder((int) count);
        task.setCompleted(false);
        return onboardingMapper.toResponse(taskRepository.save(task));
    }

    @Override
    public OnboardingTaskResponse updateTask(UUID id, UpdateOnboardingTaskRequest request) {
        OnboardingTask task = taskRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Onboarding task not found: " + id, HttpStatus.NOT_FOUND));

        onboardingMapper.updateFromRequest(request, task);
        return onboardingMapper.toResponse(taskRepository.save(task));
    }

    @Override
    public OnboardingTaskResponse toggleTask(UUID id) {
        OnboardingTask task = taskRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Onboarding task not found: " + id, HttpStatus.NOT_FOUND));

        task.setCompleted(!task.isCompleted());
        return onboardingMapper.toResponse(taskRepository.save(task));
    }
}
