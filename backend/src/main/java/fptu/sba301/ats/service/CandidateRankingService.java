package fptu.sba301.ats.service;

import fptu.sba301.ats.dto.response.CandidateRankingResponse;

public interface CandidateRankingService {

    CandidateRankingResponse getRanking(java.util.UUID jobId);
}
