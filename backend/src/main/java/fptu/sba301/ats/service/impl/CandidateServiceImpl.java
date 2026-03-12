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
    private final fptu.sba301.ats.repository.CandidateDocumentRepository candidateDocumentRepository;
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

    public CandidateProfileResponse getCandidateById(UUID id) {
        // row: [0]=id, [1]=name, [2]=email, [3]=phone, [4]=currentCompany,
        //       [5]=title, [6]=stage, [7]=status, [8]=avg_score, [9]=applied_at
        //       [10]=source, [11]=location, [12]=experience_years, [13]=summary
        List<Object[]> results = candidateRepository.findCandidateProfileById(id.toString());
        if (results == null || results.isEmpty()) {
            throw new RuntimeException("Candidate not found with id: " + id);
        }
        Object[] row = results.get(0);

        List<fptu.sba301.ats.dto.response.CandidateDocumentDto> documents = candidateDocumentRepository.findByCandidateId(id)
            .stream()
            .map(doc -> fptu.sba301.ats.dto.response.CandidateDocumentDto.builder()
                .id(doc.getId())
                .fileName(doc.getFileName())
                .fileUrl(doc.getFileUrl())
                .fileType(doc.getFileType())
                .fileSizeBytes(doc.getFileSizeBytes())
                .uploadedAt(doc.getUploadedAt())
                .build())
            .collect(Collectors.toList());

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
            row[9] != null ? ((java.sql.Timestamp) row[9]).toLocalDateTime() : null,
            (String) row[10],
            (String) row[11],
            row[12] != null ? ((Number) row[12]).intValue() : null,
            (String) row[13],
            documents
        );
    }
}
