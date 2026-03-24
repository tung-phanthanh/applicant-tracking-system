package fptu.sba301.ats.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateScorecardCriterionRequest(

        @NotBlank(message = "Criterion name is required") @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters") String name,

        @NotNull(message = "Weight is required") @DecimalMin(value = "0.01", message = "Weight must be at least 0.01") @DecimalMax(value = "1.00", message = "Weight must not exceed 1.00") BigDecimal weight,

        @NotNull(message = "Max score is required") @Min(value = 1, message = "Max score must be at least 1") @Max(value = 10, message = "Max score must not exceed 10") Integer maxScore) {
}
