package fptu.sba301.ats.service;

import fptu.sba301.ats.dto.request.SubmitInterviewScoresRequest;
import fptu.sba301.ats.dto.response.InterviewScoresResponse;

import java.util.List;

public interface InterviewScoreService {

    InterviewScoresResponse submitScores(java.util.UUID interviewId,
            SubmitInterviewScoresRequest request,
            String interviewerEmail);

    List<InterviewScoresResponse> getAllScores(java.util.UUID interviewId);

    InterviewScoresResponse getMyScores(java.util.UUID interviewId, String interviewerEmail);
}
