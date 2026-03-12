package fptu.sba301.ats.controller;

import fptu.sba301.ats.dto.request.ApprovalDecisionRequest;
import fptu.sba301.ats.dto.request.CreateOfferRequest;
import fptu.sba301.ats.dto.request.UpdateOfferRequest;
import fptu.sba301.ats.dto.response.OfferResponse;
import fptu.sba301.ats.enums.OfferStatus;
import fptu.sba301.ats.security.SecurityUtils;
import fptu.sba301.ats.service.OfferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static fptu.sba301.ats.constant.AppConstant.BASE_URL;

@RestController
@RequestMapping(BASE_URL + "/offers")
@RequiredArgsConstructor
public class OfferController {

    private final OfferService offerService;

    @PostMapping
    @PreAuthorize("hasAnyRole('HR', 'SYSTEM_ADMIN')")
    public ResponseEntity<OfferResponse> create(
            @Valid @RequestBody CreateOfferRequest request) {
        String email = SecurityUtils.getCurrentUserEmail();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(offerService.create(request, email));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR', 'HR_MANAGER', 'SYSTEM_ADMIN')")
    public ResponseEntity<OfferResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(offerService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR', 'SYSTEM_ADMIN')")
    public ResponseEntity<OfferResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOfferRequest request) {
        String email = SecurityUtils.getCurrentUserEmail();
        return ResponseEntity.ok(offerService.update(id, request, email));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('HR', 'HR_MANAGER', 'SYSTEM_ADMIN')")
    public ResponseEntity<Page<OfferResponse>> getAll(
            @RequestParam(required = false) OfferStatus status,
            Pageable pageable) {
        return ResponseEntity.ok(offerService.getAll(status, pageable));
    }

    @PostMapping("/{offerId}/submit")
    @PreAuthorize("hasAnyRole('HR', 'SYSTEM_ADMIN')")
    public ResponseEntity<Void> submit(@PathVariable Long offerId) {
        String email = SecurityUtils.getCurrentUserEmail();
        offerService.submitForApproval(offerId, email);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{offerId}/approve")
    @PreAuthorize("hasAnyRole('HR_MANAGER', 'SYSTEM_ADMIN')")
    public ResponseEntity<Void> approve(
            @PathVariable Long offerId,
            @Valid @RequestBody ApprovalDecisionRequest request) {
        String email = SecurityUtils.getCurrentUserEmail();
        offerService.approve(offerId, request, email);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{offerId}/reject")
    @PreAuthorize("hasAnyRole('HR_MANAGER', 'SYSTEM_ADMIN')")
    public ResponseEntity<Void> reject(
            @PathVariable Long offerId,
            @Valid @RequestBody ApprovalDecisionRequest request) {
        String email = SecurityUtils.getCurrentUserEmail();
        offerService.reject(offerId, request, email);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{offerId}/accept")
    @PreAuthorize("hasAuthority('CANDIDATE')")
    public ResponseEntity<Void> candidateAccept(@PathVariable Long offerId) {
        String email = SecurityUtils.getCurrentUserEmail();
        offerService.candidateAccept(offerId, email);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{offerId}/decline")
    @PreAuthorize("hasAuthority('CANDIDATE')")
    public ResponseEntity<Void> candidateReject(
            @PathVariable Long offerId,
            @Valid @RequestBody(required = false) fptu.sba301.ats.dto.request.CandidateRejectOfferRequest request) {
        String email = SecurityUtils.getCurrentUserEmail();
        String notes = (request != null) ? request.notes() : null;
        offerService.candidateReject(offerId, notes, email);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{offerId}/preview")
    @PreAuthorize("hasAnyRole('HR', 'HR_MANAGER', 'SYSTEM_ADMIN')")
    public ResponseEntity<byte[]> preview(@PathVariable Long offerId) {
        byte[] pdf = offerService.generatePdf(offerId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"offer-" + offerId + ".pdf\"")
                .body(pdf);
    }
}
