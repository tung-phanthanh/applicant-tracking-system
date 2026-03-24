package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.request.CreateCandidateRequest;
import fptu.sba301.ats.dto.response.BulkImportResponse;
import fptu.sba301.ats.dto.response.CandidateDetailResponse;
import fptu.sba301.ats.dto.response.CandidateListResponse;
import fptu.sba301.ats.enums.ApplicationStage;
import fptu.sba301.ats.service.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CandidateServiceImpl implements CandidateService {

    @Override
    public List<CandidateListResponse> getCandidateList() {
        return List.of();
    }

    @Override
    public CandidateDetailResponse getCandidateDetail(UUID candidateId) {
        return null;
    }

    @Override
    public CandidateDetailResponse updateCandidateStage(UUID candidateId, ApplicationStage targetStage) {
        return null;
    }

    @Override
    public CandidateDetailResponse createCandidate(CreateCandidateRequest request, List<MultipartFile> documents) {
        return null;
    }

    @Override
    public BulkImportResponse importCandidatesFromCsv(MultipartFile csvFile, List<MultipartFile> cvFiles) {
        return null;
    }
}
