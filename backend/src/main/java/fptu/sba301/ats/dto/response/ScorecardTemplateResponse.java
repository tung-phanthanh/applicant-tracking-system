package fptu.sba301.ats.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScorecardTemplateResponse {
    private UUID id;
    private String name;
    private UUID departmentId;
    private String departmentName;
    private LocalDateTime createdAt;
    private List<CriterionResponse> criteria;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CriterionResponse {
        private UUID id;
        private String name;
        private BigDecimal weight;
    }
}
