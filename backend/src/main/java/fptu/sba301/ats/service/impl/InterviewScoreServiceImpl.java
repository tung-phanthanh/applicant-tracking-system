package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.request.SubmitInterviewScoresRequest;
import fptu.sba301.ats.dto.response.InterviewScoreItemResponse;
import fptu.sba301.ats.dto.response.InterviewScoresResponse;
import fptu.sba301.ats.entity.InterviewParticipant;
import fptu.sba301.ats.entity.InterviewScore;
import fptu.sba301.ats.entity.ScorecardCriterion;
import fptu.sba301.ats.entity.User;
import fptu.sba301.ats.enums.InterviewStatus;
import fptu.sba301.ats.exception.BusinessException;
import fptu.sba301.ats.repository.*;
import fptu.sba301.ats.service.InterviewScoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class InterviewScoreServiceImpl implements InterviewScoreService {

    private final InterviewRepository interviewRepository;
    private final InterviewParticipantRepository participantRepository;
    private final InterviewScoreRepository scoreRepository;
    private final ScorecardCriterionRepository criterionRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public InterviewScoresResponse submitScores(java.util.UUID interviewId,
                                                SubmitInterviewScoresRequest request,
                                                String interviewerEmail) {

        // 1. Verify interview exists and is COMPLETED
        var interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new BusinessException(
                        "Interview not found with id: " + interviewId, HttpStatus.NOT_FOUND));

        if (interview.getStatus() != InterviewStatus.COMPLETED) {
            throw new BusinessException(
                    "Scores can only be submitted for COMPLETED interviews", HttpStatus.CONFLICT);
        }

        // 2. Resolve interviewer
        User interviewer = userRepository.findByEmailAndDeletedFalse(interviewerEmail)
                .orElseThrow(() -> new BusinessException("Interviewer not found", HttpStatus.NOT_FOUND));

        // 3. Update Participant
        InterviewParticipant participant = participantRepository.findById(
                new InterviewParticipant.InterviewParticipantId(interviewId, interviewer.getId()))
                .orElseThrow(() -> new BusinessException("Interviewer is not a participant of this interview", HttpStatus.FORBIDDEN));

        participant.setOverallComment(request.overallComment());
        participant.setStrengths(request.strengths());
        participant.setWeaknesses(request.weaknesses());
        participant.setRecommendation(request.recommendation());
        participant.setSubmittedAt(Instant.now());
        participantRepository.save(participant);

        // 4. Upsert scores
        List<InterviewScore> savedScores = new ArrayList<>();

        for (var item : request.scores()) {
            ScorecardCriterion criterion = criterionRepository.findById(item.criterionId())
                    .orElseThrow(() -> new BusinessException(
                            "Criterion not found with id: " + item.criterionId(), HttpStatus.NOT_FOUND));

            // In our InterviewScore entity, participant is mapped via interview_id and user_id fields
            // Wait, does InterviewScore have setting for those fields directly or via Participant relation?
            // The InterviewScore entity has: participant, criterion. 
            // So we just need to set the participant and criterion.

            Optional<InterviewScore> existingOpt = scoreRepository.findByInterviewIdAndParticipantKeyAndCriterionId(
                    interviewId, interviewer.getId(), criterion.getId()
            );

            InterviewScore score;
            if (existingOpt.isPresent()) {
                score = existingOpt.get();
                score.setScore(item.score());
                score.setComment(item.comment());
            } else {
                score = InterviewScore.builder()
                        .interview(interview)
                        .participant(participant)
                        .criterion(criterion)
                        .score(item.score())
                        .comment(item.comment())
                        .build();
            }
            savedScores.add(scoreRepository.save(score));
        }

        return buildScoresResponse(interviewId, interviewer.getId(),
                interviewer.getFullName(), participant.getOverallComment(), participant.getStrengths(), 
                participant.getWeaknesses(), participant.getRecommendation(),
                savedScores, buildCriterionMap(savedScores));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InterviewScoresResponse> getAllScores(java.util.UUID interviewId) {
        if (!interviewRepository.existsById(interviewId)) {
            throw new BusinessException(
                    "Interview not found with id: " + interviewId, HttpStatus.NOT_FOUND);
        }

        List<InterviewScore> allScores = scoreRepository.findAll()
                .stream()
                .filter(s -> s.getInterview().getId().equals(interviewId))
                .collect(Collectors.toList());

        Map<java.util.UUID, List<InterviewScore>> byInterviewer = allScores.stream()
                .collect(Collectors.groupingBy(s -> s.getParticipant().getUser().getId()));

        Map<java.util.UUID, String> criterionNames = buildCriterionNameMap(allScores);

        // Get participants
        List<InterviewParticipant> participants = participantRepository.findAll()
                .stream()
                .filter(p -> p.getId().getInterviewId().equals(interviewId))
                .collect(Collectors.toList());
        Map<java.util.UUID, InterviewParticipant> participantMap = participants.stream()
                .collect(Collectors.toMap(p -> p.getUser().getId(), p -> p));

        return byInterviewer.entrySet().stream().map(entry -> {
            java.util.UUID interviewerId = entry.getKey();
            List<InterviewScore> scores = entry.getValue();
            InterviewParticipant p = participantMap.get(interviewerId);
            
            return buildScoresResponse(interviewId, interviewerId,
                    p != null ? p.getUser().getFullName() : "User#" + interviewerId,
                    p != null ? p.getOverallComment() : null,
                    p != null ? p.getStrengths() : null,
                    p != null ? p.getWeaknesses() : null,
                    p != null ? p.getRecommendation() : null, 
                    scores, criterionNames);
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public InterviewScoresResponse getMyScores(java.util.UUID interviewId, String interviewerEmail) {
        User interviewer = userRepository.findByEmailAndDeletedFalse(interviewerEmail)
                .orElseThrow(() -> new BusinessException("Interviewer not found", HttpStatus.NOT_FOUND));

        List<InterviewScore> scores = scoreRepository.findAll()
                .stream()
                .filter(s -> s.getInterview().getId().equals(interviewId) && 
                             s.getParticipant().getUser().getId().equals(interviewer.getId()))
                .collect(Collectors.toList());

        if (scores.isEmpty()) {
            throw new BusinessException(
                    "No scores found for this interviewer on interview " + interviewId, HttpStatus.NOT_FOUND);
        }

        InterviewParticipant p = participantRepository.findById(
                new InterviewParticipant.InterviewParticipantId(interviewId, interviewer.getId())).orElse(null);

        Map<java.util.UUID, String> criterionNames = buildCriterionNameMap(scores);
        return buildScoresResponse(interviewId, interviewer.getId(),
                interviewer.getFullName(), 
                p != null ? p.getOverallComment() : null, 
                p != null ? p.getStrengths() : null, 
                p != null ? p.getWeaknesses() : null, 
                p != null ? p.getRecommendation() : null,
                scores, criterionNames);
    }

    private InterviewScoresResponse buildScoresResponse(java.util.UUID interviewId,
                                                        java.util.UUID interviewerId,
                                                        String interviewerName,
                                                        String overallComment,
                                                        String strengths,
                                                        String weaknesses,
                                                        fptu.sba301.ats.enums.Recommendation recommendation,
                                                        List<InterviewScore> scores,
                                                        Map<java.util.UUID, String> criterionNames) {
        List<InterviewScoreItemResponse> items = scores.stream()
                .map(s -> new InterviewScoreItemResponse(
                        s.getId(),
                        s.getCriterion().getId(),
                        criterionNames.getOrDefault(s.getCriterion().getId(), "Unknown"),
                        s.getScore(),
                        s.getComment()))
                .collect(Collectors.toList());

        return new InterviewScoresResponse(interviewId, interviewerId, interviewerName,
                overallComment, strengths, weaknesses, recommendation, null, items);
    }

    private Map<java.util.UUID, String> buildCriterionNameMap(List<InterviewScore> scores) {
        Set<java.util.UUID> criterionIds = scores.stream()
                .map(s -> s.getCriterion().getId()).collect(Collectors.toSet());
        Map<java.util.UUID, String> map = new HashMap<>();
        criterionRepository.findAllById(criterionIds)
                .forEach(c -> map.put(c.getId(), c.getName()));
        return map;
    }

    private Map<java.util.UUID, String> buildCriterionMap(List<InterviewScore> scores) {
        return buildCriterionNameMap(scores);
    }
}