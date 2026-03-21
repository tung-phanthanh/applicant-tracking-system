package fptu.sba301.ats.service;

import fptu.sba301.ats.dto.request.CreateOnboardingChecklistRequest;
import fptu.sba301.ats.dto.request.UpdateOnboardingItemRequest;
import fptu.sba301.ats.dto.response.OnboardingChecklistResponse;
import fptu.sba301.ats.dto.response.OnboardingItemResponse;

public interface OnboardingService {

    OnboardingChecklistResponse createChecklist(java.util.UUID applicationId,
            CreateOnboardingChecklistRequest request);

    OnboardingChecklistResponse getChecklist(java.util.UUID applicationId);

    OnboardingItemResponse updateItem(java.util.UUID itemId, UpdateOnboardingItemRequest request);
}
