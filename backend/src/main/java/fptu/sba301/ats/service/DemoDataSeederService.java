package fptu.sba301.ats.service;

import fptu.sba301.ats.entity.*;
import fptu.sba301.ats.enums.*;
import fptu.sba301.ats.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
public class DemoDataSeederService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final JobRepository jobRepository;
    private final CandidateRepository candidateRepository;
    private final ApplicationRepository applicationRepository;
    private final ScorecardTemplateRepository scorecardTemplateRepository;
    private final ScorecardCriterionRepository scorecardCriterionRepository;
    private final InterviewRepository interviewRepository;
    private final InterviewParticipantRepository interviewParticipantRepository;
    private final OfferRepository offerRepository;
    private final OnboardingChecklistRepository onboardingChecklistRepository;
    private final OnboardingTaskRepository onboardingTaskRepository;

    @Transactional
    public String seedDemoData() {
        try {
            // 1. Get HR and Admin users
            User hr = userRepository.findByEmailAndDeletedFalse("hr@ats.com")
                    .orElseThrow(() -> new RuntimeException("HR user not found. Please run /api/v1/seed-admin first."));

            // 2. Department
            Department itDept = new Department();
            itDept.setName("Information Technology");
            itDept.setCreatedBy(hr.getId());
            itDept = departmentRepository.save(itDept);

            // 3. Job
            Job frontendJob = new Job();
            frontendJob.setTitle("Senior Frontend Developer");
            frontendJob.setDescription("Looking for an experienced React developer to join our team.");
            frontendJob.setDepartment(itDept);
            frontendJob.setHiringManager(hr);
            frontendJob.setStatus(JobStatus.APPROVED);
            frontendJob.setHeadcount(2);
            frontendJob.setCreatedBy(hr.getId());
            frontendJob = jobRepository.save(frontendJob);

            // 4. Candidate
            Candidate candidate = new Candidate();
            candidate.setFullName("Nguyen Van Test");
            candidate.setEmail("nguyenvantest@example.com");
            candidate.setPhone("+84987654321");
            candidate.setCurrentCompany("Tech Solutions Inc");
            candidate.setSource("LinkedIn");
            candidate.setLocation("Hanoi, Vietnam");
            candidate.setExperienceYears(5);
            candidate.setSummary("Passionate UI engineer.");
            candidate.setCreatedBy(hr.getId());
            candidate = candidateRepository.save(candidate);

            // 5. Application
            Application application = new Application();
            application.setCandidate(candidate);
            application.setJob(frontendJob);
            application.setStage(ApplicationStage.INTERVIEW);
            application.setStatus(ApplicationStatus.ACTIVE);
            application.setCreatedBy(hr.getId());
            application = applicationRepository.save(application);

            // 6. Scorecard Template & Criteria
            ScorecardTemplate template = new ScorecardTemplate();
            template.setName("Frontend Developer Evaluation");
            template.setCreatedBy(hr.getId());
            template.setDepartment(itDept);
            template = scorecardTemplateRepository.save(template);

            ScorecardCriterion c1 = new ScorecardCriterion();
            c1.setTemplate(template);
            c1.setName("React JS Knowledge");
            c1.setWeight(new BigDecimal("4.00"));
            c1.setCreatedBy(hr.getId());

            ScorecardCriterion c2 = new ScorecardCriterion();
            c2.setTemplate(template);
            c2.setName("Communication");
            c2.setWeight(new BigDecimal("3.00"));
            c2.setCreatedBy(hr.getId());
            
            scorecardCriterionRepository.saveAll(Arrays.asList(c1, c2));

            // 7. Interview
            Interview interview = new Interview();
            interview.setApplication(application);
            interview.setScheduledAt(Instant.now().plus(1, ChronoUnit.DAYS));
            interview.setLocation("Google Meet");
            interview.setMeetingLink("https://meet.google.com/abc-defg-hij");
            interview.setDurationMinutes(60);
            interview.setType(InterviewType.ONLINE);
            interview.setStatus(InterviewStatus.SCHEDULED);
            interview.setCreatedBy(hr.getId());
            interview = interviewRepository.save(interview);

            // 8. Interview Participant (Interviewer)
            InterviewParticipant participant = new InterviewParticipant();
            InterviewParticipant.InterviewParticipantId partId = new InterviewParticipant.InterviewParticipantId(interview.getId(), hr.getId());
            participant.setId(partId);
            participant.setInterview(interview);
            participant.setUser(hr);
            participant.setRole(ParticipantRole.INTERVIEWER);
            interviewParticipantRepository.save(participant);

            // 9. Offer Draft
            Offer offer = new Offer();
            offer.setApplication(application);
            offer.setSalary(new BigDecimal("3000.00"));
            offer.setPositionTitle("Senior Frontend Developer");
            offer.setStartDate(LocalDate.now().plusDays(30));
            offer.setStatus(OfferStatus.DRAFT);
            offer.setCreatedBy(hr.getId());
            offer = offerRepository.save(offer);

            // 10. Onboarding Checklist
            OnboardingChecklist checklist = new OnboardingChecklist();
            checklist.setApplication(application);
            checklist.setTitle("Frontend Onboarding");
            checklist.setStatus(OnboardingStatus.NOT_STARTED);
            checklist.setCreatedBy(hr.getId());
            checklist = onboardingChecklistRepository.save(checklist);

            // 11. Onboarding Tasks
            OnboardingTask t1 = new OnboardingTask();
            t1.setChecklist(checklist);
            t1.setTitle("Setup Email Account");
            t1.setAssignedTo(hr);
            t1.setDueDate(LocalDate.now().plusDays(5));
            t1.setCreatedBy(hr.getId());
            t1.setSortOrder(1);

            OnboardingTask t2 = new OnboardingTask();
            t2.setChecklist(checklist);
            t2.setTitle("Provide Laptop");
            t2.setAssignedTo(hr);
            t2.setDueDate(LocalDate.now().plusDays(7));
            t2.setCreatedBy(hr.getId());
            t2.setSortOrder(2);

            onboardingTaskRepository.saveAll(Arrays.asList(t1, t2));

            return "Demo data for Job, Candidate, Application, Scorecard, Interview, Offer, and Onboarding seeded successfully!";
        } catch (Exception e) {
            log.error("Error seeding demo data", e);
            throw new RuntimeException("Failed to seed demo data: " + e.getMessage());
        }
    }
}
