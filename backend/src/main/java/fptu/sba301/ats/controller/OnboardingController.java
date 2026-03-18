package fptu.sba301.ats.controller;

import fptu.sba301.ats.dto.request.CreateOnboardingTaskRequest;
import fptu.sba301.ats.dto.request.UpdateOnboardingTaskRequest;
import fptu.sba301.ats.dto.response.OnboardingProgressResponse;
import fptu.sba301.ats.dto.response.OnboardingTaskResponse;
import fptu.sba301.ats.service.OnboardingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static fptu.sba301.ats.constant.AppConstant.BASE_URL;
import static fptu.sba301.ats.constant.AppConstant.ONBOARDING_CONTROLLER_URL;

@RestController
@RequestMapping(BASE_URL + ONBOARDING_CONTROLLER_URL)
@RequiredArgsConstructor
public class OnboardingController {

    private final OnboardingService onboardingService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('HR', 'HR_MANAGER', 'SYSTEM_ADMIN')")
    public ResponseEntity<Page<OnboardingTaskResponse>> getAllTasks(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(onboardingService.getAllTasks(pageable));
    }

    // GET /onboarding/application/{id}/progress — full list + progress summary
    @GetMapping("/application/{applicationId}/progress")
    @PreAuthorize("hasAnyAuthority('HR', 'HR_MANAGER')")
    public ResponseEntity<OnboardingProgressResponse> getProgress(@PathVariable UUID applicationId) {
        return ResponseEntity.ok(onboardingService.getByApplication(applicationId));
    }

    // GET /onboarding/application/{id}?page=0&size=20 — paginated tasks
    @GetMapping("/application/{applicationId}")
    @PreAuthorize("hasAnyAuthority('HR', 'HR_MANAGER')")
    public ResponseEntity<Page<OnboardingTaskResponse>> getTasksPaged(
            @PathVariable UUID applicationId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(onboardingService.getTasksPaged(applicationId, pageable));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('HR', 'HR_MANAGER')")
    public ResponseEntity<OnboardingTaskResponse> createTask(
            @Valid @RequestBody CreateOnboardingTaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(onboardingService.createTask(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('HR', 'HR_MANAGER')")
    public ResponseEntity<OnboardingTaskResponse> updateTask(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateOnboardingTaskRequest request) {
        return ResponseEntity.ok(onboardingService.updateTask(id, request));
    }

    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasAnyAuthority('HR', 'HR_MANAGER')")
    public ResponseEntity<OnboardingTaskResponse> toggleTask(@PathVariable UUID id) {
        return ResponseEntity.ok(onboardingService.toggleTask(id));
    }
}
