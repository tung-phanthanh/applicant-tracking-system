package fptu.sba301.ats.repository;

import fptu.sba301.ats.entity.Job;
import fptu.sba301.ats.enums.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<Job, UUID> {

    @Query("SELECT DISTINCT j FROM Job j LEFT JOIN FETCH j.department WHERE j.status = :status ORDER BY j.createdAt DESC")
    List<Job> findAllByStatusWithDepartment(@Param("status") JobStatus status);

    @Query("SELECT DISTINCT j FROM Job j LEFT JOIN FETCH j.department WHERE j.status = :status AND j.department.id = :departmentId ORDER BY j.createdAt DESC")
    List<Job> findAllByStatusAndDepartmentIdWithDepartment(
            @Param("status") JobStatus status,
            @Param("departmentId") UUID departmentId);

    @Query("SELECT DISTINCT j FROM Job j LEFT JOIN FETCH j.department WHERE j.id = :id")
    Optional<Job> findByIdWithDepartment(@Param("id") UUID id);

    long countByStatus(JobStatus status);

    long countByDepartment_IdAndStatusNot(UUID departmentId, JobStatus status);
}
