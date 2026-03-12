package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.request.SubmitInterviewScoresRequest;
import fptu.sba301.ats.entity.Interview;
import fptu.sba301.ats.entity.User;
import fptu.sba301.ats.enums.InterviewStatus;
import fptu.sba301.ats.exception.BusinessException;
import fptu.sba301.ats.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterviewScoreServiceImplTest {

    @Mock
    private InterviewRepository interviewRepository;
    @Mock
    private InterviewParticipantRepository participantRepository;
    @Mock
    private InterviewScoreRepository scoreRepository;
    @Mock
    private ScorecardCriterionRepository criterionRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private InterviewScoreServiceImpl service;

    // ==========================================
    // submitScores — Exception paths
    // ==========================================

    @Test
    void submitScores_InterviewNotFound_ThrowsNotFound() {
        when(interviewRepository.findById(99L)).thenReturn(Optional.empty());

        SubmitInterviewScoresRequest req = new SubmitInterviewScoresRequest(List.of(), null, null, null, null);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.submitScores(99L, req, "user@test.com"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertTrue(ex.getMessage().contains("Interview not found"));
    }

    @Test
    void submitScores_InterviewNotCompleted_ThrowsConflict() {
        Interview interview = new Interview();
        interview.setId(1L);
        interview.setStatus(InterviewStatus.SCHEDULED);

        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));

        SubmitInterviewScoresRequest req = new SubmitInterviewScoresRequest(List.of(), null, null, null, null);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.submitScores(1L, req, "user@test.com"));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        assertTrue(ex.getMessage().contains("COMPLETED"));
    }

    @Test
    void submitScores_InterviewerNotFound_ThrowsNotFound() {
        Interview interview = new Interview();
        interview.setId(1L);
        interview.setStatus(InterviewStatus.COMPLETED);

        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        when(userRepository.findByEmailAndDeletedFalse("missing@test.com")).thenReturn(Optional.empty());

        SubmitInterviewScoresRequest req = new SubmitInterviewScoresRequest(List.of(), null, null, null, null);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.submitScores(1L, req, "missing@test.com"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertTrue(ex.getMessage().contains("Interviewer not found"));
    }

    // ==========================================
    // getAllScores — Exception paths
    // ==========================================

    @Test
    void getAllScores_InterviewNotFound_ThrowsNotFound() {
        when(interviewRepository.existsById(42L)).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.getAllScores(42L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertTrue(ex.getMessage().contains("Interview not found"));
    }

    // ==========================================
    // getMyScores — Exception paths
    // ==========================================

    @Test
    void getMyScores_InterviewerNotFound_ThrowsNotFound() {
        when(userRepository.findByEmailAndDeletedFalse("ghost@test.com")).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.getMyScores(1L, "ghost@test.com"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertTrue(ex.getMessage().contains("Interviewer not found"));
    }

    @Test
    void getMyScores_NoScoresFound_ThrowsNotFound() {
        User user = new User();
        user.setEmail("user@test.com");
        user.setFullName("Test User");

        when(userRepository.findByEmailAndDeletedFalse("user@test.com")).thenReturn(Optional.of(user));
        // scoreRepository returns empty list → no scores submitted
        when(scoreRepository.findByInterviewIdAndInterviewerId(anyLong(), anyLong()))
                .thenReturn(Collections.emptyList());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.getMyScores(5L, "user@test.com"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertTrue(ex.getMessage().contains("No scores found"));
    }
}
