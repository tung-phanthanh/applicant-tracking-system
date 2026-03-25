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

    /** Add a single candidate. Documents (PDFs, etc.) are uploaded to Cloudinary. */
    CandidateDetailResponse createCandidate(CreateCandidateRequest request, List<MultipartFile> documents);

    /**
     * Bulk-import candidates from a CSV file.
     * Each row may specify a cvFileName; if matching PDFs are included in cvFiles they will be uploaded.
     */
    BulkImportResponse importCandidatesFromCsv(MultipartFile csvFile, List<MultipartFile> cvFiles);
}
