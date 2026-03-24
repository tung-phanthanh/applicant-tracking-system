package fptu.sba301.ats.repository;

import fptu.sba301.ats.entity.InterviewParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InterviewParticipantRepository extends JpaRepository<InterviewParticipant, UUID> {

    Optional<InterviewParticipant> findByInterviewIdAndUserId(UUID interviewId, UUID userId);
}
