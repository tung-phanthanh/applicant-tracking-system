package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.response.CandidateRankEntry;
import fptu.sba301.ats.dto.response.CandidateRankingResponse;
import fptu.sba301.ats.dto.response.EvaluationSummaryResponse;
import fptu.sba301.ats.entity.Application;
import fptu.sba301.ats.entity.Candidate;
import fptu.sba301.ats.entity.Interview;
import fptu.sba301.ats.entity.Job;
import fptu.sba301.ats.enums.ApplicationStage;
import fptu.sba301.ats.enums.ApplicationStatus;
import fptu.sba301.ats.enums.InterviewStatus;
import fptu.sba301.ats.exception.BusinessException;
import fptu.sba301.ats.repository.*;
import fptu.sba301.ats.service.CandidateEvaluationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidateRankingServiceImplTest {

    @Mock
    private JobRepository jobRepository;
    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private CandidateRepository candidateRepository;
    @Mock
    private InterviewRepository interviewRepository;
    @Mock
    private CandidateEvaluationService evaluationService;

    @InjectMocks
    private CandidateRankingServiceImpl service;

    // Helpers
    private EvaluationSummaryResponse eval(long appId, String candidateName, String jobTitle, double score) {
        return new EvaluationSummaryResponse(appId, candidateName, jobTitle, score, List.of(), List.of());
    }

    // ==========================================
    // getRanking — Exception paths
    // ==========================================

    @Test
    void getRanking_JobNotFound_ThrowsNotFound() {
        when(jobRepository.findById(99L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.getRanking(99L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertTrue(ex.getMessage().contains("Job not found"));
    }

    // ==========================================
    // getRanking — Happy paths
    // ==========================================

    @Test
    void getRanking_NoApplications_ReturnsEmptyRanking() {
        Job job = new Job();
        job.setId(1L);
        job.setTitle("Software Engineer");

        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(applicationRepository.findActiveByJobId(1L)).thenReturn(Collections.emptyList());
        when(interviewRepository.findByApplicationIdIn(Collections.emptyList())).thenReturn(Collections.emptyList());

        CandidateRankingResponse response = service.getRanking(1L);

        assertNotNull(response);
        assertEquals(1L, response.jobId());
        assertEquals("Software Engineer", response.jobTitle());
        assertEquals(0, response.totalCandidates());
        assertTrue(response.ranking().isEmpty());
    }

    @Test
    void getRanking_SingleCandidate_ReturnedWithRank1() {
        Job job = new Job();
        job.setId(1L);
        job.setTitle("Backend Dev");

        Application app = new Application();
        app.setId(10L);
        app.setCandidateId(20L);
        app.setJobId(1L);
        app.setStage(ApplicationStage.INTERVIEW);
        app.setStatus(ApplicationStatus.ACTIVE);

        Candidate candidate = new Candidate();
        candidate.setId(20L);
        candidate.setFullName("Nguyen Van A");

        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(applicationRepository.findActiveByJobId(1L)).thenReturn(List.of(app));
        when(candidateRepository.findAllById(any())).thenReturn(List.of(candidate));
        when(interviewRepository.findByApplicationIdIn(List.of(10L))).thenReturn(Collections.emptyList());
        when(evaluationService.getEvaluation(10L))
                .thenReturn(eval(10L, "Nguyen Van A", "Backend Dev", 4.2));

        CandidateRankingResponse response = service.getRanking(1L);

        assertNotNull(response);
        assertEquals(1, response.totalCandidates());
        assertEquals(1, response.ranking().size());

        CandidateRankEntry entry = response.ranking().get(0);
        assertEquals(1, entry.rank());
        assertEquals(10L, entry.applicationId());
        assertEquals(20L, entry.candidateId());
        assertEquals("Nguyen Van A", entry.candidateFullName());
        assertEquals(4.2, entry.aggregateScore(), 0.001);
    }

    @Test
    void getRanking_MultipleCandidates_SortedByScoreDescending() {
        Job job = new Job();
        job.setId(2L);
        job.setTitle("QA Engineer");

        Application appA = new Application();
        appA.setId(11L);
        appA.setCandidateId(21L);
        appA.setJobId(2L);
        appA.setStage(ApplicationStage.INTERVIEW);
        appA.setStatus(ApplicationStatus.ACTIVE);

        Application appB = new Application();
        appB.setId(12L);
        appB.setCandidateId(22L);
        appB.setJobId(2L);
        appB.setStage(ApplicationStage.INTERVIEW);
        appB.setStatus(ApplicationStatus.ACTIVE);

        Candidate candidateA = new Candidate();
        candidateA.setId(21L);
        candidateA.setFullName("Tran Thi B");

        Candidate candidateB = new Candidate();
        candidateB.setId(22L);
        candidateB.setFullName("Le Van C");

        // A has lower score (3.0), B has higher score (4.8)
        when(jobRepository.findById(2L)).thenReturn(Optional.of(job));
        when(applicationRepository.findActiveByJobId(2L)).thenReturn(List.of(appA, appB));
        when(candidateRepository.findAllById(any())).thenReturn(List.of(candidateA, candidateB));
        when(interviewRepository.findByApplicationIdIn(any())).thenReturn(Collections.emptyList());
        when(evaluationService.getEvaluation(11L)).thenReturn(eval(11L, "Tran Thi B", "QA Engineer", 3.0));
        when(evaluationService.getEvaluation(12L)).thenReturn(eval(12L, "Le Van C", "QA Engineer", 4.8));

        CandidateRankingResponse response = service.getRanking(2L);

        assertEquals(2, response.totalCandidates());
        // Higher score (4.8) must be rank 1
        CandidateRankEntry first = response.ranking().get(0);
        CandidateRankEntry second = response.ranking().get(1);
        assertEquals(1, first.rank());
        assertEquals(4.8, first.aggregateScore(), 0.001);
        assertEquals(2, second.rank());
        assertEquals(3.0, second.aggregateScore(), 0.001);
    }

    @Test
    void getRanking_CandidateWithNoEvaluation_ScoreIsNull() {
        Job job = new Job();
        job.setId(3L);
        job.setTitle("DevOps");

        Application app = new Application();
        app.setId(13L);
        app.setCandidateId(23L);
        app.setJobId(3L);
        app.setStage(ApplicationStage.SCREENING);
        app.setStatus(ApplicationStatus.ACTIVE);

        Candidate candidate = new Candidate();
        candidate.setId(23L);
        candidate.setFullName("Pham Van D");

        when(jobRepository.findById(3L)).thenReturn(Optional.of(job));
        when(applicationRepository.findActiveByJobId(3L)).thenReturn(List.of(app));
        when(candidateRepository.findAllById(any())).thenReturn(List.of(candidate));
        when(interviewRepository.findByApplicationIdIn(any())).thenReturn(Collections.emptyList());
        when(evaluationService.getEvaluation(13L))
                .thenThrow(new BusinessException("No scores", HttpStatus.NOT_FOUND));

        CandidateRankingResponse response = service.getRanking(3L);

        assertEquals(1, response.totalCandidates());
        assertNull(response.ranking().get(0).aggregateScore(),
                "Score should be null when no completed interviews");
    }

    @Test
    void getRanking_InterviewCountAndLastInterviewAt_Populated() {
        Job job = new Job();
        job.setId(4L);
        job.setTitle("PM");

        Application app = new Application();
        app.setId(14L);
        app.setCandidateId(24L);
        app.setJobId(4L);
        app.setStage(ApplicationStage.INTERVIEW);
        app.setStatus(ApplicationStatus.ACTIVE);

        Candidate candidate = new Candidate();
        candidate.setId(24L);
        candidate.setFullName("Hoang Thi E");

        Instant firstAt = Instant.parse("2026-01-10T09:00:00Z");
        Instant secondAt = Instant.parse("2026-01-20T09:00:00Z");

        Interview i1 = new Interview();
        i1.setApplicationId(14L);
        i1.setStatus(InterviewStatus.COMPLETED);
        i1.setScheduledAt(firstAt);

        Interview i2 = new Interview();
        i2.setApplicationId(14L);
        i2.setStatus(InterviewStatus.COMPLETED);
        i2.setScheduledAt(secondAt);

        when(jobRepository.findById(4L)).thenReturn(Optional.of(job));
        when(applicationRepository.findActiveByJobId(4L)).thenReturn(List.of(app));
        when(candidateRepository.findAllById(any())).thenReturn(List.of(candidate));
        when(interviewRepository.findByApplicationIdIn(List.of(14L))).thenReturn(List.of(i1, i2));
        when(evaluationService.getEvaluation(14L))
                .thenThrow(new BusinessException("No scores", HttpStatus.NOT_FOUND));

        CandidateRankingResponse response = service.getRanking(4L);

        CandidateRankEntry entry = response.ranking().get(0);
        assertEquals(2, entry.interviewCount());
        // lastInterviewAt should be the most recent: secondAt
        assertEquals(secondAt, entry.lastInterviewAt());
    }
}
