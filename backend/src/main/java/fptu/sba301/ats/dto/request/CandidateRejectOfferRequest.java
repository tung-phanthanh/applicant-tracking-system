package fptu.sba301.ats.dto.request;

import jakarta.validation.constraints.Size;

public record CandidateRejectOfferRequest(
        @Size(max = 1000, message = "Notes must not exceed 1000 characters") String notes) {
}
