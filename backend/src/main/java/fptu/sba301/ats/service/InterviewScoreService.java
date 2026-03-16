package fptu.sba301.ats.service;

import fptu.sba301.ats.dto.request.SubmitInterviewScoreRequest;
import fptu.sba301.ats.dto.response.InterviewScoreResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface InterviewScoreService {
    List<InterviewScoreResponse> submitScores(SubmitInterviewScoreRequest request, String interviewerEmail);
    Page<InterviewScoreResponse> getScoresByInterview(UUID interviewId, Pageable pageable);
}
