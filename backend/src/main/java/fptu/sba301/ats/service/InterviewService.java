package fptu.sba301.ats.service;

import fptu.sba301.ats.dto.request.SubmitInterviewScoreRequest;
import fptu.sba301.ats.dto.response.InterviewScorecardResponse;

import java.util.List;
import java.util.UUID;

public interface InterviewService {
    void submitScores(UUID interviewId, SubmitInterviewScoreRequest request, String userEmail);
    InterviewScorecardResponse getMyScorecard(UUID interviewId, String userEmail);
    List<InterviewScorecardResponse> getAllScorecards(UUID interviewId);
}
