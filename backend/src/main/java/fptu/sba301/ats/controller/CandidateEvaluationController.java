package fptu.sba301.ats.controller;

import fptu.sba301.ats.dto.response.EvaluationSummaryResponse;
import fptu.sba301.ats.service.CandidateEvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static fptu.sba301.ats.constant.AppConstant.BASE_URL;

@RestController
@RequestMapping(BASE_URL + "/applications")
@RequiredArgsConstructor
public class CandidateEvaluationController {

    private final CandidateEvaluationService evaluationService;

    @GetMapping("/{applicationId}/evaluation")
    @PreAuthorize("hasAnyRole('HR', 'HR_MANAGER', 'SYSTEM_ADMIN')")
    public ResponseEntity<EvaluationSummaryResponse> getEvaluation(
            @PathVariable Long applicationId) {
        return ResponseEntity.ok(evaluationService.getEvaluation(applicationId));
    }
}
