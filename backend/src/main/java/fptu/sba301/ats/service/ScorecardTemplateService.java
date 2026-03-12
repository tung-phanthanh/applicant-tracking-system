package fptu.sba301.ats.service;

import fptu.sba301.ats.dto.request.CreateScorecardCriterionRequest;
import fptu.sba301.ats.dto.request.CreateScorecardTemplateRequest;
import fptu.sba301.ats.dto.request.UpdateScorecardTemplateRequest;
import fptu.sba301.ats.dto.response.ScorecardCriterionResponse;
import fptu.sba301.ats.dto.response.ScorecardTemplateResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ScorecardTemplateService {

    Page<ScorecardTemplateResponse> getAll(Pageable pageable);

    ScorecardTemplateResponse getById(Long id);

    ScorecardTemplateResponse create(CreateScorecardTemplateRequest request);

    ScorecardTemplateResponse update(Long id, UpdateScorecardTemplateRequest request);

    void delete(Long id);

    /** Soft-archive a template so it no longer appears in default listing */
    ScorecardTemplateResponse archive(Long id);

    /** Restore a previously archived template */
    ScorecardTemplateResponse unarchive(Long id);

    /**
     * Reorder criteria for a template.
     * 
     * @param templateId          target template
     * @param orderedCriterionIds criterion IDs in desired display order (1-indexed
     *                            position = list index + 1)
     */
    void reorderCriteria(Long templateId, List<Long> orderedCriterionIds);

    ScorecardCriterionResponse addCriterion(Long templateId, CreateScorecardCriterionRequest request);

    ScorecardCriterionResponse updateCriterion(Long criterionId, CreateScorecardCriterionRequest request);

    void deleteCriterion(Long criterionId);
}
