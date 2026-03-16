package fptu.sba301.ats.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOfferRequest {

    @NotNull(message = "Application ID is required")
    private UUID applicationId;

    private BigDecimal salary;

    private Integer equity;

    private BigDecimal signOnBonus;

    private String positionTitle;

    private LocalDate startDate;

    private LocalDate expiryDate;
}
