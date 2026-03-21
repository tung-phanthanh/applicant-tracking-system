package fptu.sba301.ats.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OnboardingChecklistResponse(
        UUID id,
        UUID applicationId,
        String candidateName,
        Instant createdAt,
        int totalItems,
        int completedItems,
        List<OnboardingItemResponse> items) {
}
