package fptu.sba301.ats.repository;

import fptu.sba301.ats.entity.OfferApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfferApprovalRepository extends JpaRepository<OfferApproval, java.util.UUID> {

    List<OfferApproval> findByOfferIdOrderByApprovedAtDesc(Long offerId);

    boolean existsByOfferIdAndApprovedBy(Long offerId, Long approvedBy);
}
