package fptu.sba301.ats.dto.response;

import java.time.Instant;
import java.util.List;

public record ScorecardTemplateResponse(
        Long id,
        String name,
        Long departmentId,
        Instant createdAt,
        List<ScorecardCriterionResponse> criteria) {
}
