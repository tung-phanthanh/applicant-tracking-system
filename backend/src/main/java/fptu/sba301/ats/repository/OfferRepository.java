package fptu.sba301.ats.repository;

import fptu.sba301.ats.entity.Offer;
import fptu.sba301.ats.enums.OfferStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfferRepository extends JpaRepository<Offer, java.util.UUID> {

    List<Offer> findByApplicationId(java.util.UUID applicationId);

    boolean existsByApplicationIdAndStatusIn(java.util.UUID applicationId, List<OfferStatus> statuses);

    Optional<Offer> findByIdAndCreatedBy(java.util.UUID id, java.util.UUID createdBy);

    Page<Offer> findByStatus(OfferStatus status, Pageable pageable);

    Page<Offer> findAll(Pageable pageable);
}
