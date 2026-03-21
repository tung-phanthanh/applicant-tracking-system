package fptu.sba301.ats.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record ScorecardCriterionResponse(
        UUID id,
        String name,
        BigDecimal weight,
        Integer maxScore) {
}
