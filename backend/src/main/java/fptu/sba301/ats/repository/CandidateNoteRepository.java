package fptu.sba301.ats.repository;

import fptu.sba301.ats.entity.CandidateNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateNoteRepository extends JpaRepository<CandidateNote, java.util.UUID> {

    List<CandidateNote> findByApplicationIdOrderByCreatedAtDesc(Long applicationId);
}
