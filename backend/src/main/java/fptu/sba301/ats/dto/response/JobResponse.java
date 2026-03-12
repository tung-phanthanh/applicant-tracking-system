package fptu.sba301.ats.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class JobResponse {
    private Long id;
    private String title;
    private String description;
    private Long departmentId;
    private Long hiringManagerId;
    private String status;
    private Integer headcount;
    private Instant createdAt;
    private Instant updatedAt;
}
