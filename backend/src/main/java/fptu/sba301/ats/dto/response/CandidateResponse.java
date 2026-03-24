package fptu.sba301.ats.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class CandidateResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String currentCompany;
    private Instant createdAt;
}
