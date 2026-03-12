package fptu.sba301.ats.dto.response;

import java.util.Map;

public record InterviewerScoreBreakdown(
        Long interviewerId,
        String interviewerName,
        Map<String, Integer> scoresByCriterion, // criterionName -> score
        String overallComment) {
}
