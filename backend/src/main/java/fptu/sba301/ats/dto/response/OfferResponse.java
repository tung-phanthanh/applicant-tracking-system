package fptu.sba301.ats.dto.response;

import fptu.sba301.ats.enums.OfferStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

import java.util.UUID;

public record OfferResponse(
                UUID id,
                UUID applicationId,
                String candidateName,
                String jobTitle,
                BigDecimal salary,
                String positionTitle,
                OfferStatus status,
                LocalDate startDate,
                LocalDate expiryDate,
                String notes,
                UUID createdBy,
                LocalDateTime createdAt,
                LocalDateTime updatedAt,
                List<OfferApprovalResponse> approvals) {
}