package fptu.sba301.ats.repository;

import fptu.sba301.ats.entity.JobApproval;
import fptu.sba301.ats.enums.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobApprovalRepository extends JpaRepository<JobApproval, UUID> {

    Optional<JobApproval> findTopByJob_IdAndStatusOrderByCreatedAtDesc(UUID jobId, ApprovalStatus status);
}
