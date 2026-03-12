package fptu.sba301.ats.service;

import fptu.sba301.ats.dto.response.CandidateResponse;
import java.util.List;

public interface CandidateService {
    List<CandidateResponse> getAllCandidates();

    CandidateResponse getCandidateById(Long id);

    long countTotalCandidates();
}
