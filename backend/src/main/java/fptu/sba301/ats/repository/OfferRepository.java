package fptu.sba301.ats.repository;

import fptu.sba301.ats.entity.Offer;
import fptu.sba301.ats.enums.OfferStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OfferRepository extends JpaRepository<Offer, UUID> {

    Optional<Offer> findByApplicationId(UUID applicationId);

    Page<Offer> findByStatus(OfferStatus status, Pageable pageable);
}
