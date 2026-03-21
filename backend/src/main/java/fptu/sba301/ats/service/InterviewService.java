package fptu.sba301.ats.service;

import fptu.sba301.ats.dto.response.InterviewResponse;
import java.util.List;

public interface InterviewService {
    List<InterviewResponse> getAllInterviews();

    List<InterviewResponse> getUpcomingInterviews();

    InterviewResponse getInterviewById(java.util.UUID id);
}
