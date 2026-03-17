package fptu.sba301.ats.repository;

import fptu.sba301.ats.entity.Candidate;
import fptu.sba301.ats.repository.projection.CandidateListProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, UUID> {

    @Query(value = """
        SELECT
            BIN_TO_UUID(c.id) AS candidateId,
            c.full_name AS fullName,
            c.email AS email,
            j.title AS jobTitle,
            a.stage AS stage,
            ROUND(AVG(isc.score), 1) AS rating,
            a.applied_at AS appliedAt
        FROM candidates c
        JOIN applications a ON c.id = a.candidate_id
        JOIN jobs j ON a.job_id = j.id
        LEFT JOIN interviews i ON a.id = i.application_id
        LEFT JOIN interview_scores isc ON i.id = isc.interview_id
        WHERE a.status = 'ACTIVE'
        GROUP BY c.id, c.full_name, c.email, j.title, a.stage, a.applied_at
        ORDER BY a.applied_at DESC
        """, nativeQuery = true)
    List<CandidateListProjection> findAllCandidatesWithApplications();
}
