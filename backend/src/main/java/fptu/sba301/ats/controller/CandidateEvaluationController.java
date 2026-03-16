package fptu.sba301.ats.controller;

import fptu.sba301.ats.constant.AppConstant;
import fptu.sba301.ats.dto.response.CandidateEvaluationResponse;
import fptu.sba301.ats.dto.response.CandidateRankingResponse;
import fptu.sba301.ats.service.CandidateEvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CandidateEvaluationController {

    private final CandidateEvaluationService evaluationService;

    @GetMapping(AppConstant.BASE_URL + "/applications/{applicationId}/evaluation")
    public ResponseEntity<CandidateEvaluationResponse> getCandidateEvaluation(
            @PathVariable UUID applicationId) {
        return ResponseEntity.ok(evaluationService.getCandidateEvaluation(applicationId));
    }

    @GetMapping(AppConstant.BASE_URL + "/jobs/{jobId}/ranking")
    public ResponseEntity<CandidateRankingResponse> getCandidatesRanking(
            @PathVariable UUID jobId) {
        return ResponseEntity.ok(evaluationService.getCandidatesRanking(jobId));
    }
}
