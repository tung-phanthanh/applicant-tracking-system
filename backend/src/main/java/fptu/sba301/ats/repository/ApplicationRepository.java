package fptu.sba301.ats.repository;

import fptu.sba301.ats.entity.Application;
import fptu.sba301.ats.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    List<Application> findByJobId(UUID jobId);
    List<Application> findByJobIdAndStatus(UUID jobId, ApplicationStatus status);
    List<Application> findByCandidateId(UUID candidateId);
}
