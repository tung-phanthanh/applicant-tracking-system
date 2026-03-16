package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.request.CreateScorecardTemplateRequest;
import fptu.sba301.ats.dto.response.ScorecardTemplateResponse;
import fptu.sba301.ats.entity.Department;
import fptu.sba301.ats.entity.ScorecardCriterion;
import fptu.sba301.ats.entity.ScorecardTemplate;
import fptu.sba301.ats.exception.BusinessException;
import fptu.sba301.ats.mapper.ScorecardMapper;
import fptu.sba301.ats.repository.DepartmentRepository;
import fptu.sba301.ats.repository.ScorecardTemplateRepository;
import fptu.sba301.ats.service.ScorecardTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ScorecardTemplateServiceImpl implements ScorecardTemplateService {

    private final ScorecardTemplateRepository templateRepository;
    private final DepartmentRepository departmentRepository;
    private final ScorecardMapper scorecardMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ScorecardTemplateResponse> getAll(Pageable pageable) {
        return templateRepository.findAll(pageable)
                .map(scorecardMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ScorecardTemplateResponse getById(UUID id) {
        ScorecardTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Scorecard template not found: " + id, HttpStatus.NOT_FOUND));
        return scorecardMapper.toResponse(template);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScorecardTemplateResponse> getByDepartment(UUID departmentId) {
        return scorecardMapper.toResponseList(templateRepository.findByDepartmentId(departmentId));
    }

    @Override
    public ScorecardTemplateResponse create(CreateScorecardTemplateRequest request) {
        ScorecardTemplate template = ScorecardTemplate.builder()
                .name(request.getName())
                .build();

        if (request.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new BusinessException("Department not found: " + request.getDepartmentId(), HttpStatus.NOT_FOUND));
            template.setDepartment(dept);
        }

        if (request.getCriteria() != null) {
            for (CreateScorecardTemplateRequest.CriterionDto dto : request.getCriteria()) {
                ScorecardCriterion criterion = ScorecardCriterion.builder()
                        .name(dto.getName())
                        .weight(dto.getWeight() != null ? dto.getWeight() : BigDecimal.ONE)
                        .template(template)
                        .build();
                template.getCriteria().add(criterion);
            }
        }

        return scorecardMapper.toResponse(templateRepository.save(template));
    }

    @Override
    public ScorecardTemplateResponse update(UUID id, CreateScorecardTemplateRequest request) {
        ScorecardTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Scorecard template not found: " + id, HttpStatus.NOT_FOUND));

        scorecardMapper.updateFromRequest(request, template);

        if (request.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new BusinessException("Department not found: " + request.getDepartmentId(), HttpStatus.NOT_FOUND));
            template.setDepartment(dept);
        }

        template.getCriteria().clear();
        if (request.getCriteria() != null) {
            for (CreateScorecardTemplateRequest.CriterionDto dto : request.getCriteria()) {
                ScorecardCriterion criterion = ScorecardCriterion.builder()
                        .name(dto.getName())
                        .weight(dto.getWeight() != null ? dto.getWeight() : BigDecimal.ONE)
                        .template(template)
                        .build();
                template.getCriteria().add(criterion);
            }
        }

        return scorecardMapper.toResponse(templateRepository.save(template));
    }

    @Override
    public void delete(UUID id) {
        if (!templateRepository.existsById(id)) {
            throw new BusinessException("Scorecard template not found: " + id, HttpStatus.NOT_FOUND);
        }
        templateRepository.deleteById(id);
    }
}
