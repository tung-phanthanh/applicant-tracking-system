package fptu.sba301.ats.repository;

import fptu.sba301.ats.entity.OfferApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfferApprovalRepository extends JpaRepository<OfferApproval, java.util.UUID> {

    List<OfferApproval> findByOfferIdOrderByCreatedAtDesc(java.util.UUID offerId);

    boolean existsByOfferIdAndApprovedById(java.util.UUID offerId, java.util.UUID approvedById);
}
