package fptu.sba301.ats.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScorecardTemplateResponse {
    private UUID id;
    private String name;
    private UUID departmentId;
    private String departmentName;
    private List<CriterionResponse> criteria;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CriterionResponse {
        private UUID id;
        private String name;
        private BigDecimal weight;
    }
}
