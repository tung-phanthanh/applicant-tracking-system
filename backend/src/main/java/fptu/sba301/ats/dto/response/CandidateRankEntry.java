package fptu.sba301.ats.dto.response;

import java.util.UUID;
import java.time.Instant;

public record CandidateRankEntry(
        int rank,
        UUID applicationId,
        UUID candidateId,
        String candidateFullName,
        String currentStage, // ApplicationStage name
        Double aggregateScore, // null if no interviews completed
        int interviewCount,
        Instant lastInterviewAt // null if no interviews
) {
}
