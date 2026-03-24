package fptu.sba301.ats.dto.response;

public record CriterionScoreSummary(
        java.util.UUID criterionId,
        String criterionName,
        Double weight,
        Double averageScore,
        Double weightedScore // averageScore * weight
) {
}
