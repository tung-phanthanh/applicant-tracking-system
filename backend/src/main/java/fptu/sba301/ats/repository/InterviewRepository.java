package fptu.sba301.ats.repository;

import fptu.sba301.ats.entity.Interview;
import fptu.sba301.ats.enums.InterviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {

    List<Interview> findByApplicationId(Long applicationId);

    List<Interview> findByApplicationIdAndStatus(Long applicationId, InterviewStatus status);

    List<Interview> findByApplicationIdIn(List<Long> applicationIds);
}
