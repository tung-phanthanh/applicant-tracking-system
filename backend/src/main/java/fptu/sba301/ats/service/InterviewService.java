package fptu.sba301.ats.service;

import fptu.sba301.ats.dto.request.SubmitFeedbackRequest;
import fptu.sba301.ats.dto.response.*;
import fptu.sba301.ats.entity.Interview;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface InterviewService {
    List<InterviewResponse> getAllInterviews(String email);
    InterviewDetailResponse getInterviewDetail(UUID interviewId, String email);
    void submitFeedback(SubmitFeedbackRequest req);
    BigDecimal calculateFinalScore(Interview interview);
    Interview getInterviewById(UUID interviewId);
    ScorecardTemplateResponse getTemplateByInterviewId(UUID interviewId);
    
    CandidateEvaluationResponse getInterviewEvaluationSummary(UUID interviewId);
    ApplicationEvaluationResponse getApplicationEvaluation(UUID applicationId);
}
