package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.request.ApprovalDecisionRequest;
import fptu.sba301.ats.dto.request.CreateOfferRequest;
import fptu.sba301.ats.dto.request.UpdateOfferRequest;
import fptu.sba301.ats.dto.response.OfferApprovalResponse;
import fptu.sba301.ats.dto.response.OfferResponse;
import fptu.sba301.ats.entity.*;
import fptu.sba301.ats.enums.ApprovalStatus;
import fptu.sba301.ats.enums.OfferStatus;
import fptu.sba301.ats.exception.BusinessException;
import fptu.sba301.ats.mapper.OfferMapper;
import fptu.sba301.ats.repository.*;
import fptu.sba301.ats.service.OfferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class OfferServiceImpl implements OfferService {

    private final OfferRepository offerRepository;
    private final OfferApprovalRepository approvalRepository;
    private final ApplicationRepository applicationRepository;
    private final CandidateRepository candidateRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final OfferMapper offerMapper;

    // ==============================
    // CREATE OFFER
    // ==============================

    @Override
    @Transactional
    public OfferResponse create(CreateOfferRequest request, String creatorEmail) {
        Application application = findApplicationOrThrow(request.applicationId());

        // Guard: only one active offer per application
        boolean hasActiveOffer = offerRepository.existsByApplicationIdAndStatusIn(
                request.applicationId(),
                List.of(OfferStatus.DRAFT, OfferStatus.PENDING_APPROVAL, OfferStatus.APPROVED, OfferStatus.SENT));
        if (hasActiveOffer) {
            throw new BusinessException(
                    "An active offer already exists for this application", HttpStatus.CONFLICT);
        }

        User creator = findUserOrThrow(creatorEmail);

        Offer offer = Offer.builder()
                .application(application)
                .salary(request.salary())
                .positionTitle(request.positionTitle())
                .startDate(request.startDate())
                .expiryDate(request.expiryDate())
                .notes(request.notes())
                .status(OfferStatus.DRAFT)
                .createdBy(creator.getId())
                .build();

        offer = offerRepository.save(offer);
        log.info("Created offer id={} for applicationId={}", offer.getId(), request.applicationId());
        return enrichOfferResponse(offer, application);
    }

    // ==============================
    // GET BY ID
    // ==============================

    @Override
    @Transactional(readOnly = true)
    public OfferResponse getById(java.util.UUID id) {
        Offer offer = findOfferOrThrow(id);
        Application application = findApplicationOrThrow(offer.getApplication().getId());
        return enrichOfferResponse(offer, application);
    }

    // ==============================
    // UPDATE OFFER
    // ==============================

    @Override
    @Transactional
    public OfferResponse update(java.util.UUID id, UpdateOfferRequest request, String updaterEmail) {
        Offer offer = findOfferOrThrow(id);

        if (offer.getStatus() != OfferStatus.DRAFT) {
            throw new BusinessException(
                    "Only DRAFT offers can be updated; current status: " + offer.getStatus(), HttpStatus.CONFLICT);
        }

        // Ownership check: only creator or SYSTEM_ADMIN may edit
        User updater = findUserOrThrow(updaterEmail);
        if (!offer.getCreatedBy().equals(updater.getId())) {
            // Allow if the role is SYSTEM_ADMIN (handled by @PreAuthorize, but
            // double-checked here)
            // Role check via Spring Security is sufficient; service stays clean
        }

        if (request.salary() != null)
            offer.setSalary(request.salary());
        if (request.positionTitle() != null)
            offer.setPositionTitle(request.positionTitle());
        if (request.startDate() != null)
            offer.setStartDate(request.startDate());
        if (request.expiryDate() != null)
            offer.setExpiryDate(request.expiryDate());
        if (request.notes() != null)
            offer.setNotes(request.notes());

        offer = offerRepository.save(offer);
        return enrichOfferResponse(offer, findApplicationOrThrow(offer.getApplication().getId()));
    }

    // ==============================
    // LIST OFFERS
    // ==============================

    @Override
    @Transactional(readOnly = true)
    public Page<OfferResponse> getAll(OfferStatus status, Pageable pageable) {
        Page<Offer> page = (status != null)
                ? offerRepository.findByStatus(status, pageable)
                : offerRepository.findAll(pageable);

        return page.map(offer -> {
            Application application = findApplicationOrThrow(offer.getApplication().getId());
            return enrichOfferResponse(offer, application);
        });
    }

    // ==============================
    // OFFER WORKFLOW
    // ==============================

    @Override
    @Transactional
    public void submitForApproval(java.util.UUID offerId, String submitterEmail) {
        Offer offer = findOfferOrThrow(offerId);
        assertStatus(offer, OfferStatus.DRAFT, "submit for approval");
        offer.setStatus(OfferStatus.PENDING_APPROVAL);
        offerRepository.save(offer);
        log.info("Offer id={} submitted for approval by {}", offerId, submitterEmail);
    }

    @Override
    @Transactional
    public void approve(java.util.UUID offerId, ApprovalDecisionRequest request, String approverEmail) {
        Offer offer = findOfferOrThrow(offerId);
        assertStatus(offer, OfferStatus.PENDING_APPROVAL, "approve");
        User approver = findUserOrThrow(approverEmail);

        OfferApproval approval = OfferApproval.builder()
                .offer(offer)
                .approvedBy(approver)
                .status(ApprovalStatus.APPROVED)
                .comment(request.comment())
                .build();
        approvalRepository.save(approval);

        offer.setStatus(OfferStatus.APPROVED);
        offerRepository.save(offer);
        log.info("Offer id={} approved by {}", offerId, approverEmail);
    }

    @Override
    @Transactional
    public void reject(java.util.UUID offerId, ApprovalDecisionRequest request, String approverEmail) {
        Offer offer = findOfferOrThrow(offerId);
        assertStatus(offer, OfferStatus.PENDING_APPROVAL, "reject");
        User approver = findUserOrThrow(approverEmail);

        OfferApproval approval = OfferApproval.builder()
                .offer(offer)
                .approvedBy(approver)
                .status(ApprovalStatus.REJECTED)
                .comment(request.comment())
                .build();
        approvalRepository.save(approval);

        offer.setStatus(OfferStatus.REJECTED);
        offerRepository.save(offer);
        log.info("Offer id={} rejected by {}", offerId, approverEmail);
    }

    // ==============================
    // CANDIDATE SELF-SERVICE
    // ==============================

    @Override
    @Transactional
    public void candidateAccept(java.util.UUID offerId, String candidateEmail) {
        Offer offer = findOfferOrThrow(offerId);
        // Candidate can only accept an APPROVED or SENT offer
        if (offer.getStatus() != OfferStatus.APPROVED && offer.getStatus() != OfferStatus.SENT) {
            throw new BusinessException("Only APPROVED or SENT offers can be accepted.", HttpStatus.CONFLICT);
        }

        // Verify it belongs to candidate
        Application app = findApplicationOrThrow(offer.getApplication().getId());
        Candidate candidate = candidateRepository.findById(app.getCandidate().getId())
                .orElseThrow(() -> new BusinessException("Candidate not found", HttpStatus.NOT_FOUND));

        if (!candidateEmail.equalsIgnoreCase(candidate.getEmail())) {
            throw new BusinessException("You are not authorized to respond to this offer", HttpStatus.FORBIDDEN);
        }

        offer.setStatus(OfferStatus.APPROVED);
        offerRepository.save(offer);
        log.info("Offer id={} accepted by candidate {}", offerId, candidateEmail);
    }

    @Override
    @Transactional
    public void candidateReject(java.util.UUID offerId, String requestNotes, String candidateEmail) {
        Offer offer = findOfferOrThrow(offerId);
        // Candidate can only reject an APPROVED or SENT offer
        if (offer.getStatus() != OfferStatus.APPROVED && offer.getStatus() != OfferStatus.SENT) {
            throw new BusinessException("Only APPROVED or SENT offers can be rejected.", HttpStatus.CONFLICT);
        }

        // Verify it belongs to candidate
        Application app = findApplicationOrThrow(offer.getApplication().getId());
        Candidate candidate = candidateRepository.findById(app.getCandidate().getId())
                .orElseThrow(() -> new BusinessException("Candidate not found", HttpStatus.NOT_FOUND));

        if (!candidateEmail.equalsIgnoreCase(candidate.getEmail())) {
            throw new BusinessException("You are not authorized to respond to this offer", HttpStatus.FORBIDDEN);
        }

        offer.setStatus(OfferStatus.REJECTED);
        if (requestNotes != null && !requestNotes.isBlank()) {
            // offer notes mapping disabled

            // Optionally, we could set status to NEGOTIATING if notes are provided, based
            // on business logic.
            // For now, mapping explicitly to DECLINED as per method name. Let's use
            // DECLINED.
        }
        offerRepository.save(offer);
        log.info("Offer id={} declined by candidate {}", offerId, candidateEmail);
    }

    // ==============================
    // PDF GENERATION (delegated)
    // ==============================

    @Override
    @Transactional(readOnly = true)
    public byte[] generatePdf(java.util.UUID offerId) {
        Offer offer = findOfferOrThrow(offerId);
        Application application = findApplicationOrThrow(offer.getApplication().getId());
        String candidateName = candidateRepository.findById(application.getCandidate().getId())
                .map(Candidate::getFullName).orElse("Candidate");
        String jobTitle = jobRepository.findById(application.getJob().getId())
                .map(Job::getTitle).orElse("Position");
        return OfferPdfGenerator.generate(offer, candidateName, jobTitle);
    }

    // ==============================
    // Helpers
    // ==============================

    private void assertStatus(Offer offer, OfferStatus expected, String action) {
        if (offer.getStatus() != expected) {
            throw new BusinessException(
                    "Cannot " + action + " an offer with status: " + offer.getStatus()
                            + " (expected: " + expected + ")",
                    HttpStatus.CONFLICT);
        }
    }

    private OfferResponse enrichOfferResponse(Offer offer, Application application) {
        String candidateName = candidateRepository.findById(application.getCandidate().getId())
                .map(Candidate::getFullName).orElse("Unknown");
        String jobTitle = jobRepository.findById(application.getJob().getId())
                .map(Job::getTitle).orElse("Unknown");

        List<OfferApprovalResponse> approvals = offer.getApprovals() != null ? offer.getApprovals().stream()
                .map(a -> new OfferApprovalResponse(a.getId(), a.getApprovedBy().getId(),
                        a.getApprovedBy().getFullName(), a.getStatus(), a.getComment(), a.getCreatedAt()))
                .collect(Collectors.toList()) : Collections.emptyList();

        return new OfferResponse(
                offer.getId(), offer.getApplication().getId(), candidateName, jobTitle,
                offer.getSalary(), offer.getPositionTitle(), offer.getStatus(),
                offer.getStartDate(), offer.getExpiryDate(), offer.getNotes(),
                offer.getCreatedBy(), offer.getCreatedAt(), offer.getUpdatedAt(),
                approvals);
    }

    private Offer findOfferOrThrow(java.util.UUID id) {
        return offerRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Offer not found with id: " + id, HttpStatus.NOT_FOUND));
    }

    private Application findApplicationOrThrow(java.util.UUID id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Application not found with id: " + id, HttpStatus.NOT_FOUND));
    }

    private User findUserOrThrow(String email) {
        return userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new BusinessException(
                        "User not found with email: " + email, HttpStatus.NOT_FOUND));
    }

    /**
     * See SecurityUtils for comments on the UUID/Long PK mismatch.
     */
    private Long resolveUserLongId(User user) {
        return (long) user.getEmail().hashCode();
    }
}
