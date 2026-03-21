package fptu.sba301.ats.repository;

import fptu.sba301.ats.entity.ScorecardCriterion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ScorecardCriterionRepository extends JpaRepository<ScorecardCriterion, java.util.UUID> {

    List<ScorecardCriterion> findByTemplateId(Long templateId);

    @Query("SELECT COALESCE(SUM(c.weight), 0) FROM ScorecardCriterion c WHERE c.template.id = :templateId")
    BigDecimal sumWeightByTemplateId(@Param("templateId") Long templateId);

    @Query("SELECT COALESCE(SUM(c.weight), 0) FROM ScorecardCriterion c WHERE c.template.id = :templateId AND c.id <> :excludeId")
    BigDecimal sumWeightByTemplateIdExcluding(@Param("templateId") Long templateId,
            @Param("excludeId") Long excludeId);
}
