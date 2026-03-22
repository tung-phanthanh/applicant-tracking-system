package fptu.sba301.ats.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fptu.sba301.ats.dto.request.CreateScorecardTemplateRequest;
import fptu.sba301.ats.dto.response.ScorecardTemplateResponse;
import fptu.sba301.ats.entity.Department;
import fptu.sba301.ats.entity.ScorecardCriterion;
import fptu.sba301.ats.entity.ScorecardTemplate;
import fptu.sba301.ats.repository.DepartmentRepository;
import fptu.sba301.ats.repository.ScorecardCriterionRepository;
import fptu.sba301.ats.repository.ScorecardTemplateRepository;
import fptu.sba301.ats.service.ScorecardTemplateService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
class ScorecardTemplateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ScorecardTemplateRepository templateRepository;

    @Autowired
    private ScorecardCriterionRepository criterionRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Department testDepartment;
    private ScorecardTemplate testTemplate;

    @BeforeEach
    void setUp() {
        testDepartment = Department.builder()
                .name("Engineering")
                .build();
        departmentRepository.save(testDepartment);

        testTemplate = ScorecardTemplate.builder()
                .name("Technical Interview")
                .department(testDepartment)
                .build();
        templateRepository.save(testTemplate);

        ScorecardCriterion criterion = ScorecardCriterion.builder()
                .template(testTemplate)
                .name("Problem Solving")
                .weight(new BigDecimal("2.00"))
                .build();
        criterionRepository.save(criterion);
    }

    @AfterEach
    void tearDown() {
        criterionRepository.deleteAll();
        templateRepository.deleteAll();
        departmentRepository.deleteAll();
    }

    @Test
    @Order(1)
    @WithMockUser(authorities = "HR_MANAGER")
    void TC_ST_001_createScorecardTemplate_Success() throws Exception {
        CreateScorecardTemplateRequest request = CreateScorecardTemplateRequest.builder()
                .name("Behavioral Interview")
                .departmentId(testDepartment.getId())
                .criteria(Arrays.asList(
                        CreateScorecardTemplateRequest.CriterionRequest.builder()
                                .name("Communication")
                                .weight(new BigDecimal("1.50"))
                                .build(),
                        CreateScorecardTemplateRequest.CriterionRequest.builder()
                                .name("Leadership")
                                .weight(new BigDecimal("2.00"))
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/v1/scorecard-templates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Behavioral Interview"))
                .andExpect(jsonPath("$.departmentId").value(testDepartment.getId().toString()))
                .andExpect(jsonPath("$.criteria", hasSize(2)))
                .andExpect(jsonPath("$.criteria[0].name").value("Communication"))
                .andExpect(jsonPath("$.criteria[0].weight").value(1.50));
    }

    @Test
    @Order(2)
    @WithMockUser(authorities = "HR_MANAGER")
    void TC_ST_002_createScorecardTemplate_MissingName() throws Exception {
        CreateScorecardTemplateRequest request = CreateScorecardTemplateRequest.builder()
                .name(null)
                .criteria(Arrays.asList(
                        CreateScorecardTemplateRequest.CriterionRequest.builder()
                                .name("Test")
                                .weight(new BigDecimal("1.00"))
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/v1/scorecard-templates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(3)
    @WithMockUser(authorities = "HR_MANAGER")
    void TC_ST_003_createScorecardTemplate_EmptyCriteria() throws Exception {
        CreateScorecardTemplateRequest request = CreateScorecardTemplateRequest.builder()
                .name("Empty Criteria Template")
                .criteria(Arrays.asList())
                .build();

        mockMvc.perform(post("/api/v1/scorecard-templates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(4)
    @WithMockUser(authorities = "HR_MANAGER")
    void TC_ST_004_createScorecardTemplate_InvalidDepartment() throws Exception {
        CreateScorecardTemplateRequest request = CreateScorecardTemplateRequest.builder()
                .name("Template with Invalid Dept")
                .departmentId(UUID.randomUUID())
                .criteria(Arrays.asList(
                        CreateScorecardTemplateRequest.CriterionRequest.builder()
                                .name("Test")
                                .weight(new BigDecimal("1.00"))
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/v1/scorecard-templates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(5)
    @WithMockUser(authorities = "INTERVIEWER")
    void TC_ST_005_createScorecardTemplate_UnauthorizedRole() throws Exception {
        CreateScorecardTemplateRequest request = CreateScorecardTemplateRequest.builder()
                .name("Unauthorized Template")
                .criteria(Arrays.asList(
                        CreateScorecardTemplateRequest.CriterionRequest.builder()
                                .name("Test")
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/v1/scorecard-templates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(6)
    @WithMockUser(authorities = "HR_MANAGER")
    void TC_ST_006_getAllTemplates_Success() throws Exception {
        mockMvc.perform(get("/api/v1/scorecard-templates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(notNullValue())))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].name").exists());
    }

    @Test
    @Order(7)
    @WithMockUser(authorities = "HR_MANAGER")
    void TC_ST_007_getAllTemplates_EmptyList() throws Exception {
        criterionRepository.deleteAll();
        templateRepository.deleteAll();

        mockMvc.perform(get("/api/v1/scorecard-templates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @Order(8)
    @WithMockUser(authorities = "HR_MANAGER")
    void TC_ST_008_getTemplateById_Success() throws Exception {
        mockMvc.perform(get("/api/v1/scorecard-templates/" + testTemplate.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testTemplate.getId().toString()))
                .andExpect(jsonPath("$.name").value("Technical Interview"))
                .andExpect(jsonPath("$.criteria", hasSize(1)));
    }

    @Test
    @Order(9)
    @WithMockUser(authorities = "HR_MANAGER")
    void TC_ST_009_getTemplateById_NotFound() throws Exception {
        mockMvc.perform(get("/api/v1/scorecard-templates/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(10)
    @WithMockUser(authorities = "HR_MANAGER")
    void TC_ST_010_getTemplatesByDepartment_Success() throws Exception {
        mockMvc.perform(get("/api/v1/scorecard-templates/department/" + testDepartment.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Technical Interview"));
    }

    @Test
    @Order(11)
    @WithMockUser(authorities = "HR_MANAGER")
    void TC_ST_011_updateScorecardTemplate_Success() throws Exception {
        CreateScorecardTemplateRequest request = CreateScorecardTemplateRequest.builder()
                .name("Updated Technical Interview")
                .criteria(Arrays.asList(
                        CreateScorecardTemplateRequest.CriterionRequest.builder()
                                .name("Updated Problem Solving")
                                .weight(new BigDecimal("3.00"))
                                .build(),
                        CreateScorecardTemplateRequest.CriterionRequest.builder()
                                .name("System Design")
                                .weight(new BigDecimal("2.50"))
                                .build()
                ))
                .build();

        mockMvc.perform(put("/api/v1/scorecard-templates/" + testTemplate.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Technical Interview"))
                .andExpect(jsonPath("$.criteria", hasSize(2)));
    }

    @Test
    @Order(12)
    @WithMockUser(authorities = "HR_MANAGER")
    void TC_ST_012_updateScorecardTemplate_NotFound() throws Exception {
        CreateScorecardTemplateRequest request = CreateScorecardTemplateRequest.builder()
                .name("Updated Template")
                .criteria(Arrays.asList(
                        CreateScorecardTemplateRequest.CriterionRequest.builder()
                                .name("Test")
                                .build()
                ))
                .build();

        mockMvc.perform(put("/api/v1/scorecard-templates/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(13)
    @WithMockUser(authorities = "HR_MANAGER")
    void TC_ST_013_deleteScorecardTemplate_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/scorecard-templates/" + testTemplate.getId()))
                .andExpect(status().isNoContent());

        assertFalse(templateRepository.existsById(testTemplate.getId()));
    }

    @Test
    @Order(14)
    @WithMockUser(authorities = "HR_MANAGER")
    void TC_ST_014_deleteScorecardTemplate_NotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/scorecard-templates/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(15)
    @WithMockUser(authorities = "HR")
    void TC_ST_015_deleteScorecardTemplate_UnauthorizedRole() throws Exception {
        mockMvc.perform(delete("/api/v1/scorecard-templates/" + testTemplate.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(16)
    @WithMockUser(authorities = "HR_MANAGER")
    void TC_ST_016_criteriaWeightValidation() throws Exception {
        CreateScorecardTemplateRequest request = CreateScorecardTemplateRequest.builder()
                .name("Negative Weight Template")
                .criteria(Arrays.asList(
                        CreateScorecardTemplateRequest.CriterionRequest.builder()
                                .name("Test")
                                .weight(new BigDecimal("-1.00"))
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/v1/scorecard-templates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @Order(17)
    @WithMockUser(authorities = "HR_MANAGER")
    void TC_ST_017_templateNameUniqueness() throws Exception {
        CreateScorecardTemplateRequest request = CreateScorecardTemplateRequest.builder()
                .name("Technical Interview")
                .criteria(Arrays.asList(
                        CreateScorecardTemplateRequest.CriterionRequest.builder()
                                .name("Test")
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/v1/scorecard-templates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}
