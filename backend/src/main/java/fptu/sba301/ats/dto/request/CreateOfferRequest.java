package fptu.sba301.ats.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateOfferRequest(

        @NotNull(message = "Application ID is required") java.util.UUID applicationId,

        @NotNull(message = "Salary is required") @DecimalMin(value = "0", inclusive = true, message = "Salary must be non-negative") BigDecimal salary,

        @NotBlank(message = "Position title is required") @Size(max = 255, message = "Position title must not exceed 255 characters") String positionTitle,

        @NotNull(message = "Start date is required") @FutureOrPresent(message = "Start date must be today or in the future") LocalDate startDate,

        @Future(message = "Expiry date must be in the future") LocalDate expiryDate,

        @Size(max = 2000, message = "Notes must not exceed 2000 characters") String notes) {

    @AssertTrue(message = "Expiry date must be after start date")
    public boolean isExpiryAfterStart() {
        if (expiryDate == null || startDate == null)
            return true;
        return expiryDate.isAfter(startDate);
    }
}