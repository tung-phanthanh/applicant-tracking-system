package fptu.sba301.ats.repository;

import fptu.sba301.ats.entity.Job;
import fptu.sba301.ats.enums.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<Job, UUID> {
    long countByStatus(JobStatus status);
    long countByDepartment_IdAndStatusNot(UUID departmentId, JobStatus status);
}
