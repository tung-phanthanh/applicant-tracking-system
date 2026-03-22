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
import java.util.UUID;

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

    private final UUID jobId = UUID.randomUUID();
    private final UUID appId1 = UUID.randomUUID();
    private final UUID appId2 = UUID.randomUUID();
    private final UUID candidateId1 = UUID.randomUUID();
    private final UUID candidateId2 = UUID.randomUUID();

    private EvaluationSummaryResponse eval(UUID appId, String candidateName, String jobTitle, double score) {
        return new EvaluationSummaryResponse(appId, candidateName, jobTitle, score, List.of(), List.of());
    }

    @Test
    void getRanking_JobNotFound_ThrowsNotFound() {
        UUID randomId = UUID.randomUUID();
        when(jobRepository.findById(randomId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.getRanking(randomId));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertTrue(ex.getMessage().contains("Job not found"));
    }

    @Test
    void getRanking_SingleCandidate_ReturnedWithRank1() {
        Job job = new Job();
        job.setId(jobId);
        job.setTitle("Backend Dev");

        Candidate candidate = new Candidate();
        candidate.setId(candidateId1);
        candidate.setFullName("Nguyen Van A");

        Application app = new Application();
        app.setId(appId1);
        app.setCandidate(candidate);
        app.setJob(job);
        app.setStage(ApplicationStage.INTERVIEW);
        app.setStatus(ApplicationStatus.ACTIVE);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        // Service uses findAll() and filters in memory
        when(applicationRepository.findAll()).thenReturn(List.of(app));
        when(candidateRepository.findAllById(any())).thenReturn(List.of(candidate));
        when(interviewRepository.findByApplicationIdIn(List.of(appId1))).thenReturn(Collections.emptyList());
        when(evaluationService.getEvaluation(appId1))
                .thenReturn(eval(appId1, "Nguyen Van A", "Backend Dev", 4.2));

        CandidateRankingResponse response = service.getRanking(jobId);

        assertNotNull(response);
        assertEquals(1, response.totalCandidates());
        assertEquals(1, response.ranking().size());

        CandidateRankEntry entry = response.ranking().get(0);
        assertEquals(1, entry.rank());
        assertEquals(appId1, entry.applicationId());
        assertEquals(candidateId1, entry.candidateId());
        assertEquals("Nguyen Van A", entry.candidateFullName());
        assertEquals(4.2, entry.aggregateScore(), 0.001);
    }

    @Test
    void getRanking_MultipleCandidates_SortedByScoreDescending() {
        Job job = new Job();
        job.setId(jobId);
        job.setTitle("QA Engineer");

        Candidate candidateA = new Candidate();
        candidateA.setId(candidateId1);
        candidateA.setFullName("Tran Thi B");

        Candidate candidateB = new Candidate();
        candidateB.setId(candidateId2);
        candidateB.setFullName("Le Van C");

        Application appA = new Application();
        appA.setId(appId1);
        appA.setCandidate(candidateA);
        appA.setJob(job);
        appA.setStage(ApplicationStage.INTERVIEW);
        appA.setStatus(ApplicationStatus.ACTIVE);

        Application appB = new Application();
        appB.setId(appId2);
        appB.setCandidate(candidateB);
        appB.setJob(job);
        appB.setStage(ApplicationStage.INTERVIEW);
        appB.setStatus(ApplicationStatus.ACTIVE);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(applicationRepository.findAll()).thenReturn(List.of(appA, appB));
        when(candidateRepository.findAllById(any())).thenReturn(List.of(candidateA, candidateB));
        when(interviewRepository.findByApplicationIdIn(any())).thenReturn(Collections.emptyList());
        when(evaluationService.getEvaluation(appId1)).thenReturn(eval(appId1, "Tran Thi B", "QA Engineer", 3.0));
        when(evaluationService.getEvaluation(appId2)).thenReturn(eval(appId2, "Le Van C", "QA Engineer", 4.8));

        CandidateRankingResponse response = service.getRanking(jobId);

        assertEquals(2, response.totalCandidates());
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
        job.setId(jobId);
        job.setTitle("DevOps");

        Candidate candidate = new Candidate();
        candidate.setId(candidateId1);
        candidate.setFullName("Pham Van D");

        Application app = new Application();
        app.setId(appId1);
        app.setCandidate(candidate);
        app.setJob(job);
        app.setStage(ApplicationStage.SCREENING);
        app.setStatus(ApplicationStatus.ACTIVE);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(applicationRepository.findAll()).thenReturn(List.of(app));
        when(candidateRepository.findAllById(any())).thenReturn(List.of(candidate));
        when(interviewRepository.findByApplicationIdIn(any())).thenReturn(Collections.emptyList());
        when(evaluationService.getEvaluation(appId1))
                .thenThrow(new BusinessException("No scores", HttpStatus.NOT_FOUND));

        CandidateRankingResponse response = service.getRanking(jobId);

        assertEquals(1, response.totalCandidates());
        assertNull(response.ranking().get(0).aggregateScore(),
                "Score should be null when no completed interviews");
    }

    @Test
    void getRanking_InterviewCountAndLastInterviewAt_Populated() {
        Job job = new Job();
        job.setId(jobId);
        job.setTitle("PM");

        Candidate candidate = new Candidate();
        candidate.setId(candidateId1);
        candidate.setFullName("Hoang Thi E");

        Application app = new Application();
        app.setId(appId1);
        app.setCandidate(candidate);
        app.setJob(job);
        app.setStage(ApplicationStage.INTERVIEW);
        app.setStatus(ApplicationStatus.ACTIVE);

        Instant firstAt = Instant.parse("2026-01-10T09:00:00Z");
        Instant secondAt = Instant.parse("2026-01-20T09:00:00Z");

        Interview i1 = new Interview();
        i1.setApplication(app);
        i1.setStatus(InterviewStatus.COMPLETED);
        i1.setScheduledAt(firstAt);

        Interview i2 = new Interview();
        i2.setApplication(app);
        i2.setStatus(InterviewStatus.COMPLETED);
        i2.setScheduledAt(secondAt);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(applicationRepository.findAll()).thenReturn(List.of(app));
        when(candidateRepository.findAllById(any())).thenReturn(List.of(candidate));
        when(interviewRepository.findByApplicationIdIn(List.of(appId1))).thenReturn(List.of(i1, i2));
        when(evaluationService.getEvaluation(appId1))
                .thenThrow(new BusinessException("No scores", HttpStatus.NOT_FOUND));

        CandidateRankingResponse response = service.getRanking(jobId);

        CandidateRankEntry entry = response.ranking().get(0);
        assertEquals(2, entry.interviewCount());
        assertEquals(secondAt, entry.lastInterviewAt());
    }
}
