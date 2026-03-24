package fptu.sba301.ats.dto.request;

import jakarta.validation.constraints.*;

public record SubmitScoreItemRequest(

        @NotNull(message = "Criterion ID is required") java.util.UUID criterionId,

        @NotNull(message = "Score is required") @Min(value = 1, message = "Score must be at least 1") @Max(value = 5, message = "Score must not exceed 5") Integer score,

        @Size(max = 1000, message = "Comment must not exceed 1000 characters") String comment) {
}
