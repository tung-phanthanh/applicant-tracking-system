package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.response.JobResponse;
import fptu.sba301.ats.entity.Job;
import fptu.sba301.ats.repository.JobRepository;
import fptu.sba301.ats.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {
    private final JobRepository jobRepository;

    @Override
    public List<Job> findAll() {
        return jobRepository.findAll();
    }

    @Override
    public Optional<Job> findById(UUID id) {
        return jobRepository.findById(id);
    }

    @Override
    public Job save(Job job) {
        // if (job.getCreatedAt() == null) {
        //     job.setCreatedAt(Instant.now());
        // }
        return jobRepository.save(job);
    }

    @Override
    public Job update(UUID id, Job job) {
        return jobRepository.findById(id)
                .map(existing -> {
                    existing.setTitle(job.getTitle());
                    existing.setDepartment(job.getDepartment());
                    existing.setDescription(job.getDescription());
                    existing.setStatus(job.getStatus());
                    // postedDate should not change on update
                    return jobRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Job not found: " + id));
    }

    @Override
    public void delete(UUID id) {
        jobRepository.deleteById(id);
    }

    @Override
    public List<JobResponse> getAllJobs() {
        return jobRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public JobResponse getJobById(UUID id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        return mapToResponse(job);
    }

    @Override
    public long countActiveJobs() {
        return jobRepository.findAll().stream()
                .filter(job -> "ACTIVE".equalsIgnoreCase(job.getStatus().name()))
                .count();
    }

    private JobResponse mapToResponse(Job job) {
        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .departmentId((job.getDepartment() != null ? job.getDepartment().getId() : null))
                .hiringManagerId((job.getHiringManager() != null ? job.getHiringManager().getId() : null))
                .status(job.getStatus().name())
                .headcount(job.getHeadcount())
                // .createdAt(job.getCreatedAt())
                // .updatedAt(job.getLastModifiedDate())
                .build();
    }
}
