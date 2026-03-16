package fptu.sba301.ats.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
public class CreateScorecardTemplateRequest {

    @NotBlank(message = "Template name is required")
    private String name;

    private UUID departmentId;

    @Valid
    private List<CriterionDto> criteria;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CriterionDto {
        @NotBlank(message = "Criterion name is required")
        private String name;
        private BigDecimal weight;
    }
}
