package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.response.CandidateListResponse;
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

    @Override
    public List<CandidateListResponse> getCandidateList() {
        return candidateRepository.findAllCandidatesWithApplications()
                .stream()
                .map(row -> CandidateListResponse.builder()
                    .candidateId(UUID.fromString(row.getCandidateId()))
                        .fullName(row.getFullName())
                        .email(row.getEmail())
                        .jobTitle(row.getJobTitle())
                        .stage(row.getStage())
                        .rating(row.getRating())
                        .appliedAt(row.getAppliedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
