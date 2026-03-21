package fptu.sba301.ats.dto.response;

import java.util.List;

public record EvaluationSummaryResponse(
        java.util.UUID applicationId,
        String candidateName,
        String jobTitle,
        Double aggregateWeightedScore, // null when no interviews completed
        List<CriterionScoreSummary> criteria,
        List<InterviewerScoreBreakdown> byInterviewer) {
}
