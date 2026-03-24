package fptu.sba301.ats.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateEvaluationResponse {
    private UUID applicationId;
    private String candidateName;
    private String jobTitle;
    private double overallScore;
    private List<InterviewEvaluation> interviews;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InterviewEvaluation {
        private UUID interviewId;
        private Instant scheduledAt;
        private String type;
        private String status;
        private double averageScore;
        private List<InterviewScorecardResponse> scorecards;
    }
}
