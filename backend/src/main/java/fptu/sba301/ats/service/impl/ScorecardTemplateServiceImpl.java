package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.response.ScorecardCriterionResponse;
import fptu.sba301.ats.dto.response.ScorecardTemplateResponse;
import fptu.sba301.ats.repository.ScorecardTemplateRepository;
import fptu.sba301.ats.service.ScorecardTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScorecardTemplateServiceImpl implements ScorecardTemplateService {

    private final ScorecardTemplateRepository templateRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ScorecardTemplateResponse> getAllTemplates() {
        return templateRepository.findAll().stream()
                .map(template -> {
                    List<ScorecardCriterionResponse> criterionResponses = template.getCriteria().stream()
                            .map(c -> ScorecardCriterionResponse.builder()
                                    .id(c.getId())
                                    .name(c.getName())
                                    .weight(c.getWeight())
                                    .build())
                            .toList();

                    return ScorecardTemplateResponse.builder()
                            .id(template.getId())
                            .name(template.getName())
                            .departmentId(template.getDepartment() != null ? template.getDepartment().getId() : null)
                            .departmentName(template.getDepartment() != null ? template.getDepartment().getName() : null)
                            .criteria(criterionResponses)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
