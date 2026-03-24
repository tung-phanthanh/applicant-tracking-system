package fptu.sba301.ats.repository;

import fptu.sba301.ats.entity.ScorecardCriterion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ScorecardCriterionRepository extends JpaRepository<ScorecardCriterion, UUID> {
    Optional<ScorecardCriterion> findByName(String name);
}