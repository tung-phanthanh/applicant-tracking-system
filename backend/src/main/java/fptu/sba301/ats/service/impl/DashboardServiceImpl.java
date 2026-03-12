package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.response.DashboardStatsDTO;
import fptu.sba301.ats.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    // Repositories (JobRepository, ApplicationRepository, InterviewRepository,
    // OfferRepository)
    // would be injected here. For the scope of this implementation, we will mock
    // the return
    // to prove the structural implementation since those domain modules are not
    // fully fleshed out
    // in the backend code provided yet.

    @Override
    public DashboardStatsDTO getDashboardStats() {
        return DashboardStatsDTO.builder()
                .activeJobs(15) // Example mocked count
                .newCandidates(42)
                .interviewsToday(5)
                .offersSent(3)
                .hiringPipeline(Map.of(
                        "APPLIED", 150L,
                        "SCREENING", 45L,
                        "INTERVIEW", 15L,
                        "OFFER", 5L,
                        "HIRED", 2L))
                .recentApplications(Collections.emptyList())
                .todaysInterviews(Collections.emptyList())
                .build();
    }
}
