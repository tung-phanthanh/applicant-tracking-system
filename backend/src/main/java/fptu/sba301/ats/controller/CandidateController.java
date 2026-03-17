package fptu.sba301.ats.controller;

import fptu.sba301.ats.dto.response.CandidateListResponse;
import fptu.sba301.ats.service.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static fptu.sba301.ats.constant.AppConstant.BASE_URL;
import static fptu.sba301.ats.constant.AppConstant.CANDIDATE_CONTROLLER_URL;

@RestController
@RequestMapping(BASE_URL + CANDIDATE_CONTROLLER_URL)
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService candidateService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('HR')")
    public ResponseEntity<List<CandidateListResponse>> getCandidateList() {
        return ResponseEntity.ok(candidateService.getCandidateList());
    }
}
