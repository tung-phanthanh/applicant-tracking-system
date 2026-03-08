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
import java.util.List;
import java.util.Optional;

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

    @Test
    void createOffer_BlockedIfActiveOfferExists() {
        Long appId = 1L;
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
        offer.setId(10L);
        offer.setStatus(OfferStatus.DRAFT);

        when(offerRepository.findById(10L)).thenReturn(Optional.of(offer));

        assertDoesNotThrow(() -> service.submitForApproval(10L, "user@test.com"));
        assertEquals(OfferStatus.PENDING_APPROVAL, offer.getStatus());
        verify(offerRepository).save(offer);
    }

    @Test
    void submitForApproval_BlockedForNonDraftState() {
        Offer offer = new Offer();
        offer.setId(10L);
        offer.setStatus(OfferStatus.APPROVED);

        when(offerRepository.findById(10L)).thenReturn(Optional.of(offer));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.submitForApproval(10L, "user@test.com"));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    }

    // ==========================================
    // Additional exception paths
    // ==========================================

    @Test
    void createOffer_ApplicationNotFound_ThrowsNotFound() {
        when(applicationRepository.findById(99L)).thenReturn(Optional.empty());

        CreateOfferRequest req = new CreateOfferRequest(99L, new java.math.BigDecimal("5000"),
                "Dev", java.time.LocalDate.now(), null, "note");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.create(req, "hr@test.com"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertTrue(ex.getMessage().contains("Application not found"));
    }

    @Test
    void updateOffer_NotDraftState_ThrowsConflict() {
        Offer offer = new Offer();
        offer.setId(1L);
        offer.setStatus(OfferStatus.APPROVED);
        offer.setCreatedBy(0L);

        when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));

        UpdateOfferRequest req = new UpdateOfferRequest(null, null, null, null, "updated");
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.update(1L, req, "hr@test.com"));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        assertTrue(ex.getMessage().contains("Only DRAFT"));
    }

    @Test
    void approve_NotPendingApproval_ThrowsConflict() {
        Offer offer = new Offer();
        offer.setId(2L);
        offer.setStatus(OfferStatus.DRAFT);

        when(offerRepository.findById(2L)).thenReturn(Optional.of(offer));

        ApprovalDecisionRequest req = new ApprovalDecisionRequest("ok");
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.approve(2L, req, "manager@test.com"));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    }

    @Test
    void reject_NotPendingApproval_ThrowsConflict() {
        Offer offer = new Offer();
        offer.setId(3L);
        offer.setStatus(OfferStatus.APPROVED);

        when(offerRepository.findById(3L)).thenReturn(Optional.of(offer));

        ApprovalDecisionRequest req = new ApprovalDecisionRequest("not ok");
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.reject(3L, req, "manager@test.com"));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    }

    @Test
    void candidateAccept_WrongOfferStatus_ThrowsConflict() {
        Offer offer = new Offer();
        offer.setId(4L);
        offer.setStatus(OfferStatus.DRAFT); // Not APPROVED/SENT

        when(offerRepository.findById(4L)).thenReturn(Optional.of(offer));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.candidateAccept(4L, "candidate@test.com"));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        assertTrue(ex.getMessage().contains("APPROVED or SENT"));
    }

    @Test
    void candidateAccept_WrongCandidate_ThrowsForbidden() {
        Offer offer = new Offer();
        offer.setId(5L);
        offer.setStatus(OfferStatus.APPROVED);
        offer.setApplicationId(10L);

        Application app = new Application();
        app.setId(10L);
        app.setCandidateId(20L);

        Candidate candidate = new Candidate();
        candidate.setId(20L);
        candidate.setEmail("real@candidate.com");

        when(offerRepository.findById(5L)).thenReturn(Optional.of(offer));
        when(applicationRepository.findById(10L)).thenReturn(Optional.of(app));
        when(candidateRepository.findById(20L)).thenReturn(Optional.of(candidate));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.candidateAccept(5L, "wrong@candidate.com"));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
        assertTrue(ex.getMessage().contains("not authorized"));
    }

    @Test
    void candidateReject_WrongOfferStatus_ThrowsConflict() {
        Offer offer = new Offer();
        offer.setId(6L);
        offer.setStatus(OfferStatus.REJECTED); // Not APPROVED/SENT

        when(offerRepository.findById(6L)).thenReturn(Optional.of(offer));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.candidateReject(6L, "note", "candidate@test.com"));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        assertTrue(ex.getMessage().contains("APPROVED or SENT"));
    }
}
