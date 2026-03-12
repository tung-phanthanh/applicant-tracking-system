package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.response.CandidateRankEntry;
import fptu.sba301.ats.dto.response.CandidateRankingResponse;
import fptu.sba301.ats.dto.response.EvaluationSummaryResponse;
import fptu.sba301.ats.entity.Application;
import fptu.sba301.ats.entity.Candidate;
import fptu.sba301.ats.entity.Interview;
import fptu.sba301.ats.enums.ApplicationStage;
import fptu.sba301.ats.enums.InterviewStatus;
import fptu.sba301.ats.exception.BusinessException;
import fptu.sba301.ats.repository.ApplicationRepository;
import fptu.sba301.ats.repository.CandidateRepository;
import fptu.sba301.ats.repository.InterviewRepository;
import fptu.sba301.ats.repository.JobRepository;
import fptu.sba301.ats.service.CandidateEvaluationService;
import fptu.sba301.ats.service.CandidateRankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidateRankingServiceImpl implements CandidateRankingService {

    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final CandidateRepository candidateRepository;
    private final InterviewRepository interviewRepository;
    private final CandidateEvaluationService evaluationService;

    @Override
    @Transactional(readOnly = true)
    public CandidateRankingResponse getRanking(Long jobId) {

        // 1. Verify job exists
        var job = jobRepository.findById(jobId)
                .orElseThrow(() -> new BusinessException(
                        "Job not found with id: " + jobId, HttpStatus.NOT_FOUND));

        // 2. Load active applications
        List<Application> applications = applicationRepository.findActiveByJobId(jobId);

        // 3. Build candidate name map
        Set<Long> candidateIds = applications.stream()
                .map(Application::getCandidateId).collect(Collectors.toSet());
        Map<Long, String> candidateNames = new HashMap<>();
        candidateRepository.findAllById(candidateIds)
                .forEach(c -> candidateNames.put(c.getId(), c.getFullName()));

        // 4. Load interviews for all these applications
        List<Long> applicationIds = applications.stream()
                .map(Application::getId).collect(Collectors.toList());
        List<Interview> interviews = interviewRepository.findByApplicationIdIn(applicationIds);

        // Interview count and last interview per application
        Map<Long, List<Interview>> interviewsByApp = interviews.stream()
                .collect(Collectors.groupingBy(Interview::getApplicationId));

        // 5. Build rank entries with evaluation score
        List<CandidateRankEntry> entries = applications.stream().map(app -> {
            Double score = null;
            try {
                EvaluationSummaryResponse eval = evaluationService.getEvaluation(app.getId());
                score = eval.aggregateWeightedScore();
            } catch (Exception ignored) {
                // No completed interviews or evaluation error — score stays null
            }

            List<Interview> appInterviews = interviewsByApp.getOrDefault(app.getId(), Collections.emptyList());
            int interviewCount = appInterviews.size();
            Instant lastInterviewAt = appInterviews.stream()
                    .filter(i -> i.getStatus() == InterviewStatus.COMPLETED)
                    .map(Interview::getScheduledAt)
                    .filter(Objects::nonNull)
                    .max(Comparator.naturalOrder())
                    .orElse(null);

            return new CandidateRankEntry(
                    0, // rank assigned after sort
                    app.getId(),
                    app.getCandidateId(),
                    candidateNames.getOrDefault(app.getCandidateId(), "Unknown"),
                    app.getStage().name(),
                    score,
                    interviewCount,
                    lastInterviewAt);
        }).collect(Collectors.toList());

        // 6. Sort: score DESC (nulls last) → stage ordinal DESC → lastInterviewAt DESC
        entries.sort(Comparator
                .comparingDouble((CandidateRankEntry e) -> e.aggregateScore() == null ? -1.0 : e.aggregateScore())
                .reversed()
                .thenComparingInt((CandidateRankEntry e) -> ApplicationStage.valueOf(e.currentStage()).ordinal())
                .reversed()
                .thenComparing(Comparator.comparing(
                        CandidateRankEntry::lastInterviewAt,
                        Comparator.nullsLast(Comparator.reverseOrder()))));

        // 7. Assign rank (tied scores get same rank)
        List<CandidateRankEntry> ranked = new ArrayList<>();
        int rank = 1;
        for (int i = 0; i < entries.size(); i++) {
            CandidateRankEntry entry = entries.get(i);
            if (i > 0) {
                CandidateRankEntry prev = entries.get(i - 1);
                if (!Objects.equals(entry.aggregateScore(), prev.aggregateScore())
                        || !entry.currentStage().equals(prev.currentStage())) {
                    rank = i + 1;
                }
            }
            ranked.add(new CandidateRankEntry(rank, entry.applicationId(), entry.candidateId(),
                    entry.candidateFullName(), entry.currentStage(), entry.aggregateScore(),
                    entry.interviewCount(), entry.lastInterviewAt()));
        }

        return new CandidateRankingResponse(jobId, job.getTitle(), ranked.size(), ranked);
    }
}
