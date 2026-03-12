package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.response.*;
import fptu.sba301.ats.entity.*;
import fptu.sba301.ats.enums.InterviewStatus;
import fptu.sba301.ats.exception.BusinessException;
import fptu.sba301.ats.repository.*;
import fptu.sba301.ats.service.CandidateEvaluationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class CandidateEvaluationServiceImpl implements CandidateEvaluationService {

    private final ApplicationRepository applicationRepository;
    private final InterviewRepository interviewRepository;
    private final InterviewScoreRepository scoreRepository;
    private final ScorecardCriterionRepository criterionRepository;
    private final CandidateRepository candidateRepository;
    private final JobRepository jobRepository;

    @Override
    @Transactional(readOnly = true)
    public EvaluationSummaryResponse getEvaluation(Long applicationId) {

        // 1. Load application
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new BusinessException(
                        "Application not found with id: " + applicationId, HttpStatus.NOT_FOUND));

        // 2. Resolve names
        String candidateName = candidateRepository.findById(application.getCandidateId())
                .map(Candidate::getFullName).orElse("Unknown Candidate");
        String jobTitle = jobRepository.findById(application.getJobId())
                .map(Job::getTitle).orElse("Unknown Job");

        // 3. Load completed interviews for this application
        List<Interview> completedInterviews = interviewRepository
                .findByApplicationIdAndStatus(applicationId, InterviewStatus.COMPLETED);

        if (completedInterviews.isEmpty()) {
            return new EvaluationSummaryResponse(applicationId, candidateName, jobTitle,
                    null, Collections.emptyList(), Collections.emptyList());
        }

        List<Long> interviewIds = completedInterviews.stream()
                .map(Interview::getId).collect(Collectors.toList());

        // 4. Load all scores for these interviews
        List<InterviewScore> allScores = scoreRepository.findByInterviewIdIn(interviewIds);

        if (allScores.isEmpty()) {
            return new EvaluationSummaryResponse(applicationId, candidateName, jobTitle,
                    null, Collections.emptyList(), Collections.emptyList());
        }

        Set<Long> criterionIds = allScores.stream()
                .map(InterviewScore::getCriterionId).collect(Collectors.toSet());
        Map<Long, ScorecardCriterion> criterionMap = new HashMap<>();
        criterionRepository.findAllById(criterionIds)
                .forEach(c -> criterionMap.put(c.getId(), c));

        // 6. Group scores by criterion and compute per-criterion averages
        Map<Long, List<Integer>> scoresByCriterion = allScores.stream()
                .collect(Collectors.groupingBy(
                        InterviewScore::getCriterionId,
                        Collectors.mapping(InterviewScore::getScore, Collectors.toList())));

        List<CriterionScoreSummary> criterionSummaries = new ArrayList<>();
        double aggregateScore = 0.0;

        for (Map.Entry<Long, List<Integer>> entry : scoresByCriterion.entrySet()) {
            Long criterionId = entry.getKey();
            List<Integer> scores = entry.getValue();
            ScorecardCriterion criterion = criterionMap.get(criterionId);
            if (criterion == null)
                continue; // criterion was deleted; skip

            double avgScore = scores.stream().mapToInt(Integer::intValue).average().orElse(0.0);
            double weight = criterion.getWeight().doubleValue();
            double weightedScore = avgScore * weight;
            aggregateScore += weightedScore;

            criterionSummaries.add(new CriterionScoreSummary(
                    criterionId, criterion.getName(), weight, avgScore, weightedScore));
        }

        // 7. Build per-interviewer breakdown
        Map<Long, List<InterviewScore>> scoresByInterviewer = allScores.stream()
                .collect(Collectors.groupingBy(InterviewScore::getInterviewerId));

        List<InterviewerScoreBreakdown> breakdowns = scoresByInterviewer.entrySet().stream().map(entry -> {
            Long interviewerId = entry.getKey();
            List<InterviewScore> iScores = entry.getValue();
            Map<String, Integer> byName = new LinkedHashMap<>();
            iScores.forEach(s -> {
                ScorecardCriterion c = criterionMap.get(s.getCriterionId());
                if (c != null)
                    byName.put(c.getName(), s.getScore());
            });
            String overallComment = iScores.stream()
                    .map(InterviewScore::getOverallComment)
                    .filter(Objects::nonNull).findFirst().orElse(null);
            return new InterviewerScoreBreakdown(interviewerId, "Interviewer#" + interviewerId,
                    byName, overallComment);
        }).collect(Collectors.toList());

        return new EvaluationSummaryResponse(applicationId, candidateName, jobTitle,
                aggregateScore, criterionSummaries, breakdowns);
    }
}
