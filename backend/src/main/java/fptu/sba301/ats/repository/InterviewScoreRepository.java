package fptu.sba301.ats.repository;

import fptu.sba301.ats.entity.InterviewScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface InterviewScoreRepository
        extends JpaRepository<InterviewScore, UUID> {

    @Modifying
    @Query("""
        DELETE FROM InterviewScore s
        WHERE s.interview.id = :interviewId
        AND s.interviewer.id = :userId
    """)
    void deleteOld(UUID interviewId, UUID userId);
}
