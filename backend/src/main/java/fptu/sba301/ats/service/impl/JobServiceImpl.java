package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.JobDTO;
import fptu.sba301.ats.dto.request.CreateJobRequest;
import fptu.sba301.ats.dto.request.UpdateJobRequest;
import fptu.sba301.ats.entity.Department;
import fptu.sba301.ats.entity.Job;
import fptu.sba301.ats.entity.JobApproval;
import fptu.sba301.ats.entity.User;
import fptu.sba301.ats.enums.ApprovalStatus;
import fptu.sba301.ats.enums.JobStatus;
import fptu.sba301.ats.exception.BusinessException;
import fptu.sba301.ats.mapper.JobMapper;
import fptu.sba301.ats.repository.DepartmentRepository;
import fptu.sba301.ats.repository.JobApprovalRepository;
import fptu.sba301.ats.repository.JobRepository;
import fptu.sba301.ats.repository.UserRepository;
import fptu.sba301.ats.security.UserPrincipal;
import fptu.sba301.ats.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final DepartmentRepository departmentRepository;
    private final JobApprovalRepository jobApprovalRepository;
    private final UserRepository userRepository;
    private final JobMapper jobMapper;

    @Override
    @Transactional
    public JobDTO create(CreateJobRequest request) {
        Department department = resolveDepartment(
                request.getDepartmentId(),
                request.getDepartmentName()
        );
        Job job = jobMapper.toNewEntity(request, department);
        User creator = currentUser();
        if (creator != null) {
            job.setCreatedBy(creator.getId());
        }
        Job saved = jobRepository.save(job);
        jobApprovalRepository.save(JobApproval.builder()
                .job(saved)
                .status(ApprovalStatus.PENDING)
                .approvedBy(null)
                .createdBy(creator != null ? creator.getId() : null)
                .build());
        return jobMapper.toDto(reloadWithDepartment(saved.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobDTO> listApprovedJobs() {
        return jobRepository.findAllByStatusWithDepartment(JobStatus.APPROVED).stream()
                .map(jobMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobDTO> listPendingJobs() {
        return jobRepository.findAllByStatusWithDepartment(JobStatus.PENDING_APPROVAL).stream()
                .map(jobMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public JobDTO getById(UUID id, Authentication authentication) {
        Job job = jobRepository.findByIdWithDepartment(id)
                .orElseThrow(() -> new BusinessException("Job not found", HttpStatus.NOT_FOUND));

        if (job.getStatus() == JobStatus.APPROVED) {
            return jobMapper.toDto(job);
        }

        if (!canViewNonPublicJob(authentication)) {
            throw new BusinessException("You cannot view this job", HttpStatus.FORBIDDEN);
        }

        return jobMapper.toDto(job);
    }

    @Override
    @Transactional
    public JobDTO update(UUID id, UpdateJobRequest request) {
        Job job = jobRepository.findByIdWithDepartment(id)
                .orElseThrow(() -> new BusinessException("Job not found", HttpStatus.NOT_FOUND));

        Department department = resolveDepartment(
                request.getDepartmentId(),
                request.getDepartmentName()
        );
        jobMapper.applyUpdate(job, request, department);
        jobRepository.save(job);
        return jobMapper.toDto(reloadWithDepartment(id));
    }

    @Override
    @Transactional
    public JobDTO approve(UUID id) {
        Job job = jobRepository.findByIdWithDepartment(id)
                .orElseThrow(() -> new BusinessException("Job not found", HttpStatus.NOT_FOUND));

        if (job.getStatus() != JobStatus.PENDING_APPROVAL) {
            throw new BusinessException("Only jobs pending approval can be approved", HttpStatus.BAD_REQUEST);
        }

        job.setStatus(JobStatus.APPROVED);
        jobRepository.save(job);
        User approver = currentUser();
        if (approver != null) {
            jobApprovalRepository.findTopByJob_IdAndStatusOrderByCreatedAtDesc(id, ApprovalStatus.PENDING)
                    .ifPresentOrElse(
                            row -> {
                                row.setStatus(ApprovalStatus.APPROVED);
                                row.setApprovedBy(approver);
                                row.setModifiedBy(approver.getId());
                                jobApprovalRepository.save(row);
                            },
                            () -> jobApprovalRepository.save(JobApproval.builder()
                                    .job(job)
                                    .status(ApprovalStatus.APPROVED)
                                    .approvedBy(approver)
                                    .comment("Approved")
                                    .createdBy(approver.getId())
                                    .build())
                    );
        }
        return jobMapper.toDto(reloadWithDepartment(id));
    }

    @Override
    @Transactional
    public JobDTO reject(UUID id) {
        Job job = jobRepository.findByIdWithDepartment(id)
                .orElseThrow(() -> new BusinessException("Job not found", HttpStatus.NOT_FOUND));

        if (job.getStatus() != JobStatus.PENDING_APPROVAL) {
            throw new BusinessException("Only jobs pending approval can be rejected", HttpStatus.BAD_REQUEST);
        }

        job.setStatus(JobStatus.REJECTED);
        jobRepository.save(job);
        User reviewer = currentUser();
        if (reviewer != null) {
            jobApprovalRepository.findTopByJob_IdAndStatusOrderByCreatedAtDesc(id, ApprovalStatus.PENDING)
                    .ifPresentOrElse(
                            row -> {
                                row.setStatus(ApprovalStatus.REJECTED);
                                row.setApprovedBy(reviewer);
                                row.setModifiedBy(reviewer.getId());
                                jobApprovalRepository.save(row);
                            },
                            () -> jobApprovalRepository.save(JobApproval.builder()
                                    .job(job)
                                    .status(ApprovalStatus.REJECTED)
                                    .approvedBy(reviewer)
                                    .comment("Rejected")
                                    .createdBy(reviewer.getId())
                                    .build())
                    );
        }
        return jobMapper.toDto(reloadWithDepartment(id));
    }

    private Job reloadWithDepartment(UUID id) {
        return jobRepository.findByIdWithDepartment(id)
                .orElseThrow(() -> new BusinessException("Job not found", HttpStatus.NOT_FOUND));
    }

    private Department resolveDepartment(UUID departmentId, String departmentName) {
        if (departmentId != null) {
            return departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new BusinessException("Department not found", HttpStatus.BAD_REQUEST));
        }
        if (departmentName != null && !departmentName.isBlank()) {
            return departmentRepository.findByNameIgnoreCase(departmentName.trim()).orElse(null);
        }
        return null;
    }

    private boolean canViewNonPublicJob(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a ->
                        "ROLE_HR".equals(a)
                                || "ROLE_HR_MANAGER".equals(a)
                                || "ROLE_SYSTEM_ADMIN".equals(a)
                );
    }

    private User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal principal)) {
            return null;
        }
        return userRepository.findById(principal.getId()).orElse(null);
    }
}
