package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.response.DashboardStatsDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class DashboardServiceImplTest {

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    @Test
    void testGetDashboardStats_ReturnsCorrectCounts() {
        DashboardStatsDTO stats = dashboardService.getDashboardStats();

        assertNotNull(stats);
        assertEquals(15, stats.getActiveJobs());
        assertEquals(42, stats.getNewCandidates());
        assertEquals(5, stats.getInterviewsToday());
        assertEquals(3, stats.getOffersSent());
    }

    @Test
    void testGetDashboardStats_ContainsHiringPipeline() {
        DashboardStatsDTO stats = dashboardService.getDashboardStats();

        assertNotNull(stats.getHiringPipeline());
        assertEquals(5, stats.getHiringPipeline().size());
        assertEquals(150L, stats.getHiringPipeline().get("APPLIED"));
        assertEquals(45L, stats.getHiringPipeline().get("SCREENING"));
        assertEquals(15L, stats.getHiringPipeline().get("INTERVIEW"));
        assertEquals(5L, stats.getHiringPipeline().get("OFFER"));
        assertEquals(2L, stats.getHiringPipeline().get("HIRED"));
    }

    @Test
    void testGetDashboardStats_EmptyLists() {
        DashboardStatsDTO stats = dashboardService.getDashboardStats();

        assertNotNull(stats.getRecentApplications());
        assertTrue(stats.getRecentApplications().isEmpty());
        assertNotNull(stats.getTodaysInterviews());
        assertTrue(stats.getTodaysInterviews().isEmpty());
    }
}
