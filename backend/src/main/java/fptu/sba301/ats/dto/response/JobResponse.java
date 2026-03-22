package fptu.sba301.ats.dto.response;

import fptu.sba301.ats.enums.JobStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class JobResponse {
    private UUID jobId;
    private String title;
    private JobStatus status;
}
