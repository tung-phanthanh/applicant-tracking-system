package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.request.CreateScorecardCriterionRequest;
import fptu.sba301.ats.entity.ScorecardCriterion;
import fptu.sba301.ats.entity.ScorecardTemplate;
import fptu.sba301.ats.exception.BusinessException;
import fptu.sba301.ats.mapper.ScorecardTemplateMapper;
import fptu.sba301.ats.repository.InterviewScoreRepository;
import fptu.sba301.ats.repository.ScorecardCriterionRepository;
import fptu.sba301.ats.repository.ScorecardTemplateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScorecardTemplateServiceImplTest {

    @Mock
    private ScorecardTemplateRepository templateRepository;
    @Mock
    private ScorecardCriterionRepository criterionRepository;
    @Mock
    private InterviewScoreRepository scoreRepository;
    @Mock
    private ScorecardTemplateMapper mapper;

    @InjectMocks
    private ScorecardTemplateServiceImpl service;

    @Test
    void addCriterion_Success() {
        Long templateId = 1L;
        ScorecardTemplate template = new ScorecardTemplate();
        template.setId(templateId);

        when(templateRepository.findById(templateId)).thenReturn(Optional.of(template));
        // Current sum is 0.40
        when(criterionRepository.sumWeightByTemplateId(templateId)).thenReturn(new BigDecimal("0.40"));

        CreateScorecardCriterionRequest request = new CreateScorecardCriterionRequest("Algorithmic",
                new BigDecimal("0.60"), 5);
        when(criterionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> service.addCriterion(templateId, request));
        verify(criterionRepository).save(any(ScorecardCriterion.class));
    }

    @Test
    void addCriterion_ExceedsWeight_ThrowsException() {
        Long templateId = 1L;
        ScorecardTemplate template = new ScorecardTemplate();
        template.setId(templateId);

        when(templateRepository.findById(templateId)).thenReturn(Optional.of(template));
        // Current sum is 0.70
        when(criterionRepository.sumWeightByTemplateId(templateId)).thenReturn(new BigDecimal("0.70"));

        // Adding 0.40 would make it 1.10 (exceeds 1.0)
        CreateScorecardCriterionRequest request = new CreateScorecardCriterionRequest("System Design",
                new BigDecimal("0.40"), 5);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.addCriterion(templateId, request));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertTrue(ex.getMessage().contains("Total criteria weight would exceed 1.0"));
        verify(criterionRepository, never()).save(any());
    }

    @Test
    void deleteTemplate_BlockedWhenInUse() {
        Long templateId = 1L;
        ScorecardTemplate template = new ScorecardTemplate();
        template.setId(templateId);

        ScorecardCriterion criterion = new ScorecardCriterion();
        criterion.setId(10L);
        template.getCriteria().add(criterion);

        when(templateRepository.findById(templateId)).thenReturn(Optional.of(template));
        when(scoreRepository.existsByCriterionId(10L)).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.delete(templateId));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    }

    // ==========================================
    // Additional exception paths
    // ==========================================

    @Test
    void getById_NotFound_Throws404() {
        when(templateRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> service.getById(999L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertTrue(ex.getMessage().contains("not found"));
    }

    @Test
    void create_DuplicateName_ThrowsConflict() {
        when(templateRepository.existsByName("Dup")).thenReturn(true);

        fptu.sba301.ats.dto.request.CreateScorecardTemplateRequest req =
                new fptu.sba301.ats.dto.request.CreateScorecardTemplateRequest("Dup", null);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.create(req));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        assertTrue(ex.getMessage().contains("already exists"));
    }

    @Test
    void update_TemplateNotFound_Throws404() {
        when(templateRepository.findById(88L)).thenReturn(Optional.empty());

        fptu.sba301.ats.dto.request.UpdateScorecardTemplateRequest req =
                new fptu.sba301.ats.dto.request.UpdateScorecardTemplateRequest("NewName", null);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.update(88L, req));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void update_DuplicateName_ThrowsConflict() {
        Long templateId = 5L;
        ScorecardTemplate template = new ScorecardTemplate();
        template.setId(templateId);
        template.setName("Old Name");

        when(templateRepository.findById(templateId)).thenReturn(Optional.of(template));
        when(templateRepository.existsByNameAndIdNot("Taken", templateId)).thenReturn(true);

        fptu.sba301.ats.dto.request.UpdateScorecardTemplateRequest req = new fptu.sba301.ats.dto.request.UpdateScorecardTemplateRequest(
                "Taken", null);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.update(templateId, req));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        assertTrue(ex.getMessage().contains("already exists"));
    }

    @Test
    void deleteCriterion_InUse_ThrowsConflict() {
        ScorecardCriterion criterion = new ScorecardCriterion();
        criterion.setId(20L);
        criterion.setTemplate(new ScorecardTemplate());

        when(criterionRepository.findById(20L)).thenReturn(Optional.of(criterion));
        when(scoreRepository.existsByCriterionId(20L)).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.deleteCriterion(20L));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        assertTrue(ex.getMessage().contains("referenced"));
    }

    @Test
    void updateCriterion_ExceedsWeight_ThrowsBadRequest() {
        ScorecardTemplate template = new ScorecardTemplate();
        template.setId(1L);

        ScorecardCriterion criterion = new ScorecardCriterion();
        criterion.setId(30L);
        criterion.setTemplate(template);

        when(criterionRepository.findById(30L)).thenReturn(Optional.of(criterion));
        // Other criteria occupy 0.80
        when(criterionRepository.sumWeightByTemplateIdExcluding(1L, 30L))
                .thenReturn(new BigDecimal("0.80"));

        // Try to set weight 0.30 → 0.80 + 0.30 = 1.10 > 1.0
        CreateScorecardCriterionRequest req = new CreateScorecardCriterionRequest("New",
                new BigDecimal("0.30"), 5);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.updateCriterion(30L, req));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertTrue(ex.getMessage().contains("exceed 1.0"));
    }

    @Test
    void archive_AlreadyArchived_ThrowsConflict() {
        ScorecardTemplate template = new ScorecardTemplate();
        template.setId(7L);
        template.setArchived(true);

        when(templateRepository.findById(7L)).thenReturn(Optional.of(template));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.archive(7L));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        assertTrue(ex.getMessage().contains("already archived"));
    }

    @Test
    void reorderCriteria_MismatchedIds_ThrowsBadRequest() {
        ScorecardTemplate template = new ScorecardTemplate();
        template.setId(8L);

        ScorecardCriterion c1 = new ScorecardCriterion();
        c1.setId(1L);
        c1.setTemplate(template);
        ScorecardCriterion c2 = new ScorecardCriterion();
        c2.setId(2L);
        c2.setTemplate(template);
        template.getCriteria().add(c1);
        template.getCriteria().add(c2);

        when(templateRepository.findById(8L)).thenReturn(Optional.of(template));

        // Provide wrong IDs (missing c2, includes bogus 99)
        java.util.List<Long> wrongOrder = java.util.List.of(1L, 99L);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.reorderCriteria(8L, wrongOrder));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertTrue(ex.getMessage().contains("exactly all criterion IDs"));
    }
}
