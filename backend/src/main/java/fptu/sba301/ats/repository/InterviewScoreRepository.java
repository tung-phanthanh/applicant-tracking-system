package fptu.sba301.ats.repository;

import fptu.sba301.ats.entity.InterviewScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewScoreRepository extends JpaRepository<InterviewScore, Long> {

    List<InterviewScore> findByInterviewId(Long interviewId);

    List<InterviewScore> findByInterviewIdAndInterviewerId(Long interviewId, Long interviewerId);

    Optional<InterviewScore> findByInterviewIdAndInterviewerIdAndCriterionId(
            Long interviewId, Long interviewerId, Long criterionId);

    boolean existsByInterviewIdAndInterviewerIdAndCriterionId(
            Long interviewId, Long interviewerId, Long criterionId);

    @Query("SELECT s FROM InterviewScore s WHERE s.interviewId IN :interviewIds")
    List<InterviewScore> findByInterviewIdIn(@Param("interviewIds") List<Long> interviewIds);

    boolean existsByCriterionId(Long criterionId);
}
