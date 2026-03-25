package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.response.DashboardStatsDTO;
import fptu.sba301.ats.enums.ApplicationStage;
import fptu.sba301.ats.enums.ApplicationStatus;
import fptu.sba301.ats.enums.JobStatus;
import fptu.sba301.ats.repository.ApplicationRepository;
import fptu.sba301.ats.repository.CandidateRepository;
import fptu.sba301.ats.repository.JobRepository;
import fptu.sba301.ats.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final CandidateRepository candidateRepository;

    @Override
    public DashboardStatsDTO getDashboardStats() {
        Map<String, Long> pipeline = new HashMap<>();
        pipeline.put("APPLIED", applicationRepository.countByStage(ApplicationStage.APPLIED));
        pipeline.put("SCREENING", applicationRepository.countByStage(ApplicationStage.SCREENING));
        pipeline.put("INTERVIEW", applicationRepository.countByStage(ApplicationStage.INTERVIEW));
        pipeline.put("OFFER", applicationRepository.countByStage(ApplicationStage.OFFER));
        pipeline.put("HIRED", applicationRepository.countByStage(ApplicationStage.HIRED));

        return DashboardStatsDTO.builder()
                .activeJobs((int) jobRepository.countByStatus(JobStatus.APPROVED))
                .newCandidates((int) candidateRepository.count())
                .interviewsToday(0) // Logic for interviews today not yet implemented in repository
                .offersSent((int) applicationRepository.countByStage(ApplicationStage.OFFER))
                .hiringPipeline(pipeline)
                .recentApplications(Collections.emptyList())
                .todaysInterviews(Collections.emptyList())
                .build();
    }
}
