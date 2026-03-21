package fptu.sba301.ats.dto.response;

import fptu.sba301.ats.enums.ApplicationStage;
import fptu.sba301.ats.enums.ApplicationStatus;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;

import java.time.Instant;

@Data
@Builder
public class ApplicationResponse {
    private java.util.UUID id;
    private java.util.UUID candidateId;
    private java.util.UUID jobId;
    private ApplicationStage stage;
    private ApplicationStatus status;
    private Instant appliedAt;
    private Instant updatedAt;

    // Additional fields for convenience
    private String candidateName;
    private String jobTitle;
}
