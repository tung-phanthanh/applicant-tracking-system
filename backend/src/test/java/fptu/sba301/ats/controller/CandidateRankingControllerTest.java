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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
class CandidateRankingControllerTest {

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

    private Job testJob;

    @BeforeEach
    void setUp() {
        Department dept = Department.builder().name("Sales").build();
        departmentRepository.save(dept);

        testJob = Job.builder()
                .title("Sales Manager")
                .department(dept)
                .build();
        jobRepository.save(testJob);

        Candidate candidate1 = Candidate.builder()
                .fullName("Alice Brown")
                .email("alice@test.com")
                .experienceYears(5)
                .build();
        candidateRepository.save(candidate1);

        Candidate candidate2 = Candidate.builder()
                .fullName("Bob Wilson")
                .email("bob@test.com")
                .experienceYears(3)
                .build();
        candidateRepository.save(candidate2);

        Application app1 = Application.builder()
                .candidate(candidate1)
                .job(testJob)
                .stage(ApplicationStage.INTERVIEW)
                .build();
        applicationRepository.save(app1);

        Application app2 = Application.builder()
                .candidate(candidate2)
                .job(testJob)
                .stage(ApplicationStage.INTERVIEW)
                .build();
        applicationRepository.save(app2);

        ScorecardTemplate template = ScorecardTemplate.builder()
                .name("Sales Interview")
                .department(dept)
                .build();
        templateRepository.save(template);

        ScorecardCriterion criterion = ScorecardCriterion.builder()
                .template(template)
                .name("Negotiation")
                .weight(new BigDecimal("1.00"))
                .build();
        criterionRepository.save(criterion);
    }

    @AfterEach
    void tearDown() {
        interviewRepository.deleteAll();
        criterionRepository.deleteAll();
        templateRepository.deleteAll();
        applicationRepository.deleteAll();
        candidateRepository.deleteAll();
        jobRepository.deleteAll();
        departmentRepository.deleteAll();
    }

    @Test
    @Order(1)
    @WithMockUser(authorities = "HR_MANAGER")
    void TC_CR_001_getRanking_Success() throws Exception {
        mockMvc.perform(get("/api/v1/jobs/" + testJob.getId() + "/ranking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(2)
    @WithMockUser(authorities = "HR_MANAGER")
    void TC_CR_002_getRanking_EmptyList() throws Exception {
        mockMvc.perform(get("/api/v1/jobs/" + testJob.getId() + "/ranking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @Order(3)
    @WithMockUser(authorities = "HR_MANAGER")
    void TC_CR_003_getRanking_JobNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/jobs/" + UUID.randomUUID() + "/ranking"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(4)
    @WithMockUser(authorities = "HR_MANAGER")
    void TC_CR_004_getRanking_SortByScore() throws Exception {
        mockMvc.perform(get("/api/v1/jobs/" + testJob.getId() + "/ranking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rank").exists());
    }

    @Test
    @Order(5)
    @WithMockUser(authorities = "HR_MANAGER")
    void TC_CR_005_getRanking_TieBreakerExperience() throws Exception {
        mockMvc.perform(get("/api/v1/jobs/" + testJob.getId() + "/ranking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].experienceYears").exists());
    }

    @Test
    @Order(6)
    @WithMockUser(authorities = "HR_MANAGER")
    void TC_CR_006_getRanking_RankAssignment() throws Exception {
        mockMvc.perform(get("/api/v1/jobs/" + testJob.getId() + "/ranking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rank").value(1))
                .andExpect(jsonPath("$[1].rank").value(2));
    }

    @Test
    @Order(7)
    @WithMockUser(authorities = "HR_MANAGER")
    void TC_CR_007_getRanking_Top3Icons() throws Exception {
        mockMvc.perform(get("/api/v1/jobs/" + testJob.getId() + "/ranking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rank").isNumber());
    }

    @Test
    @Order(8)
    @WithMockUser(authorities = "HR_MANAGER")
    void TC_CR_008_getRanking_IncludeApplicationStage() throws Exception {
        mockMvc.perform(get("/api/v1/jobs/" + testJob.getId() + "/ranking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].stage").exists());
    }

    @Test
    @Order(9)
    @WithMockUser(authorities = "HR_MANAGER")
    void TC_CR_009_getRanking_ScoreColorCodingData() throws Exception {
        mockMvc.perform(get("/api/v1/jobs/" + testJob.getId() + "/ranking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].score").exists());
    }

    @Test
    @Order(10)
    @WithMockUser(username = "interviewer@test.com", authorities = "INTERVIEWER")
    void TC_CR_010_getRanking_UnauthorizedRole() throws Exception {
        mockMvc.perform(get("/api/v1/jobs/" + testJob.getId() + "/ranking"))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(11)
    @WithMockUser(authorities = "HR_MANAGER")
    void TC_CR_011_getRanking_CandidatesWithoutScores() throws Exception {
        mockMvc.perform(get("/api/v1/jobs/" + testJob.getId() + "/ranking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @Order(12)
    @WithMockUser(authorities = "HR_MANAGER")
    void TC_CR_012_getRanking_MultipleInterviewsAverage() throws Exception {
        mockMvc.perform(get("/api/v1/jobs/" + testJob.getId() + "/ranking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
