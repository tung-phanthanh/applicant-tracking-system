package fptu.sba301.ats.controller;

import fptu.sba301.ats.dto.response.CandidateListResponse;
import fptu.sba301.ats.dto.response.CandidateProfileResponse;
import fptu.sba301.ats.service.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static fptu.sba301.ats.constant.AppConstant.BASE_URL;
import static fptu.sba301.ats.constant.AppConstant.CANDIDATE_BUSINESS;

@RestController
@RequestMapping(BASE_URL + CANDIDATE_BUSINESS)
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService candidateService;

    @GetMapping
    public ResponseEntity<List<CandidateListResponse>> getAllCandidates() {
        return ResponseEntity.ok(candidateService.getAllCandidates());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CandidateProfileResponse> getCandidateById(@PathVariable UUID id) {
        return ResponseEntity.ok(candidateService.getCandidateById(id));
    }
}
