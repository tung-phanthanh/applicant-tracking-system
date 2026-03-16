package fptu.sba301.ats.dto.request;

import fptu.sba301.ats.enums.ApprovalStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfferApprovalRequest {

    @NotNull(message = "Status is required")
    private ApprovalStatus status;

    private String comment;
}
