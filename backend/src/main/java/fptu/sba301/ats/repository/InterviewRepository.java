package fptu.sba301.ats.repository;

import fptu.sba301.ats.entity.Interview;
import fptu.sba301.ats.enums.InterviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, java.util.UUID> {

    List<Interview> findByApplicationId(java.util.UUID applicationId);

    List<Interview> findByApplicationIdAndStatus(java.util.UUID applicationId, InterviewStatus status);

    List<Interview> findByApplicationIdIn(List<java.util.UUID> applicationIds);
}
