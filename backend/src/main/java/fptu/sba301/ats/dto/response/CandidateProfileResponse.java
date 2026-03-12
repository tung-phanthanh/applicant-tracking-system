package fptu.sba301.ats.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateProfileResponse {
    private UUID id;
    private String name;
    private String email;
    private String phone;
    private String currentCompany;
    private String appliedFor;
    private String stage;
    private String status;
    private Double rating;
    private LocalDateTime appliedDate;
    
    private String source;
    private String location;
    private Integer experienceYears;
    private String summary;
    private java.util.List<CandidateDocumentDto> documents;
}
