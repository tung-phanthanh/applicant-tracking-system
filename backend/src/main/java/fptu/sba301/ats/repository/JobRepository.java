package fptu.sba301.ats.repository;

import fptu.sba301.ats.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<Job, UUID> {
    // Additional query methods can be added later (search, filtering, etc.)
}
