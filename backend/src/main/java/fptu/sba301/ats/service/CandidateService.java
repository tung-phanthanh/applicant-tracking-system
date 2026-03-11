package fptu.sba301.ats.service;

import fptu.sba301.ats.dto.response.CandidateListResponse;
import fptu.sba301.ats.dto.response.CandidateProfileResponse;

import java.util.List;
import java.util.UUID;

public interface CandidateService {
    List<CandidateListResponse> getAllCandidates();
    CandidateProfileResponse getCandidateById(UUID id);
}
