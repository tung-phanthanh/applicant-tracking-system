package fptu.sba301.ats.dto.response;

import fptu.sba301.ats.enums.ChecklistItemStatus;

import java.util.UUID;
import java.time.Instant;
import java.time.LocalDate;
 
public record OnboardingItemResponse(
        UUID id,
        String title,
        String description,
        ChecklistItemStatus status,
        UUID assignedTo,
        LocalDate dueDate,
        Instant completedAt,
        Integer displayOrder) {
}
