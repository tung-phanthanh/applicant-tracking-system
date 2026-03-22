package fptu.sba301.ats.repository;

import fptu.sba301.ats.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<Job, java.util.UUID> {
    long countByStatus(fptu.sba301.ats.enums.JobStatus status);
}
