package fptu.sba301.ats.controller;

import fptu.sba301.ats.dto.request.SubmitInterviewScoreRequest;
import fptu.sba301.ats.dto.response.InterviewScoreResponse;
import fptu.sba301.ats.service.InterviewScoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static fptu.sba301.ats.constant.AppConstant.BASE_URL;
import static fptu.sba301.ats.constant.AppConstant.INTERVIEW_SCORE_CONTROLLER_URL;

@RestController
@RequestMapping(BASE_URL + INTERVIEW_SCORE_CONTROLLER_URL)
@RequiredArgsConstructor
public class InterviewScoreController {

    private final InterviewScoreService scoreService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('INTERVIEWER', 'HR', 'HR_MANAGER')")
    public ResponseEntity<List<InterviewScoreResponse>> submitScores(
            @Valid @RequestBody SubmitInterviewScoreRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(scoreService.submitScores(request, userDetails.getUsername()));
    }

    // GET /interview-scores/interview/{id}?page=0&size=20
    @GetMapping("/interview/{interviewId}")
    @PreAuthorize("hasAnyAuthority('INTERVIEWER', 'HR', 'HR_MANAGER')")
    public ResponseEntity<Page<InterviewScoreResponse>> getByInterview(
            @PathVariable UUID interviewId,
            @PageableDefault(size = 50) Pageable pageable) {
        return ResponseEntity.ok(scoreService.getScoresByInterview(interviewId, pageable));
    }
}
