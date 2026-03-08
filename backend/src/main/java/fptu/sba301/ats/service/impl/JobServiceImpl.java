package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.response.JobResponse;
import fptu.sba301.ats.entity.Job;
import fptu.sba301.ats.repository.JobRepository;
import fptu.sba301.ats.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;

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
                .departmentId(job.getDepartmentId())
                .hiringManagerId(job.getHiringManagerId())
                .status(job.getStatus())
                .headcount(job.getHeadcount())
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }
}
