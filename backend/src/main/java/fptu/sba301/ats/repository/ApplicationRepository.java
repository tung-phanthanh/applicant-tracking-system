package fptu.sba301.ats.repository;

import fptu.sba301.ats.entity.Application;
import fptu.sba301.ats.enums.ApplicationStatus;
import fptu.sba301.ats.enums.ApplicationStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    Optional<Application> findTopByCandidate_IdAndStatusOrderByAppliedAtDesc(UUID candidateId, ApplicationStatus status);
    long countByStage(ApplicationStage stage);
}
