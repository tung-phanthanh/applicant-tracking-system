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

    ScorecardTemplateResponse getById(java.util.UUID id);

    ScorecardTemplateResponse create(CreateScorecardTemplateRequest request);

    ScorecardTemplateResponse update(java.util.UUID id, UpdateScorecardTemplateRequest request);

    void delete(java.util.UUID id);

    /** Soft-archive a template so it no longer appears in default listing */
    ScorecardTemplateResponse archive(java.util.UUID id);

    /** Restore a previously archived template */
    ScorecardTemplateResponse unarchive(java.util.UUID id);

    /**
     * Reorder criteria for a template.
     * 
     * @param templateId          target template
     * @param orderedCriterionIds criterion IDs in desired display order (1-indexed
     *                            position = list index + 1)
     */
    void reorderCriteria(java.util.UUID templateId, List<java.util.UUID> orderedCriterionIds);

    ScorecardCriterionResponse addCriterion(java.util.UUID templateId, CreateScorecardCriterionRequest request);

    ScorecardCriterionResponse updateCriterion(java.util.UUID criterionId, CreateScorecardCriterionRequest request);

    void deleteCriterion(java.util.UUID criterionId);
}
