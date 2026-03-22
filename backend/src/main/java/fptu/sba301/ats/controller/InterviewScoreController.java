package fptu.sba301.ats.controller;

import fptu.sba301.ats.dto.request.SubmitInterviewScoresRequest;
import fptu.sba301.ats.dto.response.InterviewScoresResponse;
import fptu.sba301.ats.security.SecurityUtils;
import fptu.sba301.ats.service.InterviewScoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static fptu.sba301.ats.constant.AppConstant.BASE_URL;

@RestController
@RequestMapping(BASE_URL + "/interviews/{interviewId}/scores")
@RequiredArgsConstructor
public class InterviewScoreController {

    private final InterviewScoreService scoreService;

    @PostMapping
    @PreAuthorize("hasAuthority('INTERVIEW_FEEDBACK')")
    public ResponseEntity<InterviewScoresResponse> submit(
            @PathVariable java.util.UUID interviewId,
            @Valid @RequestBody SubmitInterviewScoresRequest request) {
        String email = SecurityUtils.getCurrentUserEmail();
        return ResponseEntity.ok(scoreService.submitScores(interviewId, request, email));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('INTERVIEW_VIEW')")
    public ResponseEntity<List<InterviewScoresResponse>> getAll(@PathVariable java.util.UUID interviewId) {
        return ResponseEntity.ok(scoreService.getAllScores(interviewId));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('INTERVIEW_FEEDBACK')")
    public ResponseEntity<InterviewScoresResponse> getMy(@PathVariable java.util.UUID interviewId) {
        String email = SecurityUtils.getCurrentUserEmail();
        return ResponseEntity.ok(scoreService.getMyScores(interviewId, email));
    }
}