package fptu.sba301.ats.dto.response;

import java.math.BigDecimal;

public record ScorecardCriterionResponse(
        Long id,
        String name,
        BigDecimal weight,
        Integer maxScore) {
}
