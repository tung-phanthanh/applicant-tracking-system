package fptu.sba301.ats.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateEvaluationResponse {
    private String applicationId;
    private String candidateId;
    private String candidateName;
    private String candidateEmail;
    private String positionTitle;
    private String jobId;
    private double overallScore;
    private String recommendation;
    private int interviewsCompleted;
    private int interviewsTotal;
    private List<StageEvaluation> stages;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StageEvaluation {
        private String interviewId;
        private String stageName;
        private String status;
        private Double score;
        private String interviewerName;
        private String date;
        private String feedback;
    }
}
