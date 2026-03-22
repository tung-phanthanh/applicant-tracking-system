package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.response.CriterionScoreSummary;
import fptu.sba301.ats.dto.response.EvaluationSummaryResponse;
import fptu.sba301.ats.entity.*;
import fptu.sba301.ats.enums.InterviewStatus;
import fptu.sba301.ats.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidateEvaluationServiceImplTest {

    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private InterviewRepository interviewRepository;
    @Mock
    private InterviewScoreRepository scoreRepository;
    @Mock
    private ScorecardCriterionRepository criterionRepository;
    @Mock
    private CandidateRepository candidateRepository;
    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private CandidateEvaluationServiceImpl service;

    private final UUID appId = UUID.randomUUID();
    private final UUID candidateId = UUID.randomUUID();
    private final UUID jobId = UUID.randomUUID();
    private final UUID interviewId = UUID.randomUUID();
    private final UUID criterionId1 = UUID.randomUUID();
    private final UUID criterionId2 = UUID.randomUUID();
    private final UUID interviewerId1 = UUID.randomUUID();
    private final UUID interviewerId2 = UUID.randomUUID();

    @Test
    void getEvaluation_CalculatesWeightedScoreCorrectly() {
        Candidate candidate = new Candidate();
        candidate.setId(candidateId);
        candidate.setFullName("Test Candidate");

        Job job = new Job();
        job.setId(jobId);
        job.setTitle("Test Job");

        Application app = new Application();
        app.setId(appId);
        app.setCandidate(candidate);
        app.setJob(job);

        when(applicationRepository.findById(appId)).thenReturn(Optional.of(app));
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

        // 1 Completed interview
        Interview intv = new Interview();
        intv.setId(interviewId);
        when(interviewRepository.findByApplicationIdAndStatus(appId, InterviewStatus.COMPLETED))
                .thenReturn(List.of(intv));

        // Build Users for participants
        User u1 = new User();
        u1.setId(interviewerId1);
        User u2 = new User();
        u2.setId(interviewerId2);

        // Build InterviewParticipants with users set
        InterviewParticipant p1 = InterviewParticipant.builder().user(u1).build();
        InterviewParticipant p2 = InterviewParticipant.builder().user(u2).build();

        ScorecardCriterion c1 = ScorecardCriterion.builder().name("Coding").weight(new BigDecimal("0.6")).build();
        c1.setId(criterionId1);
        ScorecardCriterion c2 = ScorecardCriterion.builder().name("System Design").weight(new BigDecimal("0.4"))
                .build();
        c2.setId(criterionId2);

        // 2 scores for Criterion 1, 1 score for Criterion 2
        InterviewScore s1 = InterviewScore.builder().interview(intv).criterion(c1).score(4).participant(p1).build();
        InterviewScore s2 = InterviewScore.builder().interview(intv).criterion(c1).score(5).participant(p2).build();
        InterviewScore s3 = InterviewScore.builder().interview(intv).criterion(c2).score(3).participant(p1).build();
        when(scoreRepository.findByInterviewIdIn(List.of(interviewId))).thenReturn(List.of(s1, s2, s3));

        when(criterionRepository.findAllById(any())).thenReturn(List.of(c1, c2));

        EvaluationSummaryResponse result = service.getEvaluation(appId);

        // Expected Math:
        // C1 Avg = (4+5)/2 = 4.5
        // C1 Weighted = 4.5 * 0.6 = 2.7
        // C2 Avg = 3/1 = 3.0
        // C2 Weighted = 3.0 * 0.4 = 1.2
        // Aggregate = 2.7 + 1.2 = 3.9

        assertNotNull(result);
        assertEquals(3.9, result.aggregateWeightedScore(), 0.001);
        assertEquals(2, result.criteria().size());

        // Assert individual breakdown
        for (CriterionScoreSummary c : result.criteria()) {
            if (c.criterionId().equals(criterionId1)) {
                assertEquals(4.5, c.averageScore(), 0.001);
                assertEquals(2.7, c.weightedScore(), 0.001);
            } else if (c.criterionId().equals(criterionId2)) {
                assertEquals(3.0, c.averageScore(), 0.001);
                assertEquals(1.2, c.weightedScore(), 0.001);
            }
        }
    }
}
