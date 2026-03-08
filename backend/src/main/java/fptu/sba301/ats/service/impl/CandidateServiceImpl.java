package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.response.CandidateResponse;
import fptu.sba301.ats.entity.Candidate;
import fptu.sba301.ats.repository.CandidateRepository;
import fptu.sba301.ats.service.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidateServiceImpl implements CandidateService {

    private final CandidateRepository candidateRepository;

    @Override
    public List<CandidateResponse> getAllCandidates() {
        return candidateRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CandidateResponse getCandidateById(Long id) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));
        return mapToResponse(candidate);
    }

    @Override
    public long countTotalCandidates() {
        return candidateRepository.count();
    }

    private CandidateResponse mapToResponse(Candidate candidate) {
        return CandidateResponse.builder()
                .id(candidate.getId())
                .fullName(candidate.getFullName())
                .email(candidate.getEmail())
                .phone(candidate.getPhone())
                .currentCompany(candidate.getCurrentCompany())
                .createdAt(candidate.getCreatedAt())
                .build();
    }
}
