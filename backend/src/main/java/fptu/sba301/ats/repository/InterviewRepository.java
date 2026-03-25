package fptu.sba301.ats.repository;

import fptu.sba301.ats.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, UUID> {
    List<Interview> findByApplicationId(UUID applicationId);
    @Query("""
        select distinct i
        from Interview i
        left join fetch i.participants p
        where i.id = :interviewId
    """)
    Optional<Interview> findByIdWithParticipants(@Param("interviewId") UUID interviewId);
}
