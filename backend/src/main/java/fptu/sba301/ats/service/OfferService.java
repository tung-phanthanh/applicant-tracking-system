package fptu.sba301.ats.service;

import fptu.sba301.ats.dto.request.CreateOfferRequest;
import fptu.sba301.ats.dto.request.OfferApprovalRequest;
import fptu.sba301.ats.dto.response.OfferResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OfferService {
    OfferResponse create(CreateOfferRequest request);
    OfferResponse update(UUID id, CreateOfferRequest request);
    OfferResponse getById(UUID id);
    OfferResponse getByApplication(UUID applicationId);
    OfferResponse submitForApproval(UUID id);
    Page<OfferResponse> getPendingApprovals(Pageable pageable);
    OfferResponse processApproval(UUID id, OfferApprovalRequest request, String approverEmail);
}
