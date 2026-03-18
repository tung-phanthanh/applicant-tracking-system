package fptu.sba301.ats.controller;

import fptu.sba301.ats.dto.request.CreateOfferRequest;
import fptu.sba301.ats.dto.request.OfferApprovalRequest;
import fptu.sba301.ats.dto.response.OfferResponse;
import fptu.sba301.ats.service.OfferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static fptu.sba301.ats.constant.AppConstant.BASE_URL;
import static fptu.sba301.ats.constant.AppConstant.OFFER_CONTROLLER_URL;

@RestController
@RequestMapping(BASE_URL + OFFER_CONTROLLER_URL)
@RequiredArgsConstructor
public class OfferController {

    private final OfferService offerService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('HR', 'HR_MANAGER', 'SYSTEM_ADMIN')")
    public ResponseEntity<Page<OfferResponse>> getAll(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(offerService.getAll(pageable));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('HR', 'HR_MANAGER')")
    public ResponseEntity<OfferResponse> create(@Valid @RequestBody CreateOfferRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(offerService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('HR', 'HR_MANAGER')")
    public ResponseEntity<OfferResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody CreateOfferRequest request) {
        return ResponseEntity.ok(offerService.update(id, request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('HR', 'HR_MANAGER')")
    public ResponseEntity<OfferResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(offerService.getById(id));
    }

    @GetMapping("/application/{applicationId}")
    @PreAuthorize("hasAnyAuthority('HR', 'HR_MANAGER')")
    public ResponseEntity<OfferResponse> getByApplication(@PathVariable UUID applicationId) {
        return ResponseEntity.ok(offerService.getByApplication(applicationId));
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAnyAuthority('HR', 'HR_MANAGER')")
    public ResponseEntity<OfferResponse> submitForApproval(@PathVariable UUID id) {
        return ResponseEntity.ok(offerService.submitForApproval(id));
    }

    // GET /offers/pending-approvals?page=0&size=10
    @GetMapping("/pending-approvals")
    @PreAuthorize("hasAnyAuthority('HR_MANAGER', 'SYSTEM_ADMIN')")
    public ResponseEntity<Page<OfferResponse>> getPendingApprovals(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(offerService.getPendingApprovals(pageable));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyAuthority('HR_MANAGER', 'SYSTEM_ADMIN')")
    public ResponseEntity<OfferResponse> processApproval(
            @PathVariable UUID id,
            @Valid @RequestBody OfferApprovalRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(offerService.processApproval(id, request, userDetails.getUsername()));
    }
}
