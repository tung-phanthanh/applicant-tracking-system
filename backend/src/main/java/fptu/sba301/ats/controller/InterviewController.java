package fptu.sba301.ats.controller;

import fptu.sba301.ats.dto.request.SubmitInterviewScoreRequest;
import fptu.sba301.ats.dto.response.InterviewScorecardResponse;
import fptu.sba301.ats.service.InterviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static fptu.sba301.ats.constant.AppConstant.BASE_URL;

@RestController
@RequestMapping(BASE_URL + "/interviews")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping("/{interviewId}/scores")
    @PreAuthorize("hasAnyAuthority('INTERVIEWER')")
    public ResponseEntity<Void> submitScores(
            @PathVariable UUID interviewId,
            @Valid @RequestBody SubmitInterviewScoreRequest request,
            Authentication authentication) {
        interviewService.submitScores(interviewId, request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{interviewId}/scores")
    @PreAuthorize("hasAnyAuthority('HR', 'HR_MANAGER', 'INTERVIEWER')")
    public ResponseEntity<List<InterviewScorecardResponse>> getAllScorecards(
            @PathVariable UUID interviewId) {
        return ResponseEntity.ok(interviewService.getAllScorecards(interviewId));
    }

    @GetMapping("/{interviewId}/scores/me")
    @PreAuthorize("hasAnyAuthority('INTERVIEWER')")
    public ResponseEntity<InterviewScorecardResponse> getMyScorecard(
            @PathVariable UUID interviewId,
            Authentication authentication) {
        return ResponseEntity.ok(interviewService.getMyScorecard(interviewId, authentication.getName()));
    }
}
