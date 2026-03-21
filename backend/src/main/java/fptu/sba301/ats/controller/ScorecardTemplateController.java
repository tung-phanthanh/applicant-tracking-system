package fptu.sba301.ats.controller;

import fptu.sba301.ats.dto.request.CreateScorecardCriterionRequest;
import fptu.sba301.ats.dto.request.CreateScorecardTemplateRequest;
import fptu.sba301.ats.dto.request.UpdateScorecardTemplateRequest;
import fptu.sba301.ats.dto.response.ScorecardCriterionResponse;
import fptu.sba301.ats.dto.response.ScorecardTemplateResponse;
import fptu.sba301.ats.service.ScorecardTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static fptu.sba301.ats.constant.AppConstant.BASE_URL;

@RestController
@RequestMapping(BASE_URL + "/scorecards/templates")
@RequiredArgsConstructor
public class ScorecardTemplateController {

    private final ScorecardTemplateService templateService;

    @GetMapping
    @PreAuthorize("hasAnyRole('HR', 'HR_MANAGER', 'SYSTEM_ADMIN')")
    public ResponseEntity<Page<ScorecardTemplateResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(templateService.getAll(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR', 'HR_MANAGER', 'SYSTEM_ADMIN')")
    public ResponseEntity<ScorecardTemplateResponse> getById(@PathVariable java.util.UUID id) {
        return ResponseEntity.ok(templateService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('HR', 'SYSTEM_ADMIN')")
    public ResponseEntity<ScorecardTemplateResponse> create(
            @Valid @RequestBody CreateScorecardTemplateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(templateService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR', 'SYSTEM_ADMIN')")
    public ResponseEntity<ScorecardTemplateResponse> update(
            @PathVariable java.util.UUID id,
            @Valid @RequestBody UpdateScorecardTemplateRequest request) {
        return ResponseEntity.ok(templateService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR', 'SYSTEM_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable java.util.UUID id) {
        templateService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ------------------------------------------
    // ARCHIVE / UNARCHIVE (Extension Feature)
    // ------------------------------------------

    @PatchMapping("/{id}/archive")
    @PreAuthorize("hasAnyRole('HR', 'SYSTEM_ADMIN')")
    public ResponseEntity<ScorecardTemplateResponse> archive(@PathVariable java.util.UUID id) {
        return ResponseEntity.ok(templateService.archive(id));
    }

    @PatchMapping("/{id}/unarchive")
    @PreAuthorize("hasAnyRole('HR', 'SYSTEM_ADMIN')")
    public ResponseEntity<ScorecardTemplateResponse> unarchive(@PathVariable java.util.UUID id) {
        return ResponseEntity.ok(templateService.unarchive(id));
    }

    // ------------------------------------------
    // CRITERIA ENDPOINTS
    // ------------------------------------------

    @PostMapping("/{templateId}/criteria")
    @PreAuthorize("hasAnyRole('HR', 'SYSTEM_ADMIN')")
    public ResponseEntity<ScorecardCriterionResponse> addCriterion(
            @PathVariable java.util.UUID templateId,
            @Valid @RequestBody CreateScorecardCriterionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(templateService.addCriterion(templateId, request));
    }

    /**
     * Reorder all criteria for a template.
     * Body: ordered list of criterion IDs (all IDs for the template must be
     * present).
     */
    @PutMapping("/{templateId}/criteria/reorder")
    @PreAuthorize("hasAnyRole('HR', 'SYSTEM_ADMIN')")
    public ResponseEntity<Void> reorderCriteria(
            @PathVariable java.util.UUID templateId,
            @RequestBody List<java.util.UUID> orderedCriterionIds) {
        templateService.reorderCriteria(templateId, orderedCriterionIds);
        return ResponseEntity.noContent().build();
    }
}
