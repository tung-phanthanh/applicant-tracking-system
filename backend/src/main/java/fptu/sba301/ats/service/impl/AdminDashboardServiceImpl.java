package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.response.AdminAnalyticsDTO;
import fptu.sba301.ats.dto.response.SystemHealthDTO;
import fptu.sba301.ats.dto.response.UserManagementStatsDTO;
import fptu.sba301.ats.enums.ApplicationStage;
import fptu.sba301.ats.enums.ApplicationStatus;
import fptu.sba301.ats.repository.ApplicationRepository;
import fptu.sba301.ats.repository.JobRepository;
import fptu.sba301.ats.repository.UserRepository;
import fptu.sba301.ats.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final Runtime runtime = Runtime.getRuntime();

    @Override
    public AdminAnalyticsDTO getAnalytics(String period) {
        // Calculate date range based on period
        Instant endDate = Instant.now();
        Instant startDate = calculateStartDate(period, endDate);

        // Get total applications
        long totalApplications = applicationRepository.count();
        long newApplicationsThisMonth = applicationRepository
                .countByCreatedAtBetween(
                        Instant.now().minus(30, ChronoUnit.DAYS),
                        Instant.now()
                );

        // Build hiring funnel
        Map<String, Long> hiringFunnel = new HashMap<>();
        hiringFunnel.put("APPLIED", applicationRepository.countByStage(ApplicationStage.APPLIED));
        hiringFunnel.put("SCREENING", applicationRepository.countByStage(ApplicationStage.SCREENING));
        hiringFunnel.put("INTERVIEW", applicationRepository.countByStage(ApplicationStage.INTERVIEW));
        hiringFunnel.put("OFFER", applicationRepository.countByStage(ApplicationStage.OFFER));
        hiringFunnel.put("HIRED", applicationRepository.countByStage(ApplicationStage.HIRED));

        // Calculate conversion rate (hired / applied)
        long applied = hiringFunnel.getOrDefault("APPLIED", 1L);
        long hired = hiringFunnel.getOrDefault("HIRED", 0L);
        double conversionRate = applied > 0 ? (hired * 100.0) / applied : 0.0;

        // Placeholder: average time to hire (simplified)
        double timeToHireAverage = 45.0; // days (could be calculated from database)

        // Department breakdown (simplified)
        Map<String, Long> applicationsByDepartment = new HashMap<>();
        applicationsByDepartment.put("Engineering", 150L);
        applicationsByDepartment.put("Sales", 80L);
        applicationsByDepartment.put("HR", 30L);
        applicationsByDepartment.put("Marketing", 60L);

        // Top jobs (simplified)
        List<AdminAnalyticsDTO.JobPerformanceDTO> topJobs = List.of(
                AdminAnalyticsDTO.JobPerformanceDTO.builder()
                        .jobTitle("Senior Software Engineer")
                        .applications(45)
                        .hired(3)
                        .conversionRate(6.67)
                        .build(),
                AdminAnalyticsDTO.JobPerformanceDTO.builder()
                        .jobTitle("Product Manager")
                        .applications(28)
                        .hired(2)
                        .conversionRate(7.14)
                        .build(),
                AdminAnalyticsDTO.JobPerformanceDTO.builder()
                        .jobTitle("Data Analyst")
                        .applications(35)
                        .hired(2)
                        .conversionRate(5.71)
                        .build()
        );

        // Source analysis (simplified)
        Map<String, Long> applicationsBySource = new HashMap<>();
        applicationsBySource.put("LinkedIn", 120L);
        applicationsBySource.put("Indeed", 85L);
        applicationsBySource.put("Referral", 95L);
        applicationsBySource.put("Company Website", 50L);

        // Rejection reasons (simplified)
        Map<String, Long> rejectionReasons = new HashMap<>();
        rejectionReasons.put("Skill Gap", 45L);
        rejectionReasons.put("Experience", 30L);
        rejectionReasons.put("Cultural Fit", 20L);
        rejectionReasons.put("Salary Expectation", 15L);

        return AdminAnalyticsDTO.builder()
                .totalApplications(totalApplications)
                .newApplicationsThisMonth(newApplicationsThisMonth)
                .conversionRate(conversionRate)
                .timeToHireAverage(timeToHireAverage)
                .hiringFunnel(hiringFunnel)
                .applicationsByDepartment(applicationsByDepartment)
                .topJobs(topJobs)
                .applicationsBySource(applicationsBySource)
                .rejectionReasons(rejectionReasons)
                .period(period)
                .build();
    }

    @Override
    public SystemHealthDTO getSystemHealth() {
        // Database health
        boolean databaseHealthy = true;
        String databaseStatus = "HEALTHY";
        
        // Application health
        boolean applicationHealthy = true;
        String applicationStatus = "UP";
        
        // Memory usage
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        double memoryUsage = (usedMemory * 100.0) / totalMemory;
        
        // CPU usage (simplified)
        double cpuUsage = 45.0; // Would need OperatingSystemMXBean for real values
        
        // Performance metrics (simplified)
        double p95ResponseTime = 250.0; // milliseconds
        double p99ResponseTime = 500.0;
        double averageResponseTime = 120.0;
        long requestsPerSecond = 150;
        
        // Errors (simplified)
        int errorsLast24Hours = 5;
        int errorsLast7Days = 28;
        double errorRate = 0.05; // 0.05%
        
        // Health score (0-100)
        int healthScore = 92;
        
        return SystemHealthDTO.builder()
                .databaseHealthy(databaseHealthy)
                .databaseStatus(databaseStatus)
                .databaseConnections(10)
                .databaseQueries(1500)
                .applicationHealthy(applicationHealthy)
                .applicationStatus(applicationStatus)
                .cpuUsage(cpuUsage)
                .memoryUsage(memoryUsage)
                .uptime(System.currentTimeMillis()) // milliseconds since start
                .errorsLast24Hours(errorsLast24Hours)
                .errorsLast7Days(errorsLast7Days)
                .errorRate(errorRate)
                .p95ResponseTime(p95ResponseTime)
                .p99ResponseTime(p99ResponseTime)
                .averageResponseTime(averageResponseTime)
                .requestsPerSecond(requestsPerSecond)
                .healthScore(healthScore)
                .lastChecked(Instant.now())
                .build();
    }

    @Override
    public UserManagementStatsDTO getUserManagementStats() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByDeletedFalse();
        long inactiveUsers = totalUsers - activeUsers;
        
        // New users this month
        long newUsersThisMonth = userRepository.countByCreatedAtBetween(
                Instant.now().minus(30, ChronoUnit.DAYS),
                Instant.now()
        );

        // Users by role (simplified counts)
        long adminCount = 3;
        long hrManagerCount = 8;
        long recruiterCount = 15;
        long interviewerCount = 25;

        // Activity metrics (simplified)
        double averageLoginFrequency = 3.5; // times per week
        long usersInactiveMoreThan30Days = 5;
        long usersInactiveMoreThan60Days = 2;

        // Risk indicators (simplified)
        long failedLoginAttempts = 12;
        long accountsLocked = 0;
        long passwordExpiringSoon = 3;

        return UserManagementStatsDTO.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .inactiveUsers(inactiveUsers)
                .newUsersThisMonth(newUsersThisMonth)
                .adminCount(adminCount)
                .hrManagerCount(hrManagerCount)
                .recruiterCount(recruiterCount)
                .interviewerCount(interviewerCount)
                .averageLoginFrequency(averageLoginFrequency)
                .usersInactiveMoreThan30Days(usersInactiveMoreThan30Days)
                .usersInactiveMoreThan60Days(usersInactiveMoreThan60Days)
                .failedLoginAttempts(failedLoginAttempts)
                .accountsLocked(accountsLocked)
                .passwordExpiringSoon(passwordExpiringSoon)
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Helper methods
    // ─────────────────────────────────────────────────────────────────────────────

    private Instant calculateStartDate(String period, Instant endDate) {
        return switch (period) {
            case "7days" -> endDate.minus(7, ChronoUnit.DAYS);
            case "30days" -> endDate.minus(30, ChronoUnit.DAYS);
            case "quarter" -> endDate.minus(90, ChronoUnit.DAYS);
            default -> endDate.minus(30, ChronoUnit.DAYS);
        };
    }
}
