package fptu.sba301.ats.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class JobResponse {
    private UUID id;
    private String title;
    private String description;
    private UUID departmentId;
    private UUID hiringManagerId;
    private String status;
    private Integer headcount;
    private Instant createdAt;
    private Instant updatedAt;
}
