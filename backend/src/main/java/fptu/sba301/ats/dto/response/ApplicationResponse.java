package fptu.sba301.ats.dto.response;

import fptu.sba301.ats.enums.ApplicationStage;
import fptu.sba301.ats.enums.ApplicationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ApplicationResponse {
    private Long id;
    private Long candidateId;
    private Long jobId;
    private ApplicationStage stage;
    private ApplicationStatus status;
    private Instant appliedAt;
    private Instant updatedAt;

    // Additional fields for convenience
    private String candidateName;
    private String jobTitle;
}
