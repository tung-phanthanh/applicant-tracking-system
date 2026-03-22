package fptu.sba301.ats.repository;

import fptu.sba301.ats.entity.InterviewScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InterviewScoreRepository extends JpaRepository<InterviewScore, UUID> {

    List<InterviewScore> findByInterviewId(UUID interviewId);

    @Query("SELECT s FROM InterviewScore s WHERE s.interview.id = :interviewId AND s.userId = :interviewerId")
    List<InterviewScore> findByInterviewIdAndInterviewerId(@Param("interviewId") UUID interviewId, @Param("interviewerId") UUID interviewerId);

    @Query("SELECT s FROM InterviewScore s WHERE s.interview.id = :interviewId AND s.userId = :userId AND s.criterion.id = :criterionId")
    Optional<InterviewScore> findByInterviewIdAndParticipantKeyAndCriterionId(
            @Param("interviewId") UUID interviewId, @Param("userId") UUID userId, @Param("criterionId") UUID criterionId);

    @Query("SELECT count(s) > 0 FROM InterviewScore s WHERE s.interview.id = :interviewId AND s.userId = :interviewerId AND s.criterion.id = :criterionId")
    boolean existsByInterviewIdAndInterviewerIdAndCriterionId(
            @Param("interviewId") UUID interviewId, @Param("interviewerId") UUID interviewerId, @Param("criterionId") UUID criterionId);

    @Query("SELECT s FROM InterviewScore s WHERE s.interview.id IN :interviewIds")
    List<InterviewScore> findByInterviewIdIn(@Param("interviewIds") List<UUID> interviewIds);

    boolean existsByCriterionId(UUID criterionId);
}
