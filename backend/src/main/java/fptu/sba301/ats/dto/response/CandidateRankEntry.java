package fptu.sba301.ats.dto.response;

import java.time.Instant;

public record CandidateRankEntry(
        int rank,
        Long applicationId,
        Long candidateId,
        String candidateFullName,
        String currentStage, // ApplicationStage name
        Double aggregateScore, // null if no interviews completed
        int interviewCount,
        Instant lastInterviewAt // null if no interviews
) {
}
