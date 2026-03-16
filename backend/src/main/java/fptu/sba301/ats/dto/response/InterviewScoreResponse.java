package fptu.sba301.ats.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewScoreResponse {
    private UUID id;
    private UUID interviewId;
    private UUID interviewerId;
    private String interviewerName;
    private UUID criterionId;
    private String criterionName;
    private int score;
    private String comment;
}
