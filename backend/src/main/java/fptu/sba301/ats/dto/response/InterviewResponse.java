package fptu.sba301.ats.dto.response;

import fptu.sba301.ats.enums.InterviewStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class InterviewResponse {
    private java.util.UUID id;
    private java.util.UUID applicationId;
    private Instant scheduledAt;
    private String location;
    private InterviewStatus status;

    // Additional fields for convenience
    private String candidateName;
    private String jobTitle;
    private String type; // Mocking type for UI as the entity doesn't have it natively
    private String participant; // Mocking main participant for UI
}
