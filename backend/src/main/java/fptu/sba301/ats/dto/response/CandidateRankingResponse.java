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
public class CandidateRankingResponse {
    private String jobId;
    private String jobTitle;
    private List<CandidateRank> candidates;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CandidateRank {
        private int rank;
        private String applicationId;
        private String candidateId;
        private String candidateName;
        private String candidateEmail;
        private double overallScore;
        private double techScore;
        private double cultureScore;
        private String status;
        private String stage;
    }
}
