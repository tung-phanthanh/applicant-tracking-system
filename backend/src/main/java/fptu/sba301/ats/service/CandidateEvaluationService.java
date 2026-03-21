package fptu.sba301.ats.service;

import fptu.sba301.ats.dto.response.EvaluationSummaryResponse;

public interface CandidateEvaluationService {

    EvaluationSummaryResponse getEvaluation(java.util.UUID applicationId);
}