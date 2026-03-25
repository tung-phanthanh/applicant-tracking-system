package fptu.sba301.ats.service;

import fptu.sba301.ats.dto.response.InterviewScorecardResponse;

import java.util.List;
import java.util.UUID;

public interface InterviewService {
    List<InterviewScorecardResponse> getAllScorecards(UUID interviewId);
}
