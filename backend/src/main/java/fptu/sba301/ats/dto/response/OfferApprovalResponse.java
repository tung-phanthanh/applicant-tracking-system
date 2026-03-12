package fptu.sba301.ats.dto.response;

import fptu.sba301.ats.enums.ApprovalStatus;

import java.time.Instant;

public record OfferApprovalResponse(
        Long id,
        Long approvedBy,
        String approverName,
        ApprovalStatus status,
        String comment,
        Instant approvedAt) {
}
