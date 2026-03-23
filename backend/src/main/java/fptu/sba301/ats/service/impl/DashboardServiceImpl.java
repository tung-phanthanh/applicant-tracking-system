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

    @Override
    public DashboardStatsDTO getDashboardStats() {
        // Returned hardcoded data as in original version to avoid non-admin repo dependencies
        Map<String, Long> pipeline = new java.util.HashMap<>();
        pipeline.put("APPLIED", 150L);
        pipeline.put("SCREENING", 45L);
        pipeline.put("INTERVIEW", 15L);
        pipeline.put("OFFER", 5L);
        pipeline.put("HIRED", 2L);

        return DashboardStatsDTO.builder()
                .activeJobs(15)
                .newCandidates(42)
                .interviewsToday(5)
                .offersSent(3)
                .hiringPipeline(pipeline)
                .recentApplications(Collections.emptyList())
                .todaysInterviews(Collections.emptyList())
                .build();
    }
}
