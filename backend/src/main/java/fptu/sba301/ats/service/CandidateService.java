package fptu.sba301.ats.service;

import fptu.sba301.ats.dto.request.CreateCandidateRequest;
import fptu.sba301.ats.dto.response.BulkImportResponse;
import fptu.sba301.ats.dto.response.CandidateDetailResponse;
import fptu.sba301.ats.dto.response.CandidateListResponse;
import fptu.sba301.ats.enums.ApplicationStage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface CandidateService {
    List<CandidateListResponse> getCandidateList();
    CandidateDetailResponse getCandidateDetail(UUID candidateId);
    CandidateDetailResponse updateCandidateStage(UUID candidateId, ApplicationStage targetStage);

    CandidateDetailResponse createCandidate(CreateCandidateRequest request, List<MultipartFile> documents);

    BulkImportResponse importCandidatesFromCsv(MultipartFile csvFile, List<MultipartFile> cvFiles);

}
