package fptu.sba301.ats.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateOfferRequest(

        @DecimalMin(value = "0", inclusive = true, message = "Salary must be non-negative") BigDecimal salary,

        @Size(max = 255, message = "Position title must not exceed 255 characters") String positionTitle,

        @FutureOrPresent(message = "Start date must be today or in the future") LocalDate startDate,

        @Future(message = "Expiry date must be in the future") LocalDate expiryDate,

        @Size(max = 2000, message = "Notes must not exceed 2000 characters") String notes) {
    @AssertTrue(message = "Expiry date must be after start date")
    public boolean isExpiryAfterStart() {
        if (expiryDate == null || startDate == null)
            return true;
        return expiryDate.isAfter(startDate);
    }
}