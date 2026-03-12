package fptu.sba301.ats.dto.response;

public record CriterionScoreSummary(
        Long criterionId,
        String criterionName,
        Double weight,
        Double averageScore,
        Double weightedScore // averageScore * weight
) {
}
