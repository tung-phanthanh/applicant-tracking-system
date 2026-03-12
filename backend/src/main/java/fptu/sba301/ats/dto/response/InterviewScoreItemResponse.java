package fptu.sba301.ats.dto.response;

public record InterviewScoreItemResponse(
        Long id,
        Long criterionId,
        String criterionName,
        Integer score,
        String comment) {
}
