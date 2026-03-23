package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.request.SubmitInterviewScoreRequest;
import fptu.sba301.ats.dto.response.InterviewScorecardResponse;
import fptu.sba301.ats.entity.*;
import fptu.sba301.ats.exception.BusinessException;
import fptu.sba301.ats.repository.*;
import fptu.sba301.ats.service.InterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
