package fptu.sba301.ats.service;

import fptu.sba301.ats.dto.request.ApprovalDecisionRequest;
import fptu.sba301.ats.dto.request.CreateOfferRequest;
import fptu.sba301.ats.dto.request.UpdateOfferRequest;
import fptu.sba301.ats.dto.response.OfferResponse;
import fptu.sba301.ats.enums.OfferStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OfferService {

    OfferResponse create(CreateOfferRequest request, String creatorEmail);

    OfferResponse getById(java.util.UUID id);

    OfferResponse update(java.util.UUID id, UpdateOfferRequest request, String updaterEmail);

    Page<OfferResponse> getAll(OfferStatus status, Pageable pageable);

    void submitForApproval(java.util.UUID offerId, String submitterEmail);

    void approve(java.util.UUID offerId, ApprovalDecisionRequest request, String approverEmail);

    void reject(java.util.UUID offerId, ApprovalDecisionRequest request, String approverEmail);

    void candidateAccept(java.util.UUID offerId, String candidateEmail);

    void candidateReject(java.util.UUID offerId, String requestNotes, String candidateEmail);

    byte[] generatePdf(java.util.UUID offerId);
}