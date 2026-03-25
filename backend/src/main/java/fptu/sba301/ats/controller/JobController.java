package fptu.sba301.ats.controller;

import fptu.sba301.ats.dto.JobDTO;
import fptu.sba301.ats.dto.request.CreateJobRequest;
import fptu.sba301.ats.dto.request.UpdateJobRequest;
import fptu.sba301.ats.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
public class JobController {

    private final JobService jobService;

    @PostMapping
    @PreAuthorize("hasAnyRole('HR', 'HR_MANAGER')")
    public ResponseEntity<JobDTO> create(@Valid @RequestBody CreateJobRequest request) {
        JobDTO created = jobService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('HR', 'HR_MANAGER', 'INTERVIEWER')")
    public ResponseEntity<List<JobDTO>> listApproved() {
        return ResponseEntity.ok(jobService.listApprovedJobs());
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('HR_MANAGER')")
    public ResponseEntity<List<JobDTO>> listPending() {
        return ResponseEntity.ok(jobService.listPendingJobs());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR', 'HR_MANAGER', 'INTERVIEWER')")
    public ResponseEntity<JobDTO> getById(@PathVariable UUID id, Authentication authentication) {
        return ResponseEntity.ok(jobService.getById(id, authentication));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR', 'HR_MANAGER')")
    public ResponseEntity<JobDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateJobRequest request
    ) {
        return ResponseEntity.ok(jobService.update(id, request));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('HR_MANAGER')")
    public ResponseEntity<JobDTO> approve(@PathVariable UUID id) {
        return ResponseEntity.ok(jobService.approve(id));
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('HR_MANAGER')")
    public ResponseEntity<JobDTO> reject(@PathVariable UUID id) {
        return ResponseEntity.ok(jobService.reject(id));
    }
}
