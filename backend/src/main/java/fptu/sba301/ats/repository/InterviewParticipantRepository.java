package fptu.sba301.ats.repository;

import fptu.sba301.ats.entity.InterviewParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterviewParticipantRepository extends JpaRepository<InterviewParticipant, Long> {

    boolean existsByInterviewIdAndUserId(Long interviewId, Long userId);
}
