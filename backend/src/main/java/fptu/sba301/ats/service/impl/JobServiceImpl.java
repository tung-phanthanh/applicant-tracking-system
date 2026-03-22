package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.response.JobResponse;
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
        return jobRepository.findAll()
                .stream()
                .map(job -> JobResponse.builder()
                        .jobId(job.getId())
                        .title(job.getTitle())
                        .status(job.getStatus())
                        .build())
                .collect(Collectors.toList());
    }
}
