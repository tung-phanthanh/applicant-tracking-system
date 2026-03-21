package fptu.sba301.ats.service;

import fptu.sba301.ats.dto.response.CandidateDetailResponse;
import fptu.sba301.ats.dto.response.CandidateListResponse;
import fptu.sba301.ats.enums.ApplicationStage;

import java.util.List;
import java.util.UUID;

public interface CandidateService {
    List<CandidateListResponse> getCandidateList();
    CandidateDetailResponse getCandidateDetail(UUID candidateId);
    CandidateDetailResponse updateCandidateStage(UUID candidateId, ApplicationStage targetStage);
}
