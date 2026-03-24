package fptu.sba301.ats.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class SubmitFeedbackRequest {

    private UUID interviewId;
    private UUID interviewerId;
    private Integer technicalScore;
    private Integer communicationScore;
    private Integer overallScore;
    private String feedback;
}
