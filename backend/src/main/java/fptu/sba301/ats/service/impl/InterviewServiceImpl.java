package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.request.CriterionScoreRequest;
import fptu.sba301.ats.dto.request.SubmitFeedbackRequest;
import fptu.sba301.ats.dto.response.*;
import fptu.sba301.ats.entity.*;
import fptu.sba301.ats.enums.InterviewStatus;
import fptu.sba301.ats.exception.BusinessException;
import fptu.sba301.ats.repository.*;
import fptu.sba301.ats.repository.ScorecardCriterionRepository;
import fptu.sba301.ats.repository.UserRepository;
import org.springframework.http.HttpStatus;
import fptu.sba301.ats.enums.ParticipantRole;
import fptu.sba301.ats.service.InterviewService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InterviewServiceImpl implements InterviewService {
    private final InterviewRepository interviewRepository;
    private final InterviewParticipantRepository participantRepository;
    private final InterviewScoreRepository scoreRepository;
    private final ScorecardCriterionRepository criterionRepository;
    private final UserRepository userRepository;
    private final CandidateDocumentRepository candidateDocumentRepository;
    private final ApplicationRepository applicationRepository;

    public InterviewServiceImpl(InterviewRepository interviewRepository, InterviewParticipantRepository participantRepository, InterviewScoreRepository scoreRepository, ScorecardCriterionRepository criterionRepository, UserRepository userRepository, CandidateDocumentRepository candidateDocumentRepository, ApplicationRepository applicationRepository) {
        this.interviewRepository = interviewRepository;
        this.participantRepository = participantRepository;
        this.scoreRepository = scoreRepository;
        this.criterionRepository = criterionRepository;
        this.userRepository = userRepository;
        this.candidateDocumentRepository = candidateDocumentRepository;
        this.applicationRepository = applicationRepository;
    }

    @Override
    public List<InterviewResponse> getAllInterviews(String email) {
        List<Interview> interviews = interviewRepository.findAll();

        if (email != null) {
            User user = userRepository.findByEmailAndDeletedFalse(email).orElse(null);
            if (user != null && user.getRole().name().equals("INTERVIEWER")) {
                interviews = interviews.stream()
                        .filter(i -> i.getParticipants().stream()
                                .anyMatch(p -> p.getUser().getId().equals(user.getId())))
                        .toList();
            }
        }

        return interviews.stream()
                .map(interview -> {
                    String candidateName = null;
                    String jobTitle = null;
                    if (interview.getApplication() != null) {
                        if (interview.getApplication().getCandidate() != null) {
                            candidateName = interview.getApplication().getCandidate().getFullName();
                        }
                        if (interview.getApplication().getJob() != null) {
                            jobTitle = interview.getApplication().getJob().getTitle();
                        }
                    }
                    return InterviewResponse.builder()
                        .id(interview.getId())
                        .scheduledAt(interview.getScheduledAt())
                        .startedAt(interview.getStartedAt())
                        .endedAt(interview.getEndedAt())
                        .location(interview.getLocation())
                        .meetingLink(interview.getMeetingLink())
                        .type(interview.getType())
                        .status(interview.getStatus())
                        .applicationId(
                                interview.getApplication() != null ? interview.getApplication().getId() : null
                        )
                        .templateId(
                                interview.getTemplate() != null ? interview.getTemplate().getId() : null
                        )
                        .participantCount(
                                interview.getParticipants() != null ? interview.getParticipants().size() : 0
                        )
                        .candidateName(candidateName)
                        .jobTitle(jobTitle)
                        .scoreCount(
                                interview.getScores() != null ? interview.getScores().size() : 0
                        )
                        .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public InterviewDetailResponse getInterviewDetail(UUID interviewId, String email) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new BusinessException("Interview not found", HttpStatus.NOT_FOUND));

        if (email != null) {
            User user = userRepository.findByEmailAndDeletedFalse(email).orElse(null);
            if (user != null && user.getRole().name().equals("INTERVIEWER")) {
                boolean isParticipant = interview.getParticipants() != null && interview.getParticipants().stream()
                        .anyMatch(p -> p.getUser().getId().equals(user.getId()));
                if (!isParticipant) {
                    throw new BusinessException("You are not a participant of this interview", HttpStatus.FORBIDDEN);
                }
            }
        }

        UUID candidateId = null;
        String candidateName = null;
        String candidateEmail = null;
        String candidatePhone = null;
        String candidateResumeUrl = null;

        UUID jobId = null;
        String jobTitle = null;
        String jobDepartment = null;

        Application app = interview.getApplication();
        if (app != null) {
            Candidate c = app.getCandidate();
            if (c != null) {
                candidateId = c.getId();
                candidateName = c.getFullName();
                candidateEmail = c.getEmail();
                candidatePhone = c.getPhone();

                List<CandidateDocument> docs = candidateDocumentRepository.findByCandidateIdOrderByUploadedAtDesc(candidateId);
                candidateResumeUrl = docs.stream()
                        .filter(d -> "RESUME".equalsIgnoreCase(d.getFileType()))
                        .map(CandidateDocument::getFileUrl)
                        .findFirst()
                        .orElse(docs.isEmpty() ? null : docs.get(0).getFileUrl());
            }

            Job j = app.getJob();
            if (j != null) {
                jobId = j.getId();
                jobTitle = j.getTitle();
                if (j.getDepartment() != null) {
                    jobDepartment = j.getDepartment().getName();
                }
            }
        }

        List<ParticipantResponse> participants = new ArrayList<>();
        if (interview.getParticipants() != null) {
            participants = interview.getParticipants().stream()
                    .map(p -> ParticipantResponse.builder()
                            .userId(p.getUser().getId())
                            .fullName(p.getUser().getFullName())
                            .avatarUrl(null)
                            .role(p.getRole())
                            .build())
                    .toList();
        }

        return InterviewDetailResponse.builder()
                .id(interview.getId())
                .scheduledAt(interview.getScheduledAt())
                .startedAt(interview.getStartedAt())
                .endedAt(interview.getEndedAt())
                .location(interview.getLocation())
                .meetingLink(interview.getMeetingLink())
                .type(interview.getType())
                .status(interview.getStatus())
                .applicationId(app != null ? app.getId() : null)
                .templateId(interview.getTemplate() != null ? interview.getTemplate().getId() : null)
                .candidateId(candidateId)
                .candidateName(candidateName)
                .candidateEmail(candidateEmail)
                .candidatePhone(candidatePhone)
                .candidateResumeUrl(candidateResumeUrl)
                .jobId(jobId)
                .jobTitle(jobTitle)
                .jobDepartment(jobDepartment)
                .participants(participants)
                .build();
    }
    @Transactional
    @Override
    public void submitFeedback(SubmitFeedbackRequest req) {
        InterviewParticipant p = participantRepository
                .findByInterviewIdAndUserId(req.getInterviewId(), req.getInterviewerId())
                .orElseThrow(() -> new BusinessException("Not participant", HttpStatus.FORBIDDEN));

        if (p.getRole() != ParticipantRole.INTERVIEWER) {
            throw new BusinessException("Not interviewer", HttpStatus.FORBIDDEN);
        }

        List<CriterionScoreRequest> scoreReqs = req.getScores();
        if (scoreReqs == null || scoreReqs.isEmpty()) {
            throw new BusinessException("Scores cannot be empty", HttpStatus.BAD_REQUEST);
        }
        
        for (CriterionScoreRequest sr : scoreReqs) {
            validate(sr.getScore());
        }
        
        scoreRepository.deleteOld(req.getInterviewId(), req.getInterviewerId());

        Interview interview = p.getInterview();
        User interviewer = p.getUser();
        
        // 4. save raw score
        List<InterviewScore> list = new ArrayList<>();
        
        for (CriterionScoreRequest sr : scoreReqs) {
            ScorecardCriterion criterion = criterionRepository.findById(sr.getCriterionId())
                    .orElseThrow(() -> new BusinessException("Criterion " + sr.getCriterionId() + " not found", HttpStatus.BAD_REQUEST));
                    
            list.add(InterviewScore.builder()
                        .interview(interview)
                        .interviewer(interviewer)
                        .criterion(criterion)
                        .score(sr.getScore())
                        .build());
        }

        scoreRepository.saveAll(list);

        //  5. calculate overallScore của interviewer
        BigDecimal overall = calculateWeightedInterviewerScore(list);

        p.setOverallScore(overall);
        p.setFeedback(req.getFeedback());

        // Update interview status to COMPLETED
        interview.setStatus(InterviewStatus.COMPLETED);
        interviewRepository.save(interview);
    }

    @Override
    public BigDecimal calculateFinalScore(Interview interview) {
        List<InterviewParticipant> interviewers =
                interview.getParticipants().stream()
                        .filter(p -> p.getRole() == ParticipantRole.INTERVIEWER)
                        .filter(p -> p.getOverallScore() != null)
                        .toList();

        if (interviewers.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = interviewers.stream()
                .map(InterviewParticipant::getOverallScore)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return total.divide(
                BigDecimal.valueOf(interviewers.size()),
                2,
                RoundingMode.HALF_UP
        );
    }

    @Override
    public Interview getInterviewById(UUID interviewId) {
        return interviewRepository.findByIdWithParticipants(interviewId)
                .orElseThrow(() -> new BusinessException("Interview not found", HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional
    public ScorecardTemplateResponse getTemplateByInterviewId(UUID interviewId) {
        Interview interview = getInterviewById(interviewId);
        ScorecardTemplate template = interview.getTemplate();
        
        if (template == null) {
            throw new BusinessException("Interview does not have a template assigned", HttpStatus.BAD_REQUEST);
        }

        List<ScorecardCriterionResponse> criterionResponses = template.getCriteria().stream()
                .map(c -> ScorecardCriterionResponse.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .weight(c.getWeight())
                        .build())
                .toList();

        return ScorecardTemplateResponse.builder()
                .id(template.getId())
                .name(template.getName())
                .departmentId(template.getDepartment() != null ? template.getDepartment().getId() : null)
                .departmentName(template.getDepartment() != null ? template.getDepartment().getName() : null)
                .criteria(criterionResponses)
                .build();
    }

    private BigDecimal calculateWeightedInterviewerScore(List<InterviewScore> scores) {
        if (scores == null || scores.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal totalScoreWeight = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;

        for (InterviewScore s : scores) {
            BigDecimal weight = s.getCriterion().getWeight();
            if (weight == null) {
                weight = BigDecimal.ONE;
            }
            BigDecimal score = BigDecimal.valueOf(s.getScore());
            
            totalScoreWeight = totalScoreWeight.add(score.multiply(weight));
            totalWeight = totalWeight.add(weight);
        }

        if (totalWeight.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return totalScoreWeight.divide(totalWeight, 2, RoundingMode.HALF_UP);
    }

    private void validate(Integer score) {
        if (score == null || score < 1 || score > 5) {
            throw new BusinessException("Score must be 1-5", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public CandidateEvaluationResponse getInterviewEvaluationSummary(UUID interviewId) {
        Interview interview = getInterviewById(interviewId);
        
        BigDecimal finalScore = calculateFinalScore(interview);

        List<InterviewerEvaluationResponse> interviewers = interview.getParticipants().stream()
                .filter(p -> p.getRole() == ParticipantRole.INTERVIEWER)
                .map(p -> InterviewerEvaluationResponse.builder()
                        .interviewerId(p.getUser().getId())
                        .interviewerName(p.getUser().getFullName())
                        .feedback(p.getFeedback())
                        .overallScore(p.getOverallScore())
                        .build())
                .toList();

        Map<ScorecardCriterion, List<InterviewScore>> scoresByCriterion = interview.getScores().stream()
                .collect(Collectors.groupingBy(InterviewScore::getCriterion));
                
        List<CriterionEvaluationResponse> criteria = scoresByCriterion.entrySet().stream()
                .map(entry -> {
                    ScorecardCriterion criterion = entry.getKey();
                    List<InterviewScore> userScores = entry.getValue();
                    
                    BigDecimal sum = userScores.stream()
                            .map(s -> BigDecimal.valueOf(s.getScore()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                            
                    BigDecimal avg = sum.divide(BigDecimal.valueOf(userScores.size()), 2, RoundingMode.HALF_UP);
                    
                    return CriterionEvaluationResponse.builder()
                            .criterionId(criterion.getId())
                            .criterionName(criterion.getName())
                            .weight(criterion.getWeight())
                            .averageScore(avg)
                            .build();
                })
                .toList();

        return CandidateEvaluationResponse.builder()
                .interviewId(interviewId)
                .finalScore(finalScore)
                .criteria(criteria)
                .interviewers(interviewers)
                .build();
    }

    @Override
    public ApplicationEvaluationResponse getApplicationEvaluation(UUID applicationId) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new BusinessException("Application not found", HttpStatus.NOT_FOUND));

        List<Interview> interviews = interviewRepository.findByApplicationId(applicationId);
        
        List<InterviewStageEvaluationResponse> stages = new ArrayList<>();
        BigDecimal totalScore = BigDecimal.ZERO;
        int completedCount = 0;

        for (Interview interview : interviews) {
            BigDecimal score = calculateFinalScore(interview);
            String interviewerName = "N/A";
            String feedback = "";

            if (interview.getParticipants() != null) {
                // Get first interviewer name for summary
                interviewerName = interview.getParticipants().stream()
                        .filter(p -> p.getRole() == ParticipantRole.INTERVIEWER)
                        .map(p -> p.getUser().getFullName())
                        .findFirst()
                        .orElse("N/A");
                
                // Get feedback from first interviewer who provided it
                feedback = interview.getParticipants().stream()
                        .filter(p -> p.getRole() == ParticipantRole.INTERVIEWER)
                        .map(InterviewParticipant::getFeedback)
                        .filter(f -> f != null && !f.isEmpty())
                        .findFirst()
                        .orElse("");
            }

            boolean isCompleted = interview.getStatus() == InterviewStatus.COMPLETED || score.compareTo(BigDecimal.ZERO) > 0;

            if (isCompleted) {
                totalScore = totalScore.add(score);
                completedCount++;
            }

            stages.add(InterviewStageEvaluationResponse.builder()
                    .interviewId(interview.getId())
                    .type(interview.getType())
                    .status(isCompleted ? InterviewStatus.COMPLETED : interview.getStatus())
                    .score(score)
                    .interviewerName(interviewerName)
                    .scheduledAt(interview.getScheduledAt())
                    .feedbackSnippet(feedback)
                    .build());
        }

        BigDecimal overallScore = completedCount > 0 
                ? totalScore.divide(BigDecimal.valueOf(completedCount), 1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        String recommendation = "Pending";
        if (completedCount > 0) {
            recommendation = overallScore.compareTo(new BigDecimal("3.5")) >= 0 ? "Hire" : "No Hire";
        }

        return ApplicationEvaluationResponse.builder()
                .applicationId(applicationId)
                .candidateName(app.getCandidate() != null ? app.getCandidate().getFullName() : "Unknown")
                .jobTitle(app.getJob() != null ? app.getJob().getTitle() : "Unknown")
                .overallScore(overallScore)
                .recommendation(recommendation)
                .interviewsCompleted(completedCount)
                .totalInterviews(interviews.size())
                .stages(stages)
                .build();
    }
}
