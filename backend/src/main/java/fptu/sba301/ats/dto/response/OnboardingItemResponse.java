package fptu.sba301.ats.dto.response;

import fptu.sba301.ats.enums.ChecklistItemStatus;

import java.time.Instant;
import java.time.LocalDate;

public record OnboardingItemResponse(
        Long id,
        String title,
        String description,
        ChecklistItemStatus status,
        Long assignedTo,
        LocalDate dueDate,
        Instant completedAt,
        Integer displayOrder) {
}
