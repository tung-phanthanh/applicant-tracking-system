package fptu.sba301.ats.controller;

import fptu.sba301.ats.dto.request.JobUpdateRequest;
import fptu.sba301.ats.dto.response.JobDetailResponse;
import fptu.sba301.ats.dto.response.JobResponse;
import fptu.sba301.ats.entity.User;
import fptu.sba301.ats.exception.BusinessException;
import fptu.sba301.ats.repository.UserRepository;
import fptu.sba301.ats.service.JobCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static fptu.sba301.ats.constant.AppConstant.BASE_URL;

@RestController
@RequestMapping(BASE_URL + "/jobs")
@RequiredArgsConstructor
public class JobCommandController {

    private final JobCommandService jobCommandService;
    private final UserRepository userRepository;

    /** Create is handled by {@link JobController#createJob} (same path/method) to keep a single POST mapping. */

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<JobResponse> updateJob(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable("id") UUID id,
            @Valid @RequestBody JobUpdateRequest request
    ) {
        User user = resolveUser(principal);
        return ResponseEntity.ok(jobCommandService.updateJob(user, id, request));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('HR_MANAGER')")
    public ResponseEntity<List<JobResponse>> listPending(
            @AuthenticationPrincipal UserDetails principal
    ) {
        User user = resolveUser(principal);
        return ResponseEntity.ok(jobCommandService.listPendingJobs(user));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('HR_MANAGER')")
    public ResponseEntity<JobResponse> approve(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable("id") UUID id
    ) {
        User user = resolveUser(principal);
        return ResponseEntity.ok(jobCommandService.approveJob(user, id));
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('HR_MANAGER')")
    public ResponseEntity<JobResponse> reject(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable("id") UUID id
    ) {
        User user = resolveUser(principal);
        return ResponseEntity.ok(jobCommandService.rejectJob(user, id));
    }

    /**
     * HR-only: load job detail for the edit form (same department), without the APPROVED-only restriction
     * applied by {@link JobDetailController#getJobDetail}.
     */
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<JobDetailResponse> getJobForEdit(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable("id") UUID id
    ) {
        User user = resolveUser(principal);
        return ResponseEntity.ok(jobCommandService.getJobForEdit(user, id));
    }

    private User resolveUser(UserDetails principal) {
        return userRepository.findByEmailAndDeletedFalse(principal.getUsername())
                .orElseThrow(() -> new BusinessException("Authenticated user not found", HttpStatus.UNAUTHORIZED));
    }
}
