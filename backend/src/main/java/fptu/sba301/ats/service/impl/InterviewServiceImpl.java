package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.response.InterviewResponse;
import fptu.sba301.ats.entity.Application;
import fptu.sba301.ats.entity.Candidate;
import fptu.sba301.ats.entity.Interview;
import fptu.sba301.ats.entity.Job;
import fptu.sba301.ats.repository.ApplicationRepository;
import fptu.sba301.ats.repository.CandidateRepository;
import fptu.sba301.ats.repository.InterviewRepository;
import fptu.sba301.ats.repository.JobRepository;
import fptu.sba301.ats.service.InterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {

    private final InterviewRepository interviewRepository;
    private final ApplicationRepository applicationRepository;
    private final CandidateRepository candidateRepository;
    private final JobRepository jobRepository;

    @Override
    public List<InterviewResponse> getAllInterviews() {
        return interviewRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<InterviewResponse> getUpcomingInterviews() {
        return interviewRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public InterviewResponse getInterviewById(Long id) {
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Interview not found: " + id));
        return mapToResponse(interview);
    }

    private InterviewResponse mapToResponse(Interview interview) {
        String candidateName = "Unknown Candidate";
        String jobTitle = "Unknown Job";

        Application application = applicationRepository.findById(interview.getApplicationId()).orElse(null);
        if (application != null) {
            candidateName = candidateRepository.findById(application.getCandidateId())
                    .map(Candidate::getFullName)
                    .orElse("Unknown Candidate");
            jobTitle = jobRepository.findById(application.getJobId())
                    .map(Job::getTitle)
                    .orElse("Unknown Job");
        }

        return InterviewResponse.builder()
                .id(interview.getId())
                .applicationId(interview.getApplicationId())
                .scheduledAt(interview.getScheduledAt())
                .location(interview.getLocation())
                .status(interview.getStatus())
                .candidateName(candidateName)
                .jobTitle(jobTitle)
                .type(interview.getLocation() != null && !interview.getLocation().isEmpty() ? "On-site" : "Online")
                .participant("Hiring Team")
                .build();
    }
}
