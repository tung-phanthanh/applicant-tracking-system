package fptu.sba301.ats.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class InterviewResponse {
    private UUID id;
    private Instant scheduledAt;
    private Instant startedAt;
    private Instant endedAt;
    private String location;
    private String status;
}
