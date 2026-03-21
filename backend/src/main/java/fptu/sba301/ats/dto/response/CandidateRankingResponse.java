package fptu.sba301.ats.dto.response;

import java.util.List;

public record CandidateRankingResponse(
        java.util.UUID jobId,
        String jobTitle,
        int totalCandidates,
        List<CandidateRankEntry> ranking) {
}
