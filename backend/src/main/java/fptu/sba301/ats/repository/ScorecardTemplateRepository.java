package fptu.sba301.ats.repository;

import fptu.sba301.ats.entity.ScorecardTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScorecardTemplateRepository extends JpaRepository<ScorecardTemplate, java.util.UUID> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, java.util.UUID id);
}
