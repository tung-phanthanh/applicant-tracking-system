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

    @Test
    void getEvaluation_CalculatesWeightedScoreCorrectly() {
        Long appId = 100L;
        Application app = new Application();
        app.setId(appId);
        app.setCandidateId(10L);
        app.setJobId(20L);

        when(applicationRepository.findById(appId)).thenReturn(Optional.of(app));
        when(candidateRepository.findById(10L)).thenReturn(Optional.of(new Candidate()));
        when(jobRepository.findById(20L)).thenReturn(Optional.of(new Job()));

        // 1 Completed interview
        Interview intv = new Interview();
        intv.setId(1L);
        when(interviewRepository.findByApplicationIdAndStatus(appId, InterviewStatus.COMPLETED))
                .thenReturn(List.of(intv));

        // 2 scores for Criterion A (Weight 0.6), 1 score for Criterion B (Weight 0.4)
        InterviewScore s1 = InterviewScore.builder().criterionId(1L).score(4).interviewerId(1L).build();
        InterviewScore s2 = InterviewScore.builder().criterionId(1L).score(5).interviewerId(2L).build();
        InterviewScore s3 = InterviewScore.builder().criterionId(2L).score(3).interviewerId(1L).build();
        when(scoreRepository.findByInterviewIdIn(List.of(1L))).thenReturn(List.of(s1, s2, s3));

        ScorecardCriterion c1 = ScorecardCriterion.builder().name("Coding").weight(new BigDecimal("0.6")).build();
        c1.setId(1L);
        ScorecardCriterion c2 = ScorecardCriterion.builder().name("System Design").weight(new BigDecimal("0.4"))
                .build();
        c2.setId(2L);
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
            if (c.criterionId().equals(1L)) {
                assertEquals(4.5, c.averageScore(), 0.001);
                assertEquals(2.7, c.weightedScore(), 0.001);
            } else if (c.criterionId().equals(2L)) {
                assertEquals(3.0, c.averageScore(), 0.001);
                assertEquals(1.2, c.weightedScore(), 0.001);
            }
        }
    }
}
