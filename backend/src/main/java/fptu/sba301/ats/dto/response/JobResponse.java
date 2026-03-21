package fptu.sba301.ats.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class JobResponse {
    private java.util.UUID id;
    private String title;
    private String description;
    private java.util.UUID departmentId;
    private java.util.UUID hiringManagerId;
    private String status;
    private Integer headcount;
    private Instant createdAt;
    private Instant updatedAt;
}
