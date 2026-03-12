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

    OfferResponse getById(Long id);

    OfferResponse update(Long id, UpdateOfferRequest request, String updaterEmail);

    Page<OfferResponse> getAll(OfferStatus status, Pageable pageable);

    void submitForApproval(Long offerId, String submitterEmail);

    void approve(Long offerId, ApprovalDecisionRequest request, String approverEmail);

    void reject(Long offerId, ApprovalDecisionRequest request, String approverEmail);

    void candidateAccept(Long offerId, String candidateEmail);

    void candidateReject(Long offerId, String requestNotes, String candidateEmail);

    byte[] generatePdf(Long offerId);
}
