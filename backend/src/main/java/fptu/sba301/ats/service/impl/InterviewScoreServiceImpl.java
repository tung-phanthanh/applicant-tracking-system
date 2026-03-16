package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.request.SubmitInterviewScoreRequest;
import fptu.sba301.ats.dto.response.InterviewScoreResponse;
import fptu.sba301.ats.entity.Interview;
import fptu.sba301.ats.entity.InterviewScore;
import fptu.sba301.ats.entity.ScorecardCriterion;
import fptu.sba301.ats.entity.User;
import fptu.sba301.ats.exception.BusinessException;
import fptu.sba301.ats.mapper.InterviewScoreMapper;
import fptu.sba301.ats.repository.InterviewScoreRepository;
import fptu.sba301.ats.repository.ScorecardCriterionRepository;
import fptu.sba301.ats.repository.UserRepository;
import fptu.sba301.ats.service.InterviewScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class InterviewScoreServiceImpl implements InterviewScoreService {

    private final InterviewScoreRepository scoreRepository;
    private final ScorecardCriterionRepository criterionRepository;
    private final UserRepository userRepository;
    private final InterviewScoreMapper scoreMapper;
    private final EntityManager entityManager;

    @Override
    public List<InterviewScoreResponse> submitScores(SubmitInterviewScoreRequest request, String interviewerEmail) {
        User interviewer = userRepository.findByEmail(interviewerEmail)
                .orElseThrow(() -> new BusinessException("Interviewer not found: " + interviewerEmail, HttpStatus.NOT_FOUND));

        Interview interview = entityManager.getReference(Interview.class, request.getInterviewId());

        List<InterviewScore> savedScores = new ArrayList<>();

        for (SubmitInterviewScoreRequest.ScoreEntryDto entry : request.getScores()) {
            ScorecardCriterion criterion = criterionRepository.findById(entry.getCriterionId())
                    .orElseThrow(() -> new BusinessException("Criterion not found: " + entry.getCriterionId(), HttpStatus.NOT_FOUND));

            InterviewScore score = InterviewScore.builder()
                    .interview(interview)
                    .interviewer(interviewer)
                    .criterion(criterion)
                    .score(entry.getScore())
                    .comment(entry.getComment())
                    .build();

            savedScores.add(scoreRepository.save(score));
        }

        return scoreMapper.toResponseList(savedScores);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InterviewScoreResponse> getScoresByInterview(UUID interviewId, Pageable pageable) {
        return scoreRepository.findByInterviewId(interviewId, pageable)
                .map(scoreMapper::toResponse);
    }
}
