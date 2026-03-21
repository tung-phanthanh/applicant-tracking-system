package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.request.CreateScorecardCriterionRequest;
import fptu.sba301.ats.dto.request.CreateScorecardTemplateRequest;
import fptu.sba301.ats.dto.request.UpdateScorecardTemplateRequest;
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
import java.util.UUID;
import java.util.List;

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

    private final UUID templateId = UUID.randomUUID();
    private final UUID criterionId = UUID.randomUUID();

    @Test
    void addCriterion_Success() {
        ScorecardTemplate template = new ScorecardTemplate();
        template.setId(templateId);

        when(templateRepository.findById(templateId)).thenReturn(Optional.of(template));

        CreateScorecardCriterionRequest request = new CreateScorecardCriterionRequest("Algorithmic",
                new BigDecimal("0.60"), 5);
        when(criterionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> service.addCriterion(templateId, request));
        verify(criterionRepository).save(any(ScorecardCriterion.class));
    }

    @Test
    void deleteTemplate_BlockedWhenInUse() {
        ScorecardTemplate template = new ScorecardTemplate();
        template.setId(templateId);

        ScorecardCriterion criterion = new ScorecardCriterion();
        criterion.setId(criterionId);
        criterion.setTemplate(template);

        // In ServiceImpl, it fetches all criteria and filters
        when(templateRepository.findById(templateId)).thenReturn(Optional.of(template));
        when(criterionRepository.findAll()).thenReturn(List.of(criterion));
        when(scoreRepository.existsByCriterionId(criterionId)).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.delete(templateId));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    }

    @Test
    void getById_NotFound_Throws404() {
        UUID randomId = UUID.randomUUID();
        when(templateRepository.findById(randomId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> service.getById(randomId));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertTrue(ex.getMessage().contains("not found"));
    }

    @Test
    void create_DuplicateName_ThrowsConflict() {
        when(templateRepository.existsByName("Dup")).thenReturn(true);

        CreateScorecardTemplateRequest req =
                new CreateScorecardTemplateRequest("Dup", null);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.create(req));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        assertTrue(ex.getMessage().contains("already exists"));
    }

    @Test
    void update_TemplateNotFound_Throws404() {
        UUID randomId = UUID.randomUUID();
        when(templateRepository.findById(randomId)).thenReturn(Optional.empty());

        UpdateScorecardTemplateRequest req =
                new UpdateScorecardTemplateRequest("NewName", null);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.update(randomId, req));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void update_DuplicateName_ThrowsConflict() {
        ScorecardTemplate template = new ScorecardTemplate();
        template.setId(templateId);
        template.setName("Old Name");

        when(templateRepository.findById(templateId)).thenReturn(Optional.of(template));
        when(templateRepository.existsByNameAndIdNot("Taken", templateId)).thenReturn(true);

        UpdateScorecardTemplateRequest req = new UpdateScorecardTemplateRequest(
                "Taken", null);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.update(templateId, req));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        assertTrue(ex.getMessage().contains("already exists"));
    }

    @Test
    void deleteCriterion_InUse_ThrowsConflict() {
        ScorecardCriterion criterion = new ScorecardCriterion();
        criterion.setId(criterionId);

        when(criterionRepository.findById(criterionId)).thenReturn(Optional.of(criterion));
        when(scoreRepository.existsByCriterionId(criterionId)).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.deleteCriterion(criterionId));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        assertTrue(ex.getMessage().contains("referenced"));
    }

    @Test
    void archive_ThrowsBadRequest() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.archive(templateId));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertTrue(ex.getMessage().contains("no longer supported"));
    }

    @Test
    void reorderCriteria_ThrowsBadRequest() {
        List<UUID> order = List.of(UUID.randomUUID());
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.reorderCriteria(templateId, order));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertTrue(ex.getMessage().contains("no longer supported"));
    }
}
