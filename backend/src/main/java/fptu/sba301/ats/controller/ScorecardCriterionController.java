package fptu.sba301.ats.controller;

import fptu.sba301.ats.dto.request.CreateScorecardCriterionRequest;
import fptu.sba301.ats.dto.response.ScorecardCriterionResponse;
import fptu.sba301.ats.service.ScorecardTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static fptu.sba301.ats.constant.AppConstant.BASE_URL;

@RestController
@RequestMapping(BASE_URL + "/scorecards/criteria")
@RequiredArgsConstructor
public class ScorecardCriterionController {

    private final ScorecardTemplateService templateService;

    @PutMapping("/{criterionId}")
    @PreAuthorize("hasAnyRole('HR', 'SYSTEM_ADMIN')")
    public ResponseEntity<ScorecardCriterionResponse> update(
            @PathVariable Long criterionId,
            @Valid @RequestBody CreateScorecardCriterionRequest request) {
        return ResponseEntity.ok(templateService.updateCriterion(criterionId, request));
    }

    @DeleteMapping("/{criterionId}")
    @PreAuthorize("hasAnyRole('HR', 'SYSTEM_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long criterionId) {
        templateService.deleteCriterion(criterionId);
        return ResponseEntity.noContent().build();
    }
}
