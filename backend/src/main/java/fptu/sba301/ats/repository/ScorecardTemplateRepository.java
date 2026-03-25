package fptu.sba301.ats.repository;

import fptu.sba301.ats.entity.ScorecardTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ScorecardTemplateRepository extends JpaRepository<ScorecardTemplate, UUID> {
}
