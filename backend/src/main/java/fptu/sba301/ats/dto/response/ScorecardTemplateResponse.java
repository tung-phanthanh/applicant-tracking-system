package fptu.sba301.ats.dto.response;

import java.time.Instant;
import java.util.List;

import java.util.UUID;

public record ScorecardTemplateResponse(
        UUID id,
        String name,
        java.util.UUID departmentId,
        Instant createdAt,
        List<ScorecardCriterionResponse> criteria) {
}
