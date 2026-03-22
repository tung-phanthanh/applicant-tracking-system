package fptu.sba301.ats.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fptu.sba301.ats.dto.request.SubmitInterviewScoreRequest;
import fptu.sba301.ats.entity.*;
import fptu.sba301.ats.enums.ApplicationStage;
import fptu.sba301.ats.enums.InterviewStatus;
import fptu.sba301.ats.enums.InterviewType;
import fptu.sba301.ats.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
class InterviewScorecardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    private InterviewScoreRepository interviewScoreRepository;

    @Autowired
    private ScorecardTemplateRepository templateRepository;

    @Autowired
    private ScorecardCriterionRepository criterionRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserRepository userRepository;

    private Interview testInterview;
    private ScorecardCriterion testCriterion;
    private User testInterviewer;

    @BeforeEach
    void setUp() {
        Department dept = Department.builder().name("IT").build();
        departmentRepository.save(dept);

        User hrManager = User.builder()
                .email("hr@test.com")
                .fullName("HR Manager")
                .role(fptu.sba301.ats.enums.Role.HR_MANAGER)
                .build();
        userRepository.save(hrManager);

        testInterviewer = User.builder()
                .email("interviewer@test.com")
                .fullName("Interviewer")
                .role(fptu.sba301.ats.enums.Role.INTERVIEWER)
                .build();
        userRepository.save(testInterviewer);

        Job job = Job.builder()
                .title("Software Engineer")
                .department(dept)
                .build();
        jobRepository.save(job);

        Candidate candidate = Candidate.builder()
                .fullName("John Doe")
                .email("john@test.com")
                .build();
        candidateRepository.save(candidate);

        Application application = Application.builder()
                .candidate(candidate)
                .job(job)
                .stage(ApplicationStage.INTERVIEW)
                .build();
        applicationRepository.save(application);

        ScorecardTemplate template = ScorecardTemplate.builder()
                .name("Technical Interview")
                .department(dept)
                .build();
        templateRepository.save(template);

        testCriterion = ScorecardCriterion.builder()
                .template(template)
                .name("Problem Solving")
                .weight(new BigDecimal("2.00"))
                .build();
        criterionRepository.save(testCriterion);

        testInterview = Interview.builder()
                .application(application)
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
    @WithMockUser(username = "interviewer@test.com", authorities = "INTERVIEWER")
    void TC_IS_001_submitScores_Success() throws Exception {
        SubmitInterviewScoreRequest request = SubmitInterviewScoreRequest.builder()
                .scores(Arrays.asList(
                        SubmitInterviewScoreRequest.ScoreEntry.builder()
                                .criterionId(testCriterion.getId())
                                .score(8)
                                .comment("Good problem solving skills")
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/v1/interviews/" + testInterview.getId() + "/scores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @Order(2)
    @WithMockUser(username = "interviewer@test.com", authorities = "INTERVIEWER")
    void TC_IS_002_submitScores_InvalidScoreRange_BelowOne() throws Exception {
        SubmitInterviewScoreRequest request = SubmitInterviewScoreRequest.builder()
                .scores(Arrays.asList(
                        SubmitInterviewScoreRequest.ScoreEntry.builder()
                                .criterionId(testCriterion.getId())
                                .score(0)
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/v1/interviews/" + testInterview.getId() + "/scores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(3)
    @WithMockUser(username = "interviewer@test.com", authorities = "INTERVIEWER")
    void TC_IS_003_submitScores_InvalidScoreRange_AboveTen() throws Exception {
        SubmitInterviewScoreRequest request = SubmitInterviewScoreRequest.builder()
                .scores(Arrays.asList(
                        SubmitInterviewScoreRequest.ScoreEntry.builder()
                                .criterionId(testCriterion.getId())
                                .score(11)
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/v1/interviews/" + testInterview.getId() + "/scores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(4)
    @WithMockUser(username = "interviewer@test.com", authorities = "INTERVIEWER")
    void TC_IS_004_submitScores_InvalidCriterionID() throws Exception {
        SubmitInterviewScoreRequest request = SubmitInterviewScoreRequest.builder()
                .scores(Arrays.asList(
                        SubmitInterviewScoreRequest.ScoreEntry.builder()
                                .criterionId(UUID.randomUUID())
                                .score(8)
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/v1/interviews/" + testInterview.getId() + "/scores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(5)
    @WithMockUser(username = "interviewer@test.com", authorities = "INTERVIEWER")
    void TC_IS_005_submitScores_DuplicateSubmission() throws Exception {
        SubmitInterviewScoreRequest request = SubmitInterviewScoreRequest.builder()
                .scores(Arrays.asList(
                        SubmitInterviewScoreRequest.ScoreEntry.builder()
                                .criterionId(testCriterion.getId())
                                .score(8)
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/v1/interviews/" + testInterview.getId() + "/scores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/interviews/" + testInterview.getId() + "/scores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(6)
    @WithMockUser(username = "interviewer@test.com", authorities = "INTERVIEWER")
    void TC_IS_006_submitScores_InterviewNotFound() throws Exception {
        SubmitInterviewScoreRequest request = SubmitInterviewScoreRequest.builder()
                .scores(Arrays.asList(
                        SubmitInterviewScoreRequest.ScoreEntry.builder()
                                .criterionId(testCriterion.getId())
                                .score(8)
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/v1/interviews/" + UUID.randomUUID() + "/scores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(7)
    @WithMockUser(username = "hr@test.com", authorities = "HR")
    void TC_IS_007_getAllScorecards_Success() throws Exception {
        mockMvc.perform(get("/api/v1/interviews/" + testInterview.getId() + "/scores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(notNullValue())));
    }

    @Test
    @Order(8)
    @WithMockUser(username = "hr@test.com", authorities = "HR")
    void TC_IS_008_getAllScorecards_EmptyList() throws Exception {
        mockMvc.perform(get("/api/v1/interviews/" + testInterview.getId() + "/scores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @Order(9)
    @WithMockUser(username = "interviewer@test.com", authorities = "INTERVIEWER")
    void TC_IS_009_getMyScorecard_Success() throws Exception {
        SubmitInterviewScoreRequest request = SubmitInterviewScoreRequest.builder()
                .scores(Arrays.asList(
                        SubmitInterviewScoreRequest.ScoreEntry.builder()
                                .criterionId(testCriterion.getId())
                                .score(8)
                                .comment("Good")
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/v1/interviews/" + testInterview.getId() + "/scores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/interviews/" + testInterview.getId() + "/scores/me"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(10)
    @WithMockUser(username = "interviewer@test.com", authorities = "INTERVIEWER")
    void TC_IS_010_getMyScorecard_NotSubmitted() throws Exception {
        mockMvc.perform(get("/api/v1/interviews/" + testInterview.getId() + "/scores/me"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(11)
    @WithMockUser(username = "interviewer@test.com", authorities = "INTERVIEWER")
    void TC_IS_011_overallScoreCalculation() throws Exception {
        ScorecardCriterion criterion2 = ScorecardCriterion.builder()
                .template(testCriterion.getTemplate())
                .name("System Design")
                .weight(new BigDecimal("1.00"))
                .build();
        criterionRepository.save(criterion2);

        SubmitInterviewScoreRequest request = SubmitInterviewScoreRequest.builder()
                .scores(Arrays.asList(
                        SubmitInterviewScoreRequest.ScoreEntry.builder()
                                .criterionId(testCriterion.getId())
                                .score(8)
                                .build(),
                        SubmitInterviewScoreRequest.ScoreEntry.builder()
                                .criterionId(criterion2.getId())
                                .score(6)
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/v1/interviews/" + testInterview.getId() + "/scores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @Order(12)
    @WithMockUser(username = "interviewer@test.com", authorities = "INTERVIEWER")
    void TC_IS_012_getScorecards_UnauthorizedRole() throws Exception {
        mockMvc.perform(get("/api/v1/interviews/" + testInterview.getId() + "/scores"))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(13)
    @WithMockUser(username = "interviewer@test.com", authorities = "INTERVIEWER")
    void TC_IS_013_scoreWithEmptyComment() throws Exception {
        SubmitInterviewScoreRequest request = SubmitInterviewScoreRequest.builder()
                .scores(Arrays.asList(
                        SubmitInterviewScoreRequest.ScoreEntry.builder()
                                .criterionId(testCriterion.getId())
                                .score(7)
                                .comment(null)
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/v1/interviews/" + testInterview.getId() + "/scores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}
