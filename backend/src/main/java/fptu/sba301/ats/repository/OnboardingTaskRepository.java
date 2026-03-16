package fptu.sba301.ats.repository;

import fptu.sba301.ats.entity.OnboardingTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OnboardingTaskRepository extends JpaRepository<OnboardingTask, UUID> {

    List<OnboardingTask> findByApplicationIdOrderBySortOrder(UUID applicationId);

    Page<OnboardingTask> findByApplicationId(UUID applicationId, Pageable pageable);

    long countByApplicationId(UUID applicationId);
}
