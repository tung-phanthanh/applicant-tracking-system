package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.response.JobResponse;
import fptu.sba301.ats.entity.Job;
import fptu.sba301.ats.repository.JobRepository;
import fptu.sba301.ats.service.JobService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.time.Instant;
import java.util.Optional;

@Service
public class JobServiceImpl implements JobService {
    private final JobRepository jobRepository;

    public JobServiceImpl(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public List<Job> findAll() {
        return jobRepository.findAll();
    }

    @Override
    public Optional<Job> findById(Long id) {
        return jobRepository.findById(id);
    }

    @Override
    public Job save(Job job) {
        if (job.getPostedDate() == null) {
            job.setPostedDate(Instant.now());
        }
        return jobRepository.save(job);
    }

    @Override
    public Job update(Long id, Job job) {
        return jobRepository.findById(id)
                .map(existing -> {
                    existing.setTitle(job.getTitle());
                    existing.setDepartment(job.getDepartment());
                    existing.setLocation(job.getLocation());
                    existing.setDescription(job.getDescription());
                    existing.setStatus(job.getStatus());
                    // postedDate should not change on update
                    return jobRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Job not found: " + id));
    }

    @Override
    public void delete(Long id) {
        jobRepository.deleteById(id);
    }

    @Override
    public List<JobResponse> getAllJobs() {
        return jobRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public JobResponse getJobById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        return mapToResponse(job);
    }

    @Override
    public long countActiveJobs() {
        return jobRepository.findAll().stream()
                .filter(job -> "ACTIVE".equalsIgnoreCase(job.getStatus()))
                .count();
    }

    private JobResponse mapToResponse(Job job) {
        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .departmentId(job.getDepartment())
                .status(job.getStatus())
                .createdAt(job.getCreatedAt())
                .build();
  }
}
