package fptu.sba301.ats.dto.response;

import java.time.Instant;
import java.util.List;

public record OnboardingChecklistResponse(
        Long id,
        Long applicationId,
        String candidateName,
        Instant createdAt,
        int totalItems,
        int completedItems,
        List<OnboardingItemResponse> items) {
}
