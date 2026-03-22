package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.response.DashboardStatsDTO;
import fptu.sba301.ats.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import fptu.sba301.ats.repository.JobRepository;
import fptu.sba301.ats.repository.CandidateRepository;
import fptu.sba301.ats.repository.InterviewRepository;
import fptu.sba301.ats.repository.OfferRepository;
import fptu.sba301.ats.repository.ApplicationRepository;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
 
    private final JobRepository jobRepository;
    private final CandidateRepository candidateRepository;
    private final InterviewRepository interviewRepository;
    private final OfferRepository offerRepository;
    private final ApplicationRepository applicationRepository;
 
    @Override
    public DashboardStatsDTO getDashboardStats() {
        long activeJobs = jobRepository.countByStatus(fptu.sba301.ats.enums.JobStatus.APPROVED);
        long newCandidates = candidateRepository.count();
        
        java.time.Instant startOfDay = java.time.LocalDate.now().atStartOfDay(java.time.ZoneId.of("Asia/Ho_Chi_Minh")).toInstant();
        java.time.Instant endOfDay = java.time.LocalDate.now().plusDays(1).atStartOfDay(java.time.ZoneId.of("Asia/Ho_Chi_Minh")).toInstant();
        long interviewsToday = interviewRepository.countByScheduledAtBetween(startOfDay, endOfDay);

        long offersSent = offerRepository.countByStatus(fptu.sba301.ats.enums.OfferStatus.PENDING_APPROVAL);

        Map<String, Long> pipeline = new java.util.HashMap<>();
        for (fptu.sba301.ats.enums.ApplicationStage stage : fptu.sba301.ats.enums.ApplicationStage.values()) {
            pipeline.put(stage.name(), applicationRepository.countByStage(stage));
        }

        return DashboardStatsDTO.builder()
                .activeJobs((int) activeJobs)
                .newCandidates((int) newCandidates)
                .interviewsToday((int) interviewsToday)
                .offersSent((int) offersSent)
                .hiringPipeline(pipeline)
                .recentApplications(Collections.emptyList())
                .todaysInterviews(Collections.emptyList())
                .build();
    }
}
