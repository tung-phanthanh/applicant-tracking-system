package fptu.sba301.ats.controller;

import fptu.sba301.ats.dto.request.SubmitFeedbackRequest;
import fptu.sba301.ats.dto.response.InterviewResponse;
import fptu.sba301.ats.entity.Interview;
import fptu.sba301.ats.service.InterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<InterviewResponse>> getAllInterviews() {
        return ResponseEntity.ok(interviewService.getAllInterviews());
    }
    
    @PostMapping("/feedback")
    public ResponseEntity<String> submitFeedback(
            @RequestBody SubmitFeedbackRequest request
    ) {
        interviewService.submitFeedback(request);
        return ResponseEntity.ok("Feedback submitted successfully");
    }
    
    @GetMapping("/{interviewId}/final-score")
    public ResponseEntity<BigDecimal> getFinalScore(
            @PathVariable UUID interviewId
    ) {
        Interview interview = interviewService.getInterviewById(interviewId);
        BigDecimal result = interviewService.calculateFinalScore(interview);
        return ResponseEntity.ok(result);
    }
}