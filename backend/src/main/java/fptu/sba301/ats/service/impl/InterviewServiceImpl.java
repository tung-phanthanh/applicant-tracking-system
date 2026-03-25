package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.response.InterviewScorecardResponse;
import fptu.sba301.ats.entity.InterviewParticipant;
import fptu.sba301.ats.entity.InterviewScore;
import fptu.sba301.ats.repository.InterviewParticipantRepository;
import fptu.sba301.ats.repository.InterviewScoreRepository;
import fptu.sba301.ats.service.InterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {

    private final InterviewParticipantRepository participantRepository;
    private final InterviewScoreRepository interviewScoreRepository;

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
                            .findByInterviewIdAndUserId(interviewId, userId);

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
}
