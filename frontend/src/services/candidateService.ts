import { apiFetch } from "@/lib/api";
import type {
    EvaluationSummaryResponse,
    CandidateRankingResponse,
    Candidate
} from "@/types/models";

export const candidateService = {
    getAllCandidates() {
        return apiFetch<Candidate[]>("/candidates");
    },

    getCandidateById(id: number) {
        return apiFetch<Candidate>(`/candidates/${id}`);
    },

    getTotalCount() {
        return apiFetch<number>("/candidates/count");
    },

    getEvaluation(applicationId: number) {
        return apiFetch<EvaluationSummaryResponse>(
            `/applications/${applicationId}/evaluation`,
        );
    },

    getRanking(jobId: number) {
        return apiFetch<CandidateRankingResponse>(`/jobs/${jobId}/ranking`);
    },
};
