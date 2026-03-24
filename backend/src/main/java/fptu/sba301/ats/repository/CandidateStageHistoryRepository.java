package fptu.sba301.ats.repository;

import fptu.sba301.ats.entity.CandidateStageHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CandidateStageHistoryRepository extends JpaRepository<CandidateStageHistory, UUID> {
}
