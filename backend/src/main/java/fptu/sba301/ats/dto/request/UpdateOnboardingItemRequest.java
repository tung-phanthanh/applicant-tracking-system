package fptu.sba301.ats.dto.request;

import fptu.sba301.ats.enums.ChecklistItemStatus;
import jakarta.validation.constraints.Size;

import java.util.UUID;
import java.time.LocalDate;
 
public record UpdateOnboardingItemRequest(
 
        @Size(max = 255, message = "Title must not exceed 255 characters") String title,
 
        @Size(max = 1000, message = "Description must not exceed 1000 characters") String description,
 
        ChecklistItemStatus status,
 
        UUID assignedTo,
 
        LocalDate dueDate) {
}
