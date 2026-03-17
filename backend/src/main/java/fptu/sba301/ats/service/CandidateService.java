package fptu.sba301.ats.service;

import fptu.sba301.ats.dto.response.CandidateListResponse;

import java.util.List;

public interface CandidateService {
    List<CandidateListResponse> getCandidateList();
}
