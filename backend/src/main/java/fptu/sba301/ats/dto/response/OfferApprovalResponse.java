package fptu.sba301.ats.dto.response;

import fptu.sba301.ats.enums.ApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfferApprovalResponse {
    private UUID id;
    private UUID offerId;
    private String approvedByName;
    private ApprovalStatus status;
    private String comment;
    private Instant approvedAt;
}
