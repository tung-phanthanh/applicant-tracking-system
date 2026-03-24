package fptu.sba301.ats.dto.response;

public record InterviewScoreItemResponse(
        java.util.UUID id,
        java.util.UUID criterionId,
        String criterionName,
        Integer score,
        String comment) {
}
