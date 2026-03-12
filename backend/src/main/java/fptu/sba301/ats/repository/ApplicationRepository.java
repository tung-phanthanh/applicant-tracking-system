package fptu.sba301.ats.repository;

import fptu.sba301.ats.entity.Application;
import fptu.sba301.ats.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByJobId(Long jobId);

    List<Application> findByJobIdAndStatus(Long jobId, ApplicationStatus status);

    @Query("""
            SELECT a FROM Application a
            WHERE a.jobId = :jobId AND a.status = 'ACTIVE'
            ORDER BY a.appliedAt DESC
            """)
    List<Application> findActiveByJobId(@Param("jobId") Long jobId);
}
