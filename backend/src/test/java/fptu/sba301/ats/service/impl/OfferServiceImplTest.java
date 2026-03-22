package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.request.ApprovalDecisionRequest;
import fptu.sba301.ats.dto.request.CreateOfferRequest;
import fptu.sba301.ats.dto.request.UpdateOfferRequest;
import fptu.sba301.ats.entity.*;
import fptu.sba301.ats.enums.OfferStatus;
import fptu.sba301.ats.exception.BusinessException;
import fptu.sba301.ats.mapper.OfferMapper;
import fptu.sba301.ats.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfferServiceImplTest {

    @Mock
    private OfferRepository offerRepository;
    @Mock
    private OfferApprovalRepository approvalRepository;
    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private CandidateRepository candidateRepository;
    @Mock
    private JobRepository jobRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private OfferMapper offerMapper;

    @InjectMocks
    private OfferServiceImpl service;

    @org.springframework.lang.NonNull
    private final UUID appId = UUID.randomUUID();
    @org.springframework.lang.NonNull
    private final UUID offerId = UUID.randomUUID();
    @org.springframework.lang.NonNull
    private final UUID userId = UUID.randomUUID();
    @org.springframework.lang.NonNull
    private final UUID candidateId = UUID.randomUUID();
    @org.springframework.lang.NonNull
    private final UUID jobId = UUID.randomUUID();

    @Test
    void createOffer_BlockedIfActiveOfferExists() {
        CreateOfferRequest req = new CreateOfferRequest(appId, new BigDecimal("1000"), "Title", LocalDate.now(), null,
                "Note");

        when(applicationRepository.findById(appId)).thenReturn(Optional.of(new Application()));
        when(offerRepository.existsByApplicationIdAndStatusIn(eq(appId), anyList())).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.create(req, "user@test.com"));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        assertTrue(ex.getMessage().contains("active offer already exists"));
    }

    @Test
    void submitForApproval_SuccessForDraftState() {
        Offer offer = new Offer();
        offer.setId(offerId);
        offer.setStatus(OfferStatus.DRAFT);

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));

        assertDoesNotThrow(() -> service.submitForApproval(offerId, "user@test.com"));
        assertEquals(OfferStatus.PENDING_APPROVAL, offer.getStatus());
        verify(offerRepository).save(offer);
    }

    @Test
    void submitForApproval_BlockedForNonDraftState() {
        Offer offer = new Offer();
        offer.setId(offerId);
        offer.setStatus(OfferStatus.APPROVED);

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.submitForApproval(offerId, "user@test.com"));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    }

    @Test
    void createOffer_ApplicationNotFound_ThrowsNotFound() {
        when(applicationRepository.findById(appId)).thenReturn(Optional.empty());

        CreateOfferRequest req = new CreateOfferRequest(appId, new java.math.BigDecimal("5000"),
                "Dev", java.time.LocalDate.now(), null, "note");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.create(req, "hr@test.com"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertTrue(ex.getMessage().contains("Application not found"));
    }

    @Test
    void updateOffer_NotDraftState_ThrowsConflict() {
        Offer offer = new Offer();
        offer.setId(offerId);
        offer.setStatus(OfferStatus.APPROVED);
        offer.setCreatedBy(UUID.randomUUID());

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));

        UpdateOfferRequest req = new UpdateOfferRequest(null, null, null, null, "updated");
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.update(offerId, req, "hr@test.com"));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        assertTrue(ex.getMessage().contains("Only DRAFT"));
    }

    @Test
    void approve_NotPendingApproval_ThrowsConflict() {
        Offer offer = new Offer();
        offer.setId(offerId);
        offer.setStatus(OfferStatus.DRAFT);

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));

        ApprovalDecisionRequest req = new ApprovalDecisionRequest("ok");
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.approve(offerId, req, "manager@test.com"));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    }

    @Test
    void reject_NotPendingApproval_ThrowsConflict() {
        Offer offer = new Offer();
        offer.setId(offerId);
        offer.setStatus(OfferStatus.APPROVED);

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));

        ApprovalDecisionRequest req = new ApprovalDecisionRequest("not ok");
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.reject(offerId, req, "manager@test.com"));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    }

    @Test
    void candidateAccept_WrongOfferStatus_ThrowsConflict() {
        Offer offer = new Offer();
        offer.setId(offerId);
        offer.setStatus(OfferStatus.DRAFT); // Not APPROVED/SENT

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.candidateAccept(offerId, "candidate@test.com"));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        assertTrue(ex.getMessage().contains("APPROVED or SENT"));
    }

    @Test
    void candidateAccept_WrongCandidate_ThrowsForbidden() {
        Application app = new Application();
        app.setId(appId);
        
        Candidate candidate = new Candidate();
        candidate.setId(candidateId);
        candidate.setEmail("real@candidate.com");
        app.setCandidate(candidate);

        Offer offer = new Offer();
        offer.setId(offerId);
        offer.setStatus(OfferStatus.APPROVED);
        offer.setApplication(app);

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));
        when(applicationRepository.findById(appId)).thenReturn(Optional.of(app));
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.candidateAccept(offerId, "wrong@candidate.com"));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
        assertTrue(ex.getMessage().contains("not authorized"));
    }

    @Test
    void candidateReject_WrongOfferStatus_ThrowsConflict() {
        Offer offer = new Offer();
        offer.setId(offerId);
        offer.setStatus(OfferStatus.REJECTED); // Not APPROVED/SENT

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.candidateReject(offerId, "note", "candidate@test.com"));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        assertTrue(ex.getMessage().contains("APPROVED or SENT"));
    }

    @Test
    void approve_PendingApproval_SetsStatusToApproved() {
        Offer offer = new Offer();
        offer.setId(offerId);
        offer.setStatus(OfferStatus.PENDING_APPROVAL);

        User approver = new User();
        approver.setId(userId);
        approver.setEmail("manager@test.com");

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));
        when(userRepository.findByEmailAndDeletedFalse("manager@test.com")).thenReturn(Optional.of(approver));
        when(approvalRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(offerRepository.save(offer)).thenReturn(offer);

        ApprovalDecisionRequest req = new ApprovalDecisionRequest("Looks good");
        assertDoesNotThrow(() -> service.approve(offerId, req, "manager@test.com"));

        assertEquals(OfferStatus.APPROVED, offer.getStatus());
        verify(approvalRepository).save(any(OfferApproval.class));
        verify(offerRepository).save(offer);
    }

    @Test
    void reject_PendingApproval_SetsStatusToRejected() {
        Offer offer = new Offer();
        offer.setId(offerId);
        offer.setStatus(OfferStatus.PENDING_APPROVAL);

        User approver = new User();
        approver.setId(userId);
        approver.setEmail("manager@test.com");

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));
        when(userRepository.findByEmailAndDeletedFalse("manager@test.com")).thenReturn(Optional.of(approver));
        when(approvalRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(offerRepository.save(offer)).thenReturn(offer);

        ApprovalDecisionRequest req = new ApprovalDecisionRequest("Salary too high");
        assertDoesNotThrow(() -> service.reject(offerId, req, "manager@test.com"));

        assertEquals(OfferStatus.REJECTED, offer.getStatus());
        verify(approvalRepository).save(any(OfferApproval.class));
    }

    @Test
    void generatePdf_OfferNotFound_ThrowsNotFound() {
        UUID randomId = UUID.randomUUID();
        when(offerRepository.findById(randomId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.generatePdf(randomId));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertTrue(ex.getMessage().contains("Offer not found"));
    }

    @Test
    void generatePdf_ValidOffer_ReturnsPdfBytes() {
        Candidate candidate = new Candidate();
        candidate.setId(candidateId);
        candidate.setFullName("Nguyen Van A");

        Job job = new Job();
        job.setId(jobId);
        job.setTitle("Backend Developer");

        Application app = new Application();
        app.setId(appId);
        app.setCandidate(candidate);
        app.setJob(job);

        Offer offer = new Offer();
        offer.setId(offerId);
        offer.setApplication(app);
        offer.setPositionTitle("Backend Dev");
        offer.setSalary(new java.math.BigDecimal("5000.00"));
        offer.setStatus(OfferStatus.APPROVED);

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));
        when(applicationRepository.findById(appId)).thenReturn(Optional.of(app));
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

        byte[] pdf = service.generatePdf(offerId);

        assertNotNull(pdf, "PDF bytes should not be null");
        assertTrue(pdf.length > 0, "PDF should have content");
        // PDF files start with the %PDF header
        assertEquals('%', (char) pdf[0]);
        assertEquals('P', (char) pdf[1]);
        assertEquals('D', (char) pdf[2]);
        assertEquals('F', (char) pdf[3]);
    }
}
