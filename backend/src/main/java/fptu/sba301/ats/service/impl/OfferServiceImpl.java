package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.request.CreateOfferRequest;
import fptu.sba301.ats.dto.request.OfferApprovalRequest;
import fptu.sba301.ats.dto.response.OfferResponse;
import fptu.sba301.ats.entity.Application;
import fptu.sba301.ats.entity.Offer;
import fptu.sba301.ats.entity.OfferApproval;
import fptu.sba301.ats.entity.User;
import fptu.sba301.ats.enums.ApprovalStatus;
import fptu.sba301.ats.enums.OfferStatus;
import fptu.sba301.ats.exception.BusinessException;
import fptu.sba301.ats.mapper.OfferMapper;
import fptu.sba301.ats.repository.ApplicationRepository;
import fptu.sba301.ats.repository.OfferApprovalRepository;
import fptu.sba301.ats.repository.OfferRepository;
import fptu.sba301.ats.repository.UserRepository;
import fptu.sba301.ats.service.OfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OfferServiceImpl implements OfferService {

    private final OfferRepository offerRepository;
    private final OfferApprovalRepository approvalRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final OfferMapper offerMapper;

    @Override
    public OfferResponse create(CreateOfferRequest request) {
        Application application = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new BusinessException("Application not found: " + request.getApplicationId(), HttpStatus.NOT_FOUND));
        
        Offer offer = offerMapper.toEntity(request);
        offer.setApplication(application);
        offer.setStatus(OfferStatus.DRAFT);
        return offerMapper.toResponse(offerRepository.save(offer));
    }

    @Override
    public OfferResponse update(UUID id, CreateOfferRequest request) {
        Offer offer = findOfferOrThrow(id);

        if (offer.getStatus() != OfferStatus.DRAFT) {
            throw new BusinessException("Only DRAFT offers can be edited", HttpStatus.CONFLICT);
        }

        offerMapper.updateFromRequest(request, offer);
        return offerMapper.toResponse(offerRepository.save(offer));
    }

    @Override
    @Transactional(readOnly = true)
    public OfferResponse getById(UUID id) {
        return offerMapper.toResponse(findOfferOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public OfferResponse getByApplication(UUID applicationId) {
        Offer offer = offerRepository.findByApplicationId(applicationId)
                .orElseThrow(() -> new BusinessException("No offer found for application: " + applicationId, HttpStatus.NOT_FOUND));
        return offerMapper.toResponse(offer);
    }

    @Override
    public OfferResponse submitForApproval(UUID id) {
        Offer offer = findOfferOrThrow(id);

        if (offer.getStatus() != OfferStatus.DRAFT) {
            throw new BusinessException("Only DRAFT offers can be submitted for approval", HttpStatus.CONFLICT);
        }

        offer.setStatus(OfferStatus.PENDING_APPROVAL);
        return offerMapper.toResponse(offerRepository.save(offer));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OfferResponse> getPendingApprovals(Pageable pageable) {
        return offerRepository.findByStatus(OfferStatus.PENDING_APPROVAL, pageable)
                .map(offerMapper::toResponse);
    }

    @Override
    public OfferResponse processApproval(UUID id, OfferApprovalRequest request, String approverEmail) {
        Offer offer = findOfferOrThrow(id);

        if (offer.getStatus() != OfferStatus.PENDING_APPROVAL) {
            throw new BusinessException("Offer is not pending approval", HttpStatus.CONFLICT);
        }

        User approver = userRepository.findByEmail(approverEmail)
                .orElseThrow(() -> new BusinessException("Approver not found: " + approverEmail, HttpStatus.NOT_FOUND));

        OfferApproval approval = OfferApproval.builder()
                .offer(offer)
                .approvedBy(approver)
                .status(request.getStatus())
                .comment(request.getComment())
                .approvedAt(Instant.now())
                .build();
        approvalRepository.save(approval);

        offer.setStatus(request.getStatus() == ApprovalStatus.APPROVED
                ? OfferStatus.APPROVED
                : OfferStatus.REJECTED);

        return offerMapper.toResponse(offerRepository.save(offer));
    }

    // ── helpers ────────────────────────────────────────────────────────────

    private Offer findOfferOrThrow(UUID id) {
        return offerRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Offer not found: " + id, HttpStatus.NOT_FOUND));
    }
}
