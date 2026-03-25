package fptu.sba301.ats.controller;

import fptu.sba301.ats.dto.request.SubmitFeedbackRequest;
import fptu.sba301.ats.dto.response.*;
import fptu.sba301.ats.entity.Interview;
import fptu.sba301.ats.service.InterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static fptu.sba301.ats.constant.AppConstant.*;

@RestController
@RequestMapping(BASE_URL + INTERVIEW_CONTROLLER_URL)
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    @GetMapping
    @PreAuthorize("hasAnyRole('INTERVIEWER')")
    public ResponseEntity<List<InterviewResponse>> getAllInterviews(Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(interviewService.getAllInterviews(email));
    }

    @PostMapping("/feedback")
    @PreAuthorize("hasAnyRole('INTERVIEWER')")
    public ResponseEntity<String> submitFeedback(
            @RequestBody SubmitFeedbackRequest request
    ) {
        interviewService.submitFeedback(request);
        return ResponseEntity.ok("Feedback submitted successfully");
    }

    @GetMapping("/{interviewId}")
    @PreAuthorize("hasAnyRole('INTERVIEWER')")
    public ResponseEntity<InterviewDetailResponse> getInterviewDetail(
            @PathVariable UUID interviewId,
            Authentication authentication
    ) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(interviewService.getInterviewDetail(interviewId, email));
    }

    @GetMapping("/{interviewId}/final-score")
    @PreAuthorize("hasAnyRole('INTERVIEWER', 'HR', 'HR_MANAGER', 'SYSTEM_ADMIN')")
    public ResponseEntity<BigDecimal> getFinalScore(
            @PathVariable UUID interviewId
    ) {
        Interview interview = interviewService.getInterviewById(interviewId);
        BigDecimal result = interviewService.calculateFinalScore(interview);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{interviewId}/template")
    @PreAuthorize("hasAnyRole('INTERVIEWER')")
    public ResponseEntity<ScorecardTemplateResponse> getInterviewTemplate(
            @PathVariable UUID interviewId
    ) {
        return ResponseEntity.ok(interviewService.getTemplateByInterviewId(interviewId));
    }

    @GetMapping("/{interviewId}/evaluation")
    @PreAuthorize("hasAnyRole('INTERVIEWER', 'HR', 'HR_MANAGER', 'SYSTEM_ADMIN')")
    public ResponseEntity<InterviewEvaluationDetailResponse> getCandidateEvaluation(
            @PathVariable UUID interviewId
    ) {
        return ResponseEntity.ok(interviewService.getInterviewEvaluationSummary(interviewId));
    }

    @GetMapping("/applications/{applicationId}/evaluation")
    @PreAuthorize("hasAnyRole('HR', 'HR_MANAGER', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApplicationEvaluationResponse> getApplicationEvaluation(
            @PathVariable UUID applicationId
    ) {
        return ResponseEntity.ok(interviewService.getApplicationEvaluation(applicationId));
    }
}