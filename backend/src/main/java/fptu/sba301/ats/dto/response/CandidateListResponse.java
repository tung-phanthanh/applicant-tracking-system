package fptu.sba301.ats.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateListResponse {
    private String name;
    private String email;
    private String appliedFor;
    private String stage;
    private Double rating;
    private LocalDateTime appliedDate;
}