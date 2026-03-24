package fptu.sba301.ats.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;
import java.time.LocalDate;
 
public record OnboardingItemRequest(
 
        @NotBlank(message = "Item title is required") @Size(min = 2, max = 255, message = "Title must be between 2 and 255 characters") String title,
 
        @Size(max = 1000, message = "Description must not exceed 1000 characters") String description,
 
        UUID assignedTo,
 
        LocalDate dueDate,

        Integer displayOrder) {
}
