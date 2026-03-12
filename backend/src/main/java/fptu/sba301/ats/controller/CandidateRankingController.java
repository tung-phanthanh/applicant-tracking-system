package fptu.sba301.ats.controller;

import fptu.sba301.ats.dto.response.CandidateRankingResponse;
import fptu.sba301.ats.service.CandidateRankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static fptu.sba301.ats.constant.AppConstant.BASE_URL;

@RestController
@RequestMapping(BASE_URL + "/jobs")
@RequiredArgsConstructor
public class CandidateRankingController {

    private final CandidateRankingService rankingService;

    @GetMapping("/{jobId}/ranking")
    @PreAuthorize("hasAnyRole('HR', 'HR_MANAGER', 'SYSTEM_ADMIN')")
    public ResponseEntity<CandidateRankingResponse> getRanking(@PathVariable Long jobId) {
        return ResponseEntity.ok(rankingService.getRanking(jobId));
    }
}
