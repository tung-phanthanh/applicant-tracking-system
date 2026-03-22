package fptu.sba301.ats.controller;

import fptu.sba301.ats.dto.request.CreateOnboardingChecklistRequest;
import fptu.sba301.ats.dto.request.UpdateOnboardingItemRequest;
import fptu.sba301.ats.dto.response.OnboardingChecklistResponse;
import fptu.sba301.ats.dto.response.OnboardingItemResponse;
import fptu.sba301.ats.service.OnboardingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static fptu.sba301.ats.constant.AppConstant.BASE_URL;

@RestController
@RequestMapping(BASE_URL + "/onboarding")
@RequiredArgsConstructor
public class OnboardingController {

    private final OnboardingService onboardingService;

    @PostMapping("/{applicationId}/checklist")
    @PreAuthorize("hasAuthority('ONBOARDING_MANAGE')")
    public ResponseEntity<OnboardingChecklistResponse> create(
            @PathVariable java.util.UUID applicationId,
            @Valid @RequestBody CreateOnboardingChecklistRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(onboardingService.createChecklist(applicationId, request));
    }

    @GetMapping("/{applicationId}/checklist")
    @PreAuthorize("hasAuthority('ONBOARDING_MANAGE')")
    public ResponseEntity<OnboardingChecklistResponse> get(@PathVariable java.util.UUID applicationId) {
        return ResponseEntity.ok(onboardingService.getChecklist(applicationId));
    }

    @PutMapping("/items/{itemId}")
    @PreAuthorize("hasAuthority('ONBOARDING_MANAGE')")
    public ResponseEntity<OnboardingItemResponse> updateItem(
            @PathVariable java.util.UUID itemId,
            @Valid @RequestBody UpdateOnboardingItemRequest request) {
        return ResponseEntity.ok(onboardingService.updateItem(itemId, request));
    }
}
