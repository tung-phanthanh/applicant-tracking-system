package fptu.sba301.ats.repository;

import fptu.sba301.ats.entity.OnboardingItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OnboardingItemRepository extends JpaRepository<OnboardingItem, java.util.UUID> {

    java.util.List<OnboardingItem> findByChecklistIdOrderByDisplayOrderAscIdAsc(java.util.UUID checklistId);
}
