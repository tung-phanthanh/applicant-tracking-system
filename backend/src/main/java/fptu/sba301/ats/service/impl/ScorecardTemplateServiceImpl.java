package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.request.CreateScorecardCriterionRequest;
import fptu.sba301.ats.dto.request.CreateScorecardTemplateRequest;
import fptu.sba301.ats.dto.request.UpdateScorecardTemplateRequest;
import fptu.sba301.ats.dto.response.ScorecardCriterionResponse;
import fptu.sba301.ats.dto.response.ScorecardTemplateResponse;
import fptu.sba301.ats.entity.Department;
import fptu.sba301.ats.entity.ScorecardCriterion;
import fptu.sba301.ats.entity.ScorecardTemplate;
import fptu.sba301.ats.exception.BusinessException;
import fptu.sba301.ats.mapper.ScorecardTemplateMapper;
import fptu.sba301.ats.repository.DepartmentRepository;
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
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class ScorecardTemplateServiceImpl implements ScorecardTemplateService {

    private final ScorecardTemplateRepository templateRepository;
    private final ScorecardCriterionRepository criterionRepository;
    private final InterviewScoreRepository scoreRepository;
    private final DepartmentRepository departmentRepository;
    private final ScorecardTemplateMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ScorecardTemplateResponse> getAll(Pageable pageable) {
        return templateRepository.findAll(pageable).map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ScorecardTemplateResponse getById(UUID id) {
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
    public ScorecardTemplateResponse update(UUID id, UpdateScorecardTemplateRequest request) {
        ScorecardTemplate template = findTemplateOrThrow(id);
        if (templateRepository.existsByNameAndIdNot(request.name(), id)) {
            throw new BusinessException(
                    "Another template with name '" + request.name() + "' already exists", HttpStatus.CONFLICT);
        }
        template.setName(request.name());
        if (request.departmentId() != null) {
            template.setDepartment(departmentRepository.getReferenceById(request.departmentId()));
        } else {
            template.setDepartment(null);
        }
        return mapper.toResponse(templateRepository.save(template));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        ScorecardTemplate template = findTemplateOrThrow(id);
        // We cannot delete if criteria is used
        // However, template.getCriteria() is no longer mapped in Entity. We must query it.
        List<ScorecardCriterion> criteria = criterionRepository.findAll().stream()
                 .filter(c -> c.getTemplate() != null && c.getTemplate().getId().equals(id))
                 .toList();

        boolean inUse = criteria.stream().anyMatch(c -> scoreRepository.existsByCriterionId(c.getId()));
        if (inUse) {
            throw new BusinessException(
                    "Template is referenced by existing interview scores and cannot be deleted", HttpStatus.CONFLICT);
        }
        
        criterionRepository.deleteAll(criteria);
        templateRepository.delete(template);
        log.info("Deleted scorecard template id={}", id);
    }

    @Override
    @Transactional
    public ScorecardTemplateResponse archive(UUID id) {
        throw new BusinessException("Archive is no longer supported", HttpStatus.BAD_REQUEST);
    }

    @Override
    @Transactional
    public ScorecardTemplateResponse unarchive(UUID id) {
        throw new BusinessException("Unarchive is no longer supported", HttpStatus.BAD_REQUEST);
    }

    @Override
    @Transactional
    public void reorderCriteria(UUID templateId, List<UUID> orderedCriterionIds) {
        throw new BusinessException("Reordering criteria is no longer supported", HttpStatus.BAD_REQUEST);
    }

    @Override
    @Transactional
    public ScorecardCriterionResponse addCriterion(UUID templateId, CreateScorecardCriterionRequest request) {
        ScorecardTemplate template = findTemplateOrThrow(templateId);

        // Validation for total weight could go here. For now, disabled because sumWeightByTemplateId doesn't exist
        ScorecardCriterion criterion = ScorecardCriterion.builder()
                .template(template)
                .name(request.name())
                .weight(request.weight())
                .build();

        return mapper.toResponse(criterionRepository.save(criterion));
    }

    @Override
    @Transactional
    public ScorecardCriterionResponse updateCriterion(UUID criterionId, CreateScorecardCriterionRequest request) {
        ScorecardCriterion criterion = findCriterionOrThrow(criterionId);
        
        criterion.setName(request.name());
        criterion.setWeight(request.weight());
        return mapper.toResponse(criterionRepository.save(criterion));
    }

    @Override
    @Transactional
    public void deleteCriterion(UUID criterionId) {
        ScorecardCriterion criterion = findCriterionOrThrow(criterionId);
        if (scoreRepository.existsByCriterionId(criterionId)) {
            throw new BusinessException(
                    "Criterion is referenced by existing interview scores and cannot be deleted", HttpStatus.CONFLICT);
        }
        criterionRepository.delete(criterion);
        log.info("Deleted scorecard criterion id={}", criterionId);
    }

    private ScorecardTemplate findTemplateOrThrow(UUID id) {
        return templateRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Scorecard template not found with id: " + id, HttpStatus.NOT_FOUND));
    }

    private ScorecardCriterion findCriterionOrThrow(UUID id) {
        return criterionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Scorecard criterion not found with id: " + id, HttpStatus.NOT_FOUND));
    }
}
