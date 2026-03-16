package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.response.CandidateEvaluationResponse;
import fptu.sba301.ats.dto.response.CandidateRankingResponse;
import fptu.sba301.ats.entity.*;
import fptu.sba301.ats.enums.ApplicationStage;
import fptu.sba301.ats.enums.InterviewStatus;
import fptu.sba301.ats.repository.*;
import fptu.sba301.ats.service.CandidateEvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidateEvaluationServiceImpl implements CandidateEvaluationService {

    private final ApplicationRepository applicationRepository;
    private final InterviewRepository interviewRepository;
    private final InterviewScoreRepository interviewScoreRepository;
    private final OfferRepository offerRepository;

    @Override
    public CandidateEvaluationResponse getCandidateEvaluation(UUID applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        Candidate candidate = application.getCandidate();
        Job job = application.getJob();

        List<Interview> interviews = interviewRepository.findByApplicationId(applicationId);
        int totalInterviews = interviews.size();
        int completedInterviews = (int) interviews.stream()
                .filter(i -> i.getStatus() == InterviewStatus.COMPLETED)
                .count();

        List<CandidateEvaluationResponse.StageEvaluation> stageEvaluations = new ArrayList<>();
        double totalScore = 0;
        int scoredInterviews = 0;

        for (Interview interview : interviews) {
            List<InterviewScore> scores = interviewScoreRepository.findByInterviewId(interview.getId());
            
            Double interviewScore = null;
            String feedback = null;
            String interviewerName = null;

            if (!scores.isEmpty()) {
                double sum = scores.stream().mapToInt(InterviewScore::getScore).sum();
                interviewScore = sum / scores.size();
                totalScore += interviewScore;
                scoredInterviews++;

                feedback = scores.stream()
                        .map(InterviewScore::getComment)
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining("; "));

                User interviewer = scores.get(0).getInterviewer();
                if (interviewer != null) {
                    interviewerName = interviewer.getFullName();
                }
            }

            String status = interview.getStatus() == InterviewStatus.COMPLETED ? "Completed" :
                           interview.getStatus() == InterviewStatus.SCHEDULED ? "Scheduled" : "Cancelled";

            CandidateEvaluationResponse.StageEvaluation stageEval = 
                CandidateEvaluationResponse.StageEvaluation.builder()
                    .interviewId(interview.getId().toString())
                    .stageName("Interview " + (interviews.indexOf(interview) + 1))
                    .status(status)
                    .score(interviewScore)
                    .interviewerName(interviewerName)
                    .date(interview.getScheduledAt() != null ? interview.getScheduledAt().toString() : null)
                    .feedback(feedback)
                    .build();
            
            stageEvaluations.add(stageEval);
        }

        double overallScore = scoredInterviews > 0 ? totalScore / scoredInterviews : 0;
        
        String recommendation = "Hold";
        if (overallScore >= 4) {
            recommendation = "Strong Hire";
        } else if (overallScore >= 3) {
            recommendation = "Hire";
        } else if (overallScore > 0 && overallScore < 3) {
            recommendation = "No Hire";
        }

        Optional<Offer> offer = offerRepository.findByApplicationId(applicationId).stream().findFirst();
        String applicationStatus = offer.isPresent() ? "Offer " + offer.get().getStatus() : application.getStage().toString();

        return CandidateEvaluationResponse.builder()
                .applicationId(applicationId.toString())
                .candidateId(candidate.getId().toString())
                .candidateName(candidate.getFullName())
                .candidateEmail(candidate.getEmail())
                .positionTitle(job.getTitle())
                .jobId(job.getId().toString())
                .overallScore(Math.round(overallScore * 10) / 10.0)
                .recommendation(recommendation)
                .interviewsCompleted(completedInterviews)
                .interviewsTotal(totalInterviews)
                .stages(stageEvaluations)
                .build();
    }

    @Override
    public CandidateRankingResponse getCandidatesRanking(UUID jobId) {
        List<Application> applications = applicationRepository.findByJobIdAndStatus(jobId, fptu.sba301.ats.enums.ApplicationStatus.ACTIVE);
        
        Job job = applications.isEmpty() ? null : applications.get(0).getJob();
        
        List<CandidateRankingResponse.CandidateRank> ranks = new ArrayList<>();
        
        for (Application app : applications) {
            Candidate candidate = app.getCandidate();
            
            List<Interview> interviews = interviewRepository.findByApplicationId(app.getId());
            List<InterviewScore> allScores = new ArrayList<>();
            
            for (Interview interview : interviews) {
                allScores.addAll(interviewScoreRepository.findByInterviewId(interview.getId()));
            }
            
            double overallScore = 0;
            double techScore = 0;
            double cultureScore = 0;
            
            if (!allScores.isEmpty()) {
                overallScore = allScores.stream()
                        .mapToInt(InterviewScore::getScore)
                        .average()
                        .orElse(0);
                
                techScore = overallScore * 0.7;
                cultureScore = overallScore * 0.3;
            }

            String status = app.getStage() == ApplicationStage.OFFER ? "Offer Sent" :
                           app.getStage() == ApplicationStage.INTERVIEW ? "Interview" :
                           app.getStage().toString();

            CandidateRankingResponse.CandidateRank rank = CandidateRankingResponse.CandidateRank.builder()
                    .applicationId(app.getId().toString())
                    .candidateId(candidate.getId().toString())
                    .candidateName(candidate.getFullName())
                    .candidateEmail(candidate.getEmail())
                    .overallScore(Math.round(overallScore * 10) / 10.0)
                    .techScore(Math.round(techScore * 10) / 10.0)
                    .cultureScore(Math.round(cultureScore * 10) / 10.0)
                    .status(status)
                    .stage(app.getStage().toString())
                    .build();
            
            ranks.add(rank);
        }
        
        ranks.sort((a, b) -> Double.compare(b.getOverallScore(), a.getOverallScore()));
        
        for (int i = 0; i < ranks.size(); i++) {
            ranks.get(i).setRank(i + 1);
        }

        return CandidateRankingResponse.builder()
                .jobId(jobId.toString())
                .jobTitle(job != null ? job.getTitle() : "")
                .candidates(ranks)
                .build();
    }
}
