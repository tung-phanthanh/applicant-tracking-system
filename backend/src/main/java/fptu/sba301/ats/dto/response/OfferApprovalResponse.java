package fptu.sba301.ats.dto.response;

import fptu.sba301.ats.enums.ApprovalStatus;

import java.time.LocalDateTime;

import java.util.UUID;

public record OfferApprovalResponse(
        UUID id,
        UUID approvedBy,
        String approverName,
        ApprovalStatus status,
        String comment,
        LocalDateTime approvedAt) {
}