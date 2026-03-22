package fptu.sba301.ats.controller;

import fptu.sba301.ats.dto.request.CandidateStageUpdateRequest;
import fptu.sba301.ats.dto.response.CandidateDetailResponse;
import fptu.sba301.ats.dto.response.CandidateListResponse;
import fptu.sba301.ats.service.CandidateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static fptu.sba301.ats.constant.AppConstant.BASE_URL;
import static fptu.sba301.ats.constant.AppConstant.CANDIDATE_CONTROLLER_URL;

@RestController
@RequestMapping(BASE_URL + CANDIDATE_CONTROLLER_URL)
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService candidateService;

    @GetMapping
    @PreAuthorize("hasAuthority('CANDIDATE_VIEW')")
    public ResponseEntity<List<CandidateListResponse>> getCandidateList() {
        return ResponseEntity.ok(candidateService.getCandidateList());
    }

    @GetMapping("/{candidateId}")
    @PreAuthorize("hasAuthority('CANDIDATE_VIEW')")
    public ResponseEntity<CandidateDetailResponse> getCandidateDetail(@PathVariable UUID candidateId) {
        return ResponseEntity.ok(candidateService.getCandidateDetail(candidateId));
    }

    @PatchMapping("/{candidateId}/stage")
    @PreAuthorize("hasAuthority('CANDIDATE_MANAGE')")
    public ResponseEntity<CandidateDetailResponse> updateCandidateStage(
            @PathVariable UUID candidateId,
            @Valid @RequestBody CandidateStageUpdateRequest request
    ) {
        return ResponseEntity.ok(candidateService.updateCandidateStage(candidateId, request.getStage()));
    }
}
