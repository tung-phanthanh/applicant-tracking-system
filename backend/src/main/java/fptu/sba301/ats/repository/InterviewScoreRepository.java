package fptu.sba301.ats.repository;

import fptu.sba301.ats.entity.InterviewScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InterviewScoreRepository extends JpaRepository<InterviewScore, UUID> {
    List<InterviewScore> findByInterviewId(UUID interviewId);
    List<InterviewScore> findByInterviewIdAndUserId(UUID interviewId, UUID userId);
}
