package fptu.sba301.ats.service;

import fptu.sba301.ats.dto.request.SubmitInterviewScoresRequest;
import fptu.sba301.ats.dto.response.InterviewScoresResponse;

import java.util.List;

public interface InterviewScoreService {

    InterviewScoresResponse submitScores(Long interviewId,
            SubmitInterviewScoresRequest request,
            String interviewerEmail);

    List<InterviewScoresResponse> getAllScores(Long interviewId);

    InterviewScoresResponse getMyScores(Long interviewId, String interviewerEmail);
}
