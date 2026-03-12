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
public interface OfferRepository extends JpaRepository<Offer, Long> {

    List<Offer> findByApplicationId(Long applicationId);

    boolean existsByApplicationIdAndStatusIn(Long applicationId, List<OfferStatus> statuses);

    Optional<Offer> findByIdAndCreatedBy(Long id, Long createdBy);

    Page<Offer> findByStatus(OfferStatus status, Pageable pageable);

    Page<Offer> findAll(Pageable pageable);
}
