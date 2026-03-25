package fptu.sba301.ats.service;

import fptu.sba301.ats.entity.*;
import fptu.sba301.ats.enums.*;
import fptu.sba301.ats.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DemoDataSeederService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final JobRepository jobRepository;
    private final CandidateRepository candidateRepository;
    private final ApplicationRepository applicationRepository;
    private final SystemConfigRepository systemConfigRepository;

    @Transactional
    public String seedDemoData() {
        try {
            // Seed system configs if empty
            if (systemConfigRepository.count() == 0) {
                SystemConfig config = new SystemConfig();
                config.setKey("NOTIFICATIONS_ENABLED");
                config.setValue("true");
                config.setUpdatedAt(java.time.Instant.now());
                systemConfigRepository.save(config);
                
                SystemConfig sessionConfig = new SystemConfig();
                sessionConfig.setKey("SESSION_TIMEOUT_MINUTES");
                sessionConfig.setValue("30");
                sessionConfig.setUpdatedAt(java.time.Instant.now());
                systemConfigRepository.save(sessionConfig);
            }
            // Check if data already exists to avoid duplication
            if (jobRepository.count() > 0) {
                return "Demo data already exists.";
            }

            // 1. Get HR user (assumed to be seeded by admin seeder)
            User hr = userRepository.findByEmailAndDeletedFalse("hr@ats.com")
                    .orElseThrow(() -> new RuntimeException("HR user not found. Please run admin seeding first."));

            // 2. Department
            Department itDept = Department.builder()
                .id(java.util.UUID.randomUUID())
                .name("Information Technology")
                .description("IT and Software Engineering")
                .build();
            itDept = departmentRepository.save(itDept);

            // 3. Jobs
            Job frontendJob = Job.builder()
                .id(java.util.UUID.randomUUID())
                .title("Senior Frontend Developer")
                .description("Looking for experienced React engineer.")
                .department(itDept)
                .hiringManager(hr)
                .status(JobStatus.APPROVED)
                .headcount(2)
                .build();
            jobRepository.save(frontendJob);

            Job backendJob = Job.builder()
                .id(java.util.UUID.randomUUID())
                .title("Java Backend Developer")
                .description("Spring Boot expert needed.")
                .department(itDept)
                .hiringManager(hr)
                .status(JobStatus.APPROVED)
                .headcount(1)
                .build();
            jobRepository.save(backendJob);

            // 4. Candidate
            Candidate candidate = Candidate.builder()
                .id(java.util.UUID.randomUUID())
                .fullName("Nguyen Van Test")
                .email("test@example.com")
                .source("LinkedIn")
                .experienceYears(5)
                .build();
            candidate = candidateRepository.save(candidate);

            // 5. Application
            Application app = Application.builder()
                .id(java.util.UUID.randomUUID())
                .candidate(candidate)
                .job(frontendJob)
                .stage(ApplicationStage.APPLIED)
                .status(ApplicationStatus.ACTIVE)
                .build();
            applicationRepository.save(app);

            return "Prioritized demo data seeded successfully!";
        } catch (Exception e) {
            log.error("Error seeding demo data", e);
            throw new RuntimeException("Failed to seed: " + e.getMessage());
        }
    }
}
