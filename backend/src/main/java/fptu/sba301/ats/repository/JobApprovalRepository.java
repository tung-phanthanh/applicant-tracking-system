package fptu.sba301.ats.repository;

import fptu.sba301.ats.entity.JobApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobApprovalRepository extends JpaRepository<JobApproval, java.util.UUID> {

    List<JobApproval> findByJobIdOrderByCreatedAtDesc(java.util.UUID jobId);
}
