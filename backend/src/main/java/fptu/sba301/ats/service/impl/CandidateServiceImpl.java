package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.response.CandidateListResponse;
import fptu.sba301.ats.dto.response.CandidateProfileResponse;
import fptu.sba301.ats.mapper.CandidateMapper;
import fptu.sba301.ats.repository.CandidateRepository;
import fptu.sba301.ats.service.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidateServiceImpl implements CandidateService {

    private final CandidateRepository candidateRepository;
    private final CandidateMapper candidateMapper;

    @Override
    public List<CandidateListResponse> getAllCandidates() {
        // row: [0]=id, [1]=name, [2]=email, [3]=title, [4]=stage, [5]=avg_score, [6]=applied_at
        return candidateRepository.findAllCandidatesWithApplications()
            .stream()
            .map(row -> candidateMapper.toCandidateListResponse(
                (String) row[0],
                (String) row[1],
                (String) row[2],
                (String) row[3],
                (String) row[4],
                row[5] != null ? ((Number) row[5]).doubleValue() : null,
                row[6] != null ? ((java.sql.Timestamp) row[6]).toLocalDateTime() : null
            ))
            .collect(Collectors.toList());
    }

    @Override
    public CandidateProfileResponse getCandidateById(UUID id) {
        // row: [0]=id, [1]=name, [2]=email, [3]=phone, [4]=currentCompany,
        //       [5]=title, [6]=stage, [7]=status, [8]=avg_score, [9]=applied_at
        Object[] row = candidateRepository.findCandidateProfileById(id.toString())
            .orElseThrow(() -> new RuntimeException("Candidate not found with id: " + id));

        return candidateMapper.toCandidateProfileResponse(
            UUID.fromString((String) row[0]),
            (String) row[1],
            (String) row[2],
            (String) row[3],
            (String) row[4],
            (String) row[5],
            (String) row[6],
            (String) row[7],
            row[8] != null ? ((Number) row[8]).doubleValue() : null,
            row[9] != null ? ((java.sql.Timestamp) row[9]).toLocalDateTime() : null
        );
    }
}
