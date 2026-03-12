package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.request.CreateScorecardCriterionRequest;
import fptu.sba301.ats.dto.request.CreateScorecardTemplateRequest;
import fptu.sba301.ats.dto.request.UpdateScorecardTemplateRequest;
import fptu.sba301.ats.dto.response.ScorecardCriterionResponse;
import fptu.sba301.ats.dto.response.ScorecardTemplateResponse;
import fptu.sba301.ats.entity.ScorecardCriterion;
import fptu.sba301.ats.entity.ScorecardTemplate;
import fptu.sba301.ats.exception.BusinessException;
import fptu.sba301.ats.mapper.ScorecardTemplateMapper;
import fptu.sba301.ats.repository.InterviewScoreRepository;
import fptu.sba301.ats.repository.ScorecardCriterionRepository;
import fptu.sba301.ats.repository.ScorecardTemplateRepository;
import fptu.sba301.ats.service.ScorecardTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class ScorecardTemplateServiceImpl implements ScorecardTemplateService {

    private final ScorecardTemplateRepository templateRepository;
    private final ScorecardCriterionRepository criterionRepository;
    private final InterviewScoreRepository scoreRepository;
    private final ScorecardTemplateMapper mapper;

    // ==============================
    // TEMPLATE CRUD
    // ==============================

    @Override
    @Transactional(readOnly = true)
    public Page<ScorecardTemplateResponse> getAll(Pageable pageable) {
        return templateRepository.findAll(pageable).map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ScorecardTemplateResponse getById(Long id) {
        ScorecardTemplate template = findTemplateOrThrow(id);
        return mapper.toResponse(template);
    }

    @Override
    @Transactional
    public ScorecardTemplateResponse create(CreateScorecardTemplateRequest request) {
        if (templateRepository.existsByName(request.name())) {
            throw new BusinessException(
                    "Template with name '" + request.name() + "' already exists", HttpStatus.CONFLICT);
        }
        ScorecardTemplate template = mapper.toEntity(request);
        return mapper.toResponse(templateRepository.save(template));
    }

    @Override
    @Transactional
    public ScorecardTemplateResponse update(Long id, UpdateScorecardTemplateRequest request) {
        ScorecardTemplate template = findTemplateOrThrow(id);
        if (templateRepository.existsByNameAndIdNot(request.name(), id)) {
            throw new BusinessException(
                    "Another template with name '" + request.name() + "' already exists", HttpStatus.CONFLICT);
        }
        template.setName(request.name());
        template.setDepartmentId(request.departmentId());
        return mapper.toResponse(templateRepository.save(template));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ScorecardTemplate template = findTemplateOrThrow(id);
        // Guard: template cannot be deleted if any criterion is used in
        // interview_scores
        boolean inUse = template.getCriteria().stream()
                .anyMatch(c -> scoreRepository.existsByCriterionId(c.getId()));
        if (inUse) {
            throw new BusinessException(
                    "Template is referenced by existing interview scores and cannot be deleted", HttpStatus.CONFLICT);
        }
        templateRepository.delete(template);
        log.info("Deleted scorecard template id={}", id);
    }

    // ==============================
    // ARCHIVE / UNARCHIVE
    // ==============================

    @Override
    @Transactional
    public ScorecardTemplateResponse archive(Long id) {
        ScorecardTemplate template = findTemplateOrThrow(id);
        if (template.isArchived()) {
            throw new BusinessException(
                    "Template is already archived", HttpStatus.CONFLICT);
        }
        template.setArchived(true);
        ScorecardTemplate saved = templateRepository.save(template);
        log.info("Archived scorecard template id={}", id);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ScorecardTemplateResponse unarchive(Long id) {
        ScorecardTemplate template = findTemplateOrThrow(id);
        if (!template.isArchived()) {
            throw new BusinessException(
                    "Template is not archived", HttpStatus.CONFLICT);
        }
        template.setArchived(false);
        ScorecardTemplate saved = templateRepository.save(template);
        log.info("Unarchived scorecard template id={}", id);
        return mapper.toResponse(saved);
    }

    // ==============================
    // REORDER CRITERIA
    // ==============================

    @Override
    @Transactional
    public void reorderCriteria(Long templateId, List<Long> orderedCriterionIds) {
        ScorecardTemplate template = findTemplateOrThrow(templateId);

        Set<Long> existingIds = template.getCriteria().stream()
                .map(ScorecardCriterion::getId)
                .collect(Collectors.toSet());

        Set<Long> requestedIds = Set.copyOf(orderedCriterionIds);

        if (!existingIds.equals(requestedIds)) {
            throw new BusinessException(
                    "Reorder list must contain exactly all criterion IDs for this template",
                    HttpStatus.BAD_REQUEST);
        }

        // Build a lookup map and assign display order by list position
        Map<Long, ScorecardCriterion> criterionMap = template.getCriteria().stream()
                .collect(Collectors.toMap(ScorecardCriterion::getId, c -> c));

        for (int i = 0; i < orderedCriterionIds.size(); i++) {
            criterionMap.get(orderedCriterionIds.get(i)).setDisplayOrder(i + 1);
        }
        criterionRepository.saveAll(criterionMap.values());
        log.info("Reordered {} criteria for templateId={}", orderedCriterionIds.size(), templateId);
    }

    // ==============================
    // CRITERION CRUD
    // ==============================

    @Override
    @Transactional
    public ScorecardCriterionResponse addCriterion(Long templateId, CreateScorecardCriterionRequest request) {
        ScorecardTemplate template = findTemplateOrThrow(templateId);

        // Validate total weight will not exceed 1.0
        BigDecimal existingWeight = criterionRepository.sumWeightByTemplateId(templateId);
        if (existingWeight.add(request.weight()).compareTo(BigDecimal.ONE) > 0) {
            throw new BusinessException(
                    "Total criteria weight would exceed 1.0 (current sum: " + existingWeight + ")",
                    HttpStatus.BAD_REQUEST);
        }

        ScorecardCriterion criterion = ScorecardCriterion.builder()
                .template(template)
                .name(request.name())
                .weight(request.weight())
                .maxScore(request.maxScore())
                .build();

        return mapper.toResponse(criterionRepository.save(criterion));
    }

    @Override
    @Transactional
    public ScorecardCriterionResponse updateCriterion(Long criterionId, CreateScorecardCriterionRequest request) {
        ScorecardCriterion criterion = findCriterionOrThrow(criterionId);
        Long templateId = criterion.getTemplate().getId();

        // Validate total weight excluding this criterion will not exceed 1.0 after
        // update
        BigDecimal otherWeight = criterionRepository.sumWeightByTemplateIdExcluding(templateId, criterionId);
        if (otherWeight.add(request.weight()).compareTo(BigDecimal.ONE) > 0) {
            throw new BusinessException(
                    "Total criteria weight would exceed 1.0 (other criteria sum: " + otherWeight + ")",
                    HttpStatus.BAD_REQUEST);
        }

        criterion.setName(request.name());
        criterion.setWeight(request.weight());
        criterion.setMaxScore(request.maxScore());
        return mapper.toResponse(criterionRepository.save(criterion));
    }

    @Override
    @Transactional
    public void deleteCriterion(Long criterionId) {
        ScorecardCriterion criterion = findCriterionOrThrow(criterionId);
        if (scoreRepository.existsByCriterionId(criterionId)) {
            throw new BusinessException(
                    "Criterion is referenced by existing interview scores and cannot be deleted", HttpStatus.CONFLICT);
        }
        criterionRepository.delete(criterion);
        log.info("Deleted scorecard criterion id={}", criterionId);
    }

    // ==============================
    // Helpers
    // ==============================

    private ScorecardTemplate findTemplateOrThrow(Long id) {
        return templateRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Scorecard template not found with id: " + id, HttpStatus.NOT_FOUND));
    }

    private ScorecardCriterion findCriterionOrThrow(Long id) {
        return criterionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Scorecard criterion not found with id: " + id, HttpStatus.NOT_FOUND));
    }
}
