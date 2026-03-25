package fptu.sba301.ats.config;

import fptu.sba301.ats.entity.*;
import fptu.sba301.ats.enums.ApprovalStatus;
import fptu.sba301.ats.enums.InterviewStatus;
import fptu.sba301.ats.enums.InterviewType;
import fptu.sba301.ats.enums.NotificationType;
import fptu.sba301.ats.enums.OfferStatus;
import fptu.sba301.ats.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final CandidateRepository candidateRepository;
    private final ApplicationRepository applicationRepository;
    private final ScorecardTemplateRepository scorecardTemplateRepository;
    private final ScorecardCriterionRepository scorecardCriterionRepository;
    private final InterviewRepository interviewRepository;
    private final InterviewParticipantRepository interviewParticipantRepository;
    private final InterviewScoreRepository interviewScoreRepository;
    private final CandidateDocumentRepository candidateDocumentRepository;
    private final CandidateNoteRepository candidateNoteRepository;
    private final CandidateStageHistoryRepository candidateStageHistoryRepository;
    private final OfferRepository offerRepository;
    private final JobApprovalRepository jobApprovalRepository;
    private final OfferApprovalRepository offerApprovalRepository;
    private final NotificationRepository notificationRepository;
    private final AuditLogRepository auditLogRepository;
    private final SystemConfigRepository systemConfigRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (departmentRepository.count() > 0) {
            return;
        }

        Department engineering = departmentRepository.save(Department.builder()
                .id(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                .name("Engineering")
                .description("Engineering department")
                .build());

        Department product = departmentRepository.save(Department.builder()
                .id(UUID.fromString("22222222-2222-2222-2222-222222222222"))
                .name("Product")
                .description("Product management")
                .build());

        Department hr = departmentRepository.save(Department.builder()
                .id(UUID.fromString("33333333-3333-3333-3333-333333333333"))
                .name("HR")
                .description("Human resources")
                .build());

        User admin = userRepository.findByEmailAndDeletedFalse("admin@example.com")
                .orElseThrow(() -> new IllegalStateException("Admin user must exist"));

        User hrManager = userRepository.findByEmailAndDeletedFalse("manager@example.com")
                .orElseThrow(() -> new IllegalStateException("Manager user must exist"));

        User hrUser = userRepository.findByEmailAndDeletedFalse("hr@example.com")
                .orElseThrow(() -> new IllegalStateException("HR user must exist"));

        Job backendJob = jobRepository.save(Job.builder()
                .id(UUID.fromString("10101010-1010-1010-1010-101010101010"))
                .title("Senior Backend Engineer")
                .description("Design backend services")
                .location("Ho Chi Minh")
                .salary("k-k")
                .department(engineering)
                .hiringManager(hrManager)
                .status(fptu.sba301.ats.enums.JobStatus.APPROVED)
                .headcount(2)
                .build());

        Job productOwnerJob = jobRepository.save(Job.builder()
                .id(UUID.fromString("12121212-1212-1212-1212-121212121212"))
                .title("Product Owner")
                .description("Own product roadmap")
                .location("Ho Chi Minh")
                .salary("k-k")
                .department(product)
                .hiringManager(hrManager)
                .status(fptu.sba301.ats.enums.JobStatus.APPROVED)
                .headcount(1)
                .build());

        Candidate alice = candidateRepository.save(Candidate.builder()
                .id(UUID.fromString("d1d1d1d1-d1d1-d1d1-d1d1-d1d1d1d1d1d1"))
                .fullName("Alice Nguyen")
                .email("alice@example.com")
                .phone("+84123456789")
                .currentCompany("Acme Corp")
                .source("LinkedIn")
                .location("HCMC")
                .experienceYears(5)
                .summary("Experienced backend engineer")
                .build());

        Candidate bob = candidateRepository.save(Candidate.builder()
                .id(UUID.fromString("d2d2d2d2-d2d2-d2d2-d2d2-d2d2d2d2d2d2"))
                .fullName("Bob Tran")
                .email("bob@example.com")
                .phone("+84987654321")
                .currentCompany("Beta Co")
                .source("Referral")
                .location("Hanoi")
                .experienceYears(3)
                .summary("Fullstack developer")
                .build());

        Application application1 = applicationRepository.save(Application.builder()
                .id(UUID.fromString("e1e1e1e1-e1e1-e1e1-e1e1-e1e1e1e1e1e1"))
                .candidate(alice)
                .job(backendJob)
                .stage(fptu.sba301.ats.enums.ApplicationStage.APPLIED)
                .status(fptu.sba301.ats.enums.ApplicationStatus.ACTIVE)
                .appliedAt(LocalDateTime.now())
                .build());

        Application application2 = applicationRepository.save(Application.builder()
                .id(UUID.fromString("e2e2e2e2-e2e2-e2e2-e2e2-e2e2e2e2e2e2"))
                .candidate(bob)
                .job(productOwnerJob)
                .stage(fptu.sba301.ats.enums.ApplicationStage.APPLIED)
                .status(fptu.sba301.ats.enums.ApplicationStatus.ACTIVE)
                .appliedAt(LocalDateTime.now())
                .build());

        ScorecardTemplate template = scorecardTemplateRepository.save(ScorecardTemplate.builder()
                .id(UUID.fromString("f1f1f1f1-f1f1-f1f1-f1f1-f1f1f1f1f1f1"))
                .name("Engineering Template")
                .department(engineering)
                .build());

        ScorecardCriterion criterion1 = scorecardCriterionRepository.save(ScorecardCriterion.builder()
                .id(UUID.fromString("g1g1g1g1-g1g1-g1g1-g1g1-g1g1g1g1g1g1"))
                .template(template)
                .name("Coding Skills")
                .weight(new BigDecimal("40"))
                .build());

        ScorecardCriterion criterion2 = scorecardCriterionRepository.save(ScorecardCriterion.builder()
                .id(UUID.fromString("g2g2g2g2-g2g2-g2g2-g2g2-g2g2g2g2g2g2"))
                .template(template)
                .name("Communication")
                .weight(new BigDecimal("30"))
                .build());

        ScorecardCriterion criterion3 = scorecardCriterionRepository.save(ScorecardCriterion.builder()
                .id(UUID.fromString("g3g3g3g3-g3g3-g3g3-g3g3-g3g3g3g3g3g3"))
                .template(template)
                .name("Problem Solving")
                .weight(new BigDecimal("30"))
                .build());

        Interview interview = interviewRepository.save(Interview.builder()
                .id(UUID.fromString("h1h1h1h1-h1h1-h1h1-h1h1-h1h1h1h1h1h1"))
                .application(application1)
                .template(template)
                .scheduledAt(Instant.now().plusSeconds(2 * 24 * 3600))
                .location("Video")
                .meetingLink("https://meet.example.com/123")
                .type(InterviewType.ONLINE)
                .status(InterviewStatus.SCHEDULED)
                .build());

        InterviewParticipant participant = InterviewParticipant.builder()
                .id(new InterviewParticipant.InterviewParticipantId(interview.getId(), hrManager.getId()))
                .interview(interview)
                .user(hrManager)
                .role(fptu.sba301.ats.enums.ParticipantRole.INTERVIEWER)
                .build();

        interviewParticipantRepository.save(participant);

        interviewScoreRepository.save(InterviewScore.builder()
                .id(UUID.fromString("i1i1i1i1-i1i1-i1i1-i1i1-i1i1i1i1i1i1"))
                .interview(interview)
                .interviewer(hrManager)
                .criterion(criterion1)
                .score(8)
                .comment("Strong code base")
                .build());

        candidateNoteRepository.save(CandidateNote.builder()
                .id(UUID.fromString("j1j1j1j1-j1j1-j1j1-j1j1-j1j1j1j1j1j1"))
                .application(application1)
                .content("Positive initial screen")
                .build());

        candidateDocumentRepository.save(CandidateDocument.builder()
                .id(UUID.fromString("k1k1k1k1-k1k1-k1k1-k1k1-k1k1k1k1k1k1"))
                .candidate(alice)
                .fileName("CV.pdf")
                .fileUrl("https://cdn.example.com/cv/alice.pdf")
                .fileType("application/pdf")
                .fileSizeBytes(34567L)
                .uploadedAt(LocalDateTime.now())
                .build());

        candidateStageHistoryRepository.save(CandidateStageHistory.builder()
                .id(UUID.fromString("l1l1l1l1-l1l1-l1l1-l1l1-l1l1l1l1l1l1"))
                .application(application1)
                .fromStage(fptu.sba301.ats.enums.ApplicationStage.APPLIED)
                .toStage(fptu.sba301.ats.enums.ApplicationStage.SCREENING)
                .build());

        Offer offer = offerRepository.save(Offer.builder()
                .id(UUID.fromString("m1m1m1m1-m1m1-m1m1-m1m1-m1m1m1m1m1m1"))
                .application(application1)
                .salary(new BigDecimal("120000"))
                .positionTitle("Senior Backend Engineer")
                .status(OfferStatus.DRAFT)
                .build());

        jobApprovalRepository.save(JobApproval.builder()
                .id(UUID.fromString("n1n1n1n1-n1n1-n1n1-n1n1-n1n1n1n1n1n1"))
                .job(backendJob)
                .approvedBy(hrManager)
                .status(ApprovalStatus.APPROVED)
                .comment("Approved for posting")
                .build());

        offerApprovalRepository.save(OfferApproval.builder()
                .id(UUID.fromString("o1o1o1o1-o1o1-o1o1-o1o1-o1o1o1o1o1o1"))
                .offer(offer)
                .approvedBy(hrManager)
                .status(ApprovalStatus.APPROVED)
                .comment("Salary confirmed and approved")
                .build());

        notificationRepository.save(Notification.builder()
                .userId(hrUser.getId())
                .type(NotificationType.SYSTEM_ALERT)
                .title("Welcome")
                .message("Welcome to the Applicant Tracking System")
                .isRead(false)
                .createdAt(Instant.now())
                .build());

        auditLogRepository.save(AuditLog.builder()
                .userId(hrUser.getId())
                .action("CREATE")
                .entityType("candidate")
                .entityId(alice.getId().toString())
                .newValue("{\"full_name\":\"Alice Nguyen\"}")
                .ipAddress("127.0.0.1")
                .userAgent("PostmanRuntime/7.0")
                .build());

        systemConfigRepository.save(SystemConfig.builder()
                .key("app.name")
                .value("Enterprise ATS")
                .updatedBy(admin.getId())
                .updatedAt(Instant.now())
                .build());

        systemConfigRepository.save(SystemConfig.builder()
                .key("email.from")
                .value("no-reply@example.com")
                .updatedBy(admin.getId())
                .updatedAt(Instant.now())
                .build());
    }
}
