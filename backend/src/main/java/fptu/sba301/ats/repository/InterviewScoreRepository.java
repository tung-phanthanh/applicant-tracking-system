package fptu.sba301.ats.repository;

import fptu.sba301.ats.entity.InterviewScore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InterviewScoreRepository extends JpaRepository<InterviewScore, UUID> {

    Page<InterviewScore> findByInterviewId(UUID interviewId, Pageable pageable);

    List<InterviewScore> findByInterviewId(UUID interviewId);

    long countByInterviewIdAndInterviewerId(UUID interviewId, UUID interviewerId);
}
