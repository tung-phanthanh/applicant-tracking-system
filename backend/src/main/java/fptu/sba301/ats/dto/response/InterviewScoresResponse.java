package fptu.sba301.ats.dto.response;

import java.time.Instant;
import java.util.List;

public record InterviewScoresResponse(
                java.util.UUID interviewId,
                java.util.UUID interviewerId,
                String interviewerName,
                String overallComment,
                String strengths,
                String weaknesses,
                fptu.sba301.ats.enums.Recommendation recommendation,
                Instant submittedAt,
                List<InterviewScoreItemResponse> scores) {
}
