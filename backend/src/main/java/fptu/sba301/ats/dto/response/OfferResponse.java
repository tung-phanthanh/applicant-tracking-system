package fptu.sba301.ats.dto.response;

import fptu.sba301.ats.enums.OfferStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfferResponse {
    private UUID id;
    private UUID applicationId;
    private String candidateName;
    private String positionTitle;
    private BigDecimal salary;
    private Integer equity;
    private BigDecimal signOnBonus;
    private LocalDate startDate;
    private LocalDate expiryDate;
    private OfferStatus status;
    private String createdByName;
    private List<OfferApprovalResponse> approvals;
}
