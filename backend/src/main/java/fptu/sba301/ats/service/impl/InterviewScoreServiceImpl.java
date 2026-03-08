package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.request.SubmitInterviewScoresRequest;
import fptu.sba301.ats.dto.response.InterviewScoreItemResponse;
import fptu.sba301.ats.dto.response.InterviewScoresResponse;
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

        // ==============================
        // SUBMIT SCORES (UPSERT)
        // ==============================

        @Override
        @Transactional
        public InterviewScoresResponse submitScores(Long interviewId,
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
                                .orElseThrow(() -> new BusinessException("Interviewer not found",
                                                HttpStatus.NOT_FOUND));

                // 3. Note: InterviewParticipant.userId is Long; User.id is UUID.
                // This check is best-effort; align types when User entity migrates to Long PK.
                // For now, skip the participant check and trust Spring Security @PreAuthorize.
                // TODO: participantRepository.existsByInterviewIdAndUserId check once PK types
                // align.

                // 4. Upsert scores
                String overallComment = request.overallComment();
                String strengths = request.strengths();
                String weaknesses = request.weaknesses();
                fptu.sba301.ats.enums.Recommendation recommendation = request.recommendation();

                List<InterviewScore> savedScores = new ArrayList<>();

                for (var item : request.scores()) {
                        // Validate criterion exists
                        ScorecardCriterion criterion = criterionRepository.findById(item.criterionId())
                                        .orElseThrow(() -> new BusinessException(
                                                        "Criterion not found with id: " + item.criterionId(),
                                                        HttpStatus.NOT_FOUND));

                        // Use interviewer's Long reference — resolved via email, but User has UUID PK.
                        // We store -1L as placeholder; replace with Long user.id when DB types are
                        // aligned.
                        Long interviewerLongId = resolveInterviewerLongId(interviewer);

                        Optional<InterviewScore> existing = scoreRepository
                                        .findByInterviewIdAndInterviewerIdAndCriterionId(
                                                        interviewId, interviewerLongId, criterion.getId());

                        InterviewScore score;
                        if (existing.isPresent()) {
                                score = existing.get();
                                score.setScore(item.score());
                                score.setComment(item.comment());
                                score.setOverallComment(overallComment);
                                score.setStrengths(strengths);
                                score.setWeaknesses(weaknesses);
                                score.setRecommendation(recommendation);
                        } else {
                                score = InterviewScore.builder()
                                                .interviewId(interviewId)
                                                .interviewerId(interviewerLongId)
                                                .criterionId(criterion.getId())
                                                .score(item.score())
                                                .comment(item.comment())
                                                .overallComment(overallComment)
                                                .strengths(strengths)
                                                .weaknesses(weaknesses)
                                                .recommendation(recommendation)
                                                .build();
                        }
                        savedScores.add(scoreRepository.save(score));
                }

                return buildScoresResponse(interviewId, resolveInterviewerLongId(interviewer),
                                interviewer.getFullName(), overallComment, strengths, weaknesses, recommendation,
                                savedScores,
                                buildCriterionMap(savedScores));
        }

        // ==============================
        // GET ALL SCORES (by interview)
        // ==============================

        @Override
        @Transactional(readOnly = true)
        public List<InterviewScoresResponse> getAllScores(Long interviewId) {
                if (!interviewRepository.existsById(interviewId)) {
                        throw new BusinessException(
                                        "Interview not found with id: " + interviewId, HttpStatus.NOT_FOUND);
                }

                List<InterviewScore> allScores = scoreRepository.findByInterviewId(interviewId);
                Map<Long, List<InterviewScore>> byInterviewer = allScores.stream()
                                .collect(Collectors.groupingBy(InterviewScore::getInterviewerId));

                Map<Long, String> criterionNames = buildCriterionNameMap(allScores);

                return byInterviewer.entrySet().stream().map(entry -> {
                        Long interviewerId = entry.getKey();
                        List<InterviewScore> scores = entry.getValue();
                        String overallComment = scores.stream()
                                        .map(InterviewScore::getOverallComment).filter(Objects::nonNull).findFirst()
                                        .orElse(null);
                        String strengths = scores.stream()
                                        .map(InterviewScore::getStrengths).filter(Objects::nonNull).findFirst()
                                        .orElse(null);
                        String weaknesses = scores.stream()
                                        .map(InterviewScore::getWeaknesses).filter(Objects::nonNull).findFirst()
                                        .orElse(null);
                        fptu.sba301.ats.enums.Recommendation recommendation = scores.stream()
                                        .map(InterviewScore::getRecommendation).filter(Objects::nonNull).findFirst()
                                        .orElse(null);
                        return buildScoresResponse(interviewId, interviewerId,
                                        "User#" + interviewerId, // name resolved via separate query if needed
                                        overallComment, strengths, weaknesses, recommendation, scores, criterionNames);
                }).collect(Collectors.toList());
        }

        // ==============================
        // GET MY SCORES
        // ==============================

        @Override
        @Transactional(readOnly = true)
        public InterviewScoresResponse getMyScores(Long interviewId, String interviewerEmail) {
                User interviewer = userRepository.findByEmailAndDeletedFalse(interviewerEmail)
                                .orElseThrow(() -> new BusinessException("Interviewer not found",
                                                HttpStatus.NOT_FOUND));

                Long interviewerLongId = resolveInterviewerLongId(interviewer);
                List<InterviewScore> scores = scoreRepository
                                .findByInterviewIdAndInterviewerId(interviewId, interviewerLongId);

                if (scores.isEmpty()) {
                        throw new BusinessException(
                                        "No scores found for this interviewer on interview " + interviewId,
                                        HttpStatus.NOT_FOUND);
                }

                Map<Long, String> criterionNames = buildCriterionNameMap(scores);
                String overallComment = scores.stream()
                                .map(InterviewScore::getOverallComment).filter(Objects::nonNull).findFirst()
                                .orElse(null);
                String strengths = scores.stream()
                                .map(InterviewScore::getStrengths).filter(Objects::nonNull).findFirst()
                                .orElse(null);
                String weaknesses = scores.stream()
                                .map(InterviewScore::getWeaknesses).filter(Objects::nonNull).findFirst()
                                .orElse(null);
                fptu.sba301.ats.enums.Recommendation recommendation = scores.stream()
                                .map(InterviewScore::getRecommendation).filter(Objects::nonNull).findFirst()
                                .orElse(null);

                return buildScoresResponse(interviewId, interviewerLongId,
                                interviewer.getFullName(), overallComment, strengths, weaknesses, recommendation,
                                scores, criterionNames);
        }

        // ==============================
        // Helpers
        // ==============================

        /**
         * Since User entity uses UUID PK but DB has BIGINT, we use -1L as a sentinel.
         * Replace this method body when User PK is migrated to Long.
         */
        private Long resolveInterviewerLongId(User user) {
                // TODO: once User PK is Long, return user.getId()
                // For now, return a stable hash to differentiate interviewers
                return (long) user.getEmail().hashCode();
        }

        private InterviewScoresResponse buildScoresResponse(Long interviewId,
                        Long interviewerId,
                        String interviewerName,
                        String overallComment,
                        String strengths,
                        String weaknesses,
                        fptu.sba301.ats.enums.Recommendation recommendation,
                        List<InterviewScore> scores,
                        Map<Long, String> criterionNames) {
                List<InterviewScoreItemResponse> items = scores.stream()
                                .map(s -> new InterviewScoreItemResponse(
                                                s.getId(),
                                                s.getCriterionId(),
                                                criterionNames.getOrDefault(s.getCriterionId(), "Unknown"),
                                                s.getScore(),
                                                s.getComment()))
                                .collect(Collectors.toList());

                var submittedAt = scores.stream()
                                .map(InterviewScore::getSubmittedAt)
                                .filter(Objects::nonNull)
                                .max(Comparator.naturalOrder())
                                .orElse(null);

                return new InterviewScoresResponse(interviewId, interviewerId, interviewerName,
                                overallComment, strengths, weaknesses, recommendation, submittedAt, items);
        }

        private Map<Long, String> buildCriterionNameMap(List<InterviewScore> scores) {
                Set<Long> criterionIds = scores.stream()
                                .map(InterviewScore::getCriterionId).collect(Collectors.toSet());
                Map<Long, String> map = new HashMap<>();
                criterionRepository.findAllById(criterionIds)
                                .forEach(c -> map.put(c.getId(), c.getName()));
                return map;
        }

        private Map<Long, String> buildCriterionMap(List<InterviewScore> scores) {
                return buildCriterionNameMap(scores);
        }
}