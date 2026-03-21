package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.response.ApplicationResponse;
import fptu.sba301.ats.entity.Application;
import fptu.sba301.ats.entity.Candidate;
import fptu.sba301.ats.entity.Job;
import fptu.sba301.ats.repository.ApplicationRepository;
import fptu.sba301.ats.repository.CandidateRepository;
import fptu.sba301.ats.repository.JobRepository;
import fptu.sba301.ats.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final CandidateRepository candidateRepository;
    private final JobRepository jobRepository;

    @Override
    public List<ApplicationResponse> getAllApplications() {
        return applicationRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationResponse> getRecentApplications(int limit) {
        return applicationRepository.findAll(PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "appliedAt")))
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ApplicationResponse getApplicationById(java.util.UUID id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        return mapToResponse(application);
    }

    private ApplicationResponse mapToResponse(Application application) {
        String candidateName = candidateRepository.findById(application.getCandidate().getId())
                .map(Candidate::getFullName)
                .orElse("Unknown Candidate");

        String jobTitle = jobRepository.findById(application.getJob().getId())
                .map(Job::getTitle)
                .orElse("Unknown Job");

        return ApplicationResponse.builder()
                .id(application.getId())
                .candidateId(application.getCandidate().getId())
                .jobId(application.getJob().getId())
                .stage(application.getStage())
                .status(application.getStatus())
                .appliedAt(application.getAppliedAt())
                .updatedAt(application.getLastModifiedDate())
                .candidateName(candidateName)
                .jobTitle(jobTitle)
                .build();
    }
}
