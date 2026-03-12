package fptu.sba301.ats.repository;

import fptu.sba301.ats.entity.OnboardingChecklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OnboardingChecklistRepository extends JpaRepository<OnboardingChecklist, Long> {

    Optional<OnboardingChecklist> findByApplicationId(Long applicationId);

    boolean existsByApplicationId(Long applicationId);
}
