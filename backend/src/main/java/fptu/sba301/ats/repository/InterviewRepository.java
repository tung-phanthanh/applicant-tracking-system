package fptu.sba301.ats.repository;

import fptu.sba301.ats.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InterviewRepository extends JpaRepository<Interview, UUID> {
}
