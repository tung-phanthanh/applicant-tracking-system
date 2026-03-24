package fptu.sba301.ats.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateOnboardingChecklistRequest(

        @NotEmpty(message = "Checklist must contain at least one item") List<@Valid OnboardingItemRequest> items) {
}
