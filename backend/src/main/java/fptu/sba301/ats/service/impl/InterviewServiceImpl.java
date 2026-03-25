package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.request.SubmitFeedbackRequest;
import fptu.sba301.ats.dto.request.SubmitInterviewScoreRequest;
import fptu.sba301.ats.dto.response.InterviewResponse;
import fptu.sba301.ats.dto.response.InterviewScorecardResponse;
import fptu.sba301.ats.entity.*;
import fptu.sba301.ats.enums.ParticipantRole;
import fptu.sba301.ats.exception.BusinessException;
import fptu.sba301.ats.repository.*;
import fptu.sba301.ats.entity.InterviewParticipant;
import fptu.sba301.ats.entity.InterviewScore;
import fptu.sba301.ats.repository.InterviewParticipantRepository;
import fptu.sba301.ats.repository.InterviewScoreRepository;
import fptu.sba301.ats.service.InterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {

    private final InterviewRepository interviewRepository;
    private final InterviewScoreRepository scoreRepository;
    private final InterviewParticipantRepository participantRepository;
    private final InterviewScoreRepository interviewScoreRepository;
    private final ScorecardCriterionRepository criterionRepository;

    @Override
    public List<InterviewScorecardResponse> getAllScorecards(UUID interviewId) {
        // Use participants as the primary source — they store overallScore and feedback per interviewer
        List<InterviewParticipant> participants = participantRepository.findByIdInterviewId(interviewId);

        return participants.stream()
                .filter(p -> p.getUser() != null)
                .map(p -> {
                    UUID userId = p.getUser().getId();

                    // Get per-criterion scores for this participant
                    List<InterviewScore> userScores = interviewScoreRepository
                            .findByInterview_IdAndInterviewer_Id(interviewId, userId);
                    List<InterviewScorecardResponse.ScoreDetail> details = userScores.stream()
                            .filter(s -> s.getCriterion() != null)
                            .map(s -> InterviewScorecardResponse.ScoreDetail.builder()
                                    .criterionId(s.getCriterion().getId())
                                    .criterionName(s.getCriterion().getName())
                                    .weight(s.getCriterion().getWeight())
                                    .score(s.getScore())
                                    .comment(s.getComment())
                                    .build())
                            .collect(Collectors.toList());

                    return InterviewScorecardResponse.builder()
                            .interviewId(interviewId)
                            .participantUserId(userId)
                            .participantName(p.getUser().getFullName())
                            .overallScore(p.getOverallScore())
                            .feedback(p.getFeedback())
                            .scores(details)
                            .build();
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
