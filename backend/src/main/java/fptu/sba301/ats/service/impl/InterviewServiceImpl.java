package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.request.SubmitFeedbackRequest;
import fptu.sba301.ats.dto.request.SubmitInterviewScoreRequest;
import fptu.sba301.ats.dto.response.InterviewResponse;
import fptu.sba301.ats.dto.response.InterviewScorecardResponse;
import fptu.sba301.ats.entity.*;
import fptu.sba301.ats.enums.ParticipantRole;
import fptu.sba301.ats.exception.BusinessException;
import fptu.sba301.ats.repository.*;
import fptu.sba301.ats.service.InterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {

    private final InterviewRepository interviewRepository;
    private final InterviewScoreRepository scoreRepository;
    private final InterviewParticipantRepository participantRepository;
    private final ScorecardCriterionRepository criterionRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void submitScores(UUID interviewId, SubmitInterviewScoreRequest request, String userEmail) {
        User user = userRepository.findByEmailAndDeletedFalse(userEmail)
                .orElseThrow(() -> new BusinessException("User not found", HttpStatus.NOT_FOUND));

        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new BusinessException("Interview not found", HttpStatus.NOT_FOUND));

        // Check the user is a participant
        InterviewParticipant participant = participantRepository
                .findByIdInterviewIdAndIdUserId(interviewId, user.getId())
                .orElseThrow(() -> new BusinessException("You are not a participant of this interview", HttpStatus.FORBIDDEN));

        // Check no duplicate submission
        List<InterviewScore> existingScores = scoreRepository
                .findByInterviewIdAndUserId(interviewId, user.getId());
        if (!existingScores.isEmpty()) {
            throw new BusinessException("You have already submitted scores for this interview", HttpStatus.CONFLICT);
        }

        // Save scores
        List<InterviewScore> scores = request.getScores().stream()
                .map(entry -> {
                    ScorecardCriterion criterion = criterionRepository.findById(entry.getCriterionId())
                            .orElseThrow(() -> new BusinessException(
                                    "Criterion not found: " + entry.getCriterionId(), HttpStatus.NOT_FOUND));

                    return InterviewScore.builder()
                            .interview(interview)
                            .userId(user.getId())
                            .criterion(criterion)
                            .score(entry.getScore())
                            .comment(entry.getComment())
                            .build();
                })
                .collect(Collectors.toList());

        scoreRepository.saveAll(scores);

        // Compute and save overall score on participant
        int totalScore = request.getScores().stream()
                .mapToInt(SubmitInterviewScoreRequest.ScoreEntry::getScore)
                .sum();
        int avgScore = totalScore / request.getScores().size();
        participant.setOverallScore(avgScore);
        participantRepository.save(participant);
    }

    @Override
    public InterviewScorecardResponse getMyScorecard(UUID interviewId, String userEmail) {
        User user = userRepository.findByEmailAndDeletedFalse(userEmail)
                .orElseThrow(() -> new BusinessException("User not found", HttpStatus.NOT_FOUND));

        List<InterviewScore> scores = scoreRepository
                .findByInterviewIdAndUserId(interviewId, user.getId());

        InterviewParticipant participant = participantRepository
                .findByIdInterviewIdAndIdUserId(interviewId, user.getId())
                .orElseThrow(() -> new BusinessException("You are not a participant of this interview", HttpStatus.FORBIDDEN));

        return buildScorecardResponse(interviewId, user, participant, scores);
    }

    @Override
    public List<InterviewScorecardResponse> getAllScorecards(UUID interviewId) {
        if (!interviewRepository.existsById(interviewId)) {
            throw new BusinessException("Interview not found", HttpStatus.NOT_FOUND);
        }

        List<InterviewParticipant> participants = participantRepository.findByIdInterviewId(interviewId);
        List<InterviewScore> allScores = scoreRepository.findByInterviewId(interviewId);

        // Group scores by userId
        Map<UUID, List<InterviewScore>> scoresByUser = allScores.stream()
                .collect(Collectors.groupingBy(InterviewScore::getUserId));

        return participants.stream()
                .map(p -> {
                    List<InterviewScore> userScores = scoresByUser.getOrDefault(
                            p.getId().getUserId(), Collections.emptyList());
                    return buildScorecardResponse(interviewId, p.getUser(), p, userScores);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<InterviewResponse> getAllInterviews() {
        return interviewRepository.findAll().stream()
                .map(interview -> InterviewResponse.builder()
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
                        .scoreCount(
                                interview.getScores() != null ? interview.getScores().size() : 0
                        )
                        .build())
                .toList();
    }
    @Transactional
    @Override
    public void submitFeedback(SubmitFeedbackRequest req) {
        // 1. load participant
        InterviewParticipant p = participantRepository
                .findByInterviewIdAndUserId(req.getInterviewId(), req.getInterviewerId())
                .orElseThrow(() -> new RuntimeException("Not participant"));

        if (p.getRole() != ParticipantRole.INTERVIEWER) {
            throw new RuntimeException("Not interviewer");
        }

        validate(req.getTechnicalScore());
        validate(req.getCommunicationScore());

        // 2. delete old score
        scoreRepository.deleteOld(req.getInterviewId(), req.getInterviewerId());

        Interview interview = p.getInterview();
        User interviewer = p.getUser();

        // 3. get criteria
        ScorecardCriterion tech = criterionRepository.findByName("Technical")
                .orElseThrow();

        ScorecardCriterion comm = criterionRepository.findByName("Communication")
                .orElseThrow();

        // 4. save raw score
        List<InterviewScore> list = List.of(
                InterviewScore.builder()
                        .interview(interview)
                        .interviewer(interviewer)
                        .criterion(tech)
                        .score(req.getTechnicalScore())
                        .build(),

                InterviewScore.builder()
                        .interview(interview)
                        .interviewer(interviewer)
                        .criterion(comm)
                        .score(req.getCommunicationScore())
                        .build()
        );

        scoreRepository.saveAll(list);

        //  5. calculate overallScore của interviewer
        BigDecimal overall = calculateInterviewerScore(list);

        p.setOverallScore(overall);
        p.setFeedback(req.getFeedback());
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
                .orElseThrow(() -> new RuntimeException("Interview not found"));
    }

    //  avg của 1 interviewer
    private BigDecimal calculateInterviewerScore(List<InterviewScore> scores) {

        return scores.stream()
                .map(s -> BigDecimal.valueOf(s.getScore()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(scores.size()), 2, RoundingMode.HALF_UP);
    }

    private void validate(Integer score) {
        if (score == null || score < 1 || score > 5) {
            throw new RuntimeException("Score must be 1-5");
        }
    }

    private InterviewScorecardResponse buildScorecardResponse(
            UUID interviewId, User user, InterviewParticipant participant,
            List<InterviewScore> scores) {
        return InterviewScorecardResponse.builder()
                .interviewId(interviewId)
                .participantUserId(user.getId())
                .participantName(user.getFullName())
                .overallScore(participant.getOverallScore())
                .feedback(participant.getFeedback())
                .scores(scores.stream()
                        .map(s -> InterviewScorecardResponse.ScoreDetail.builder()
                                .criterionId(s.getCriterion().getId())
                                .criterionName(s.getCriterion().getName())
                                .weight(s.getCriterion().getWeight())
                                .score(s.getScore())
                                .comment(s.getComment())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
