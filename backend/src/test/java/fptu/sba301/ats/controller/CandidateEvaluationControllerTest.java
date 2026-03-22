package fptu.sba301.ats.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fptu.sba301.ats.entity.*;
import fptu.sba301.ats.enums.ApplicationStage;
import fptu.sba301.ats.enums.InterviewStatus;
import fptu.sba301.ats.enums.InterviewType;
import fptu.sba301.ats.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
class CandidateEvaluationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    private ScorecardTemplateRepository templateRepository;

    @Autowired
    private ScorecardCriterionRepository criterionRepository;

    @Autowired
    private InterviewScoreRepository interviewScoreRepository;

    @Autowired
    private UserRepository userRepository;

    private Application testApplication;
    private Interview testInterview;

    @BeforeEach
    void setUp() {
        Department dept = Department.builder().name("Engineering").build();
        departmentRepository.save(dept);

        Job job = Job.builder()
                .title("Senior Developer")
                .department(dept)
                .build();
        jobRepository.save(job);

        Candidate candidate = Candidate.builder()
                .fullName("Jane Smith")
                .email("jane@test.com")
                .build();
        candidateRepository.save(candidate);

        testApplication = Application.builder()
                .candidate(candidate)
                .job(job)
                .stage(ApplicationStage.INTERVIEW)
                .build();
        applicationRepository.save(testApplication);

        ScorecardTemplate template = ScorecardTemplate.builder()
                .name("Technical Screening")
                .department(dept)
                .build();
        templateRepository.save(template);

        ScorecardCriterion criterion = ScorecardCriterion.builder()
                .template(template)
                .name("Communication")
                .weight(new BigDecimal("1.00"))
                .build();
        criterionRepository.save(criterion);

        testInterview = Interview.builder()
                .application(testApplication)
                .scheduledAt(Instant.now())
                .status(InterviewStatus.COMPLETED)
                .type(InterviewType.TECHNICAL)
                .build();
        interviewRepository.save(testInterview);
    }

    @AfterEach
    void tearDown() {
        interviewScoreRepository.deleteAll();
        interviewRepository.deleteAll();
        criterionRepository.deleteAll();
        templateRepository.deleteAll();
        applicationRepository.deleteAll();
        candidateRepository.deleteAll();
        jobRepository.deleteAll();
        userRepository.deleteAll();
        departmentRepository.deleteAll();
    }

    @Test
    @Order(1)
    @WithMockUser(authorities = "HR_MANAGER")
    void TC_CE_001_getEvaluationSummary_Success() throws Exception {
        mockMvc.perform(get("/api/v1/applications/" + testApplication.getId() + "/evaluation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.applicationId").value(testApplication.getId().toString()))
                .andExpect(jsonPath("$.candidateName").value("Jane Smith"))
                .andExpect(jsonPath("$.jobTitle").value("Senior Developer"));
    }

    @Test
    @Order(2)
    @WithMockUser(authorities = "HR_MANAGER")
    void TC_CE_002_getEvaluationSummary_NoInterviews() throws Exception {
        mockMvc.perform(get("/api/v1/applications/" + testApplication.getId() + "/evaluation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interviews", hasSize(0)));
    }

    @Test
    @Order(3)
    @WithMockUser(authorities = "HR_MANAGER")
    void TC_CE_003_getEvaluationSummary_ApplicationNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/applications/" + UUID.randomUUID() + "/evaluation"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(4)
    @WithMockUser(authorities = "HR_MANAGER")
    void TC_CE_004_getEvaluationSummary_CalculateOverallScore() throws Exception {
        mockMvc.perform(get("/api/v1/applications/" + testApplication.getId() + "/evaluation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.overallScore").exists());
    }

    @Test
    @Order(5)
    @WithMockUser(authorities = "HR_MANAGER")
    void TC_CE_005_getEvaluationSummary_WithScoreDetails() throws Exception {
        mockMvc.perform(get("/api/v1/applications/" + testApplication.getId() + "/evaluation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interviews").isArray());
    }

    @Test
    @Order(6)
    @WithMockUser(authorities = "INTERVIEWER")
    void TC_CE_006_getEvaluationSummary_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/applications/" + testApplication.getId() + "/evaluation"))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(7)
    @WithMockUser(authorities = "HR_MANAGER")
    void TC_CE_007_getEvaluationSummary_PartialInterviewsScored() throws Exception {
        mockMvc.perform(get("/api/v1/applications/" + testApplication.getId() + "/evaluation"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(8)
    @WithMockUser(authorities = "HR_MANAGER")
    void TC_CE_008_getEvaluationSummary_CandidateInfo() throws Exception {
        mockMvc.perform(get("/api/v1/applications/" + testApplication.getId() + "/evaluation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.candidateName").value("Jane Smith"))
                .andExpect(jsonPath("$.jobTitle").value("Senior Developer"));
    }
}
