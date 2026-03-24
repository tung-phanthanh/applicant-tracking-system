package fptu.sba301.ats.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateScorecardTemplateRequest(

        @NotBlank(message = "Template name is required") @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters") String name,

        java.util.UUID departmentId) {
}
