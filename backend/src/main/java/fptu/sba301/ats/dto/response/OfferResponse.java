package fptu.sba301.ats.dto.response;

import fptu.sba301.ats.enums.OfferStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record OfferResponse(
        Long id,
        Long applicationId,
        String candidateName,
        String jobTitle,
        BigDecimal salary,
        String positionTitle,
        OfferStatus status,
        LocalDate startDate,
        LocalDate expiryDate,
        String notes,
        Long createdBy,
        Instant createdAt,
        Instant updatedAt,
        List<OfferApprovalResponse> approvals) {
}
