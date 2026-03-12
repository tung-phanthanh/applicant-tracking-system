package fptu.sba301.ats.repository;

import fptu.sba301.ats.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, UUID> {

    @Query(value = """
        SELECT
            c.id::text,
            c.full_name,
            c.email,
            j.title,
            a.stage,
            ROUND(AVG(isc.score)::numeric, 1),
            a.applied_at
        FROM candidates c
        JOIN applications a ON c.id = a.candidate_id
        JOIN jobs j ON a.job_id = j.id
        LEFT JOIN interviews i ON a.id = i.application_id
        LEFT JOIN interview_scores isc ON i.id = isc.interview_id
        WHERE a.status = 'ACTIVE'
        GROUP BY c.id, c.full_name, c.email, j.title, a.stage, a.applied_at
        ORDER BY a.applied_at DESC
        """, nativeQuery = true)
    List<Object[]> findAllCandidatesWithApplications();

    @Query(value = """
        SELECT
            c.id::text,
            c.full_name,
            c.email,
            c.phone,
            c.current_company,
            j.title,
            a.stage,
            a.status,
            ROUND(AVG(isc.score)::numeric, 1),
            a.applied_at,
            c.source,
            c.location,
            c.experience_years,
            c.summary
        FROM candidates c
        JOIN applications a ON c.id = a.candidate_id
        JOIN jobs j ON a.job_id = j.id
        LEFT JOIN interviews i ON a.id = i.application_id
        LEFT JOIN interview_scores isc ON i.id = isc.interview_id
        WHERE c.id = CAST(:candidateId AS uuid)
          AND a.status = 'ACTIVE'
        GROUP BY c.id, c.full_name, c.email, c.phone, c.current_company,
                 j.title, a.stage, a.status, a.applied_at,
                 c.source, c.location, c.experience_years, c.summary
        ORDER BY a.applied_at DESC
        LIMIT 1
        """, nativeQuery = true)
    List<Object[]> findCandidateProfileById(@Param("candidateId") String candidateId);
}