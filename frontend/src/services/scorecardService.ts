import api from "@/lib/api";
import type {
    ScorecardTemplate,
    CreateScorecardTemplatePayload,
    InterviewScore,
    SubmitScorePayload,
    CandidateEvaluation,
    CandidateRanking,
    PageResponse,
} from "@/types/scorecard";

export const scorecardService = {
    // ── Templates ────────────────────────────────────────────────────────
    async getTemplates(page = 0, size = 20): Promise<PageResponse<ScorecardTemplate>> {
        const { data } = await api.get<PageResponse<ScorecardTemplate>>(
            `/scorecard-templates?page=${page}&size=${size}`
        );
        return data;
    },

    async getTemplatesByDepartment(departmentId: string): Promise<ScorecardTemplate[]> {
        const { data } = await api.get<ScorecardTemplate[]>(
            `/scorecard-templates?departmentId=${departmentId}`
        );
        return data;
    },

    async getTemplateById(id: string): Promise<ScorecardTemplate> {
        const { data } = await api.get<ScorecardTemplate>(`/scorecard-templates/${id}`);
        return data;
    },

    async createTemplate(payload: CreateScorecardTemplatePayload): Promise<ScorecardTemplate> {
        const { data } = await api.post<ScorecardTemplate>("/scorecard-templates", payload);
        return data;
    },

    async updateTemplate(id: string, payload: CreateScorecardTemplatePayload): Promise<ScorecardTemplate> {
        const { data } = await api.put<ScorecardTemplate>(`/scorecard-templates/${id}`, payload);
        return data;
    },

    async deleteTemplate(id: string): Promise<void> {
        await api.delete(`/scorecard-templates/${id}`);
    },

    // ── Interview Scores ─────────────────────────────────────────────────
    async submitScores(payload: SubmitScorePayload): Promise<InterviewScore[]> {
        const { data } = await api.post<InterviewScore[]>("/interview-scores", payload);
        return data;
    },

    async getScoresByInterview(interviewId: string, page = 0, size = 50): Promise<PageResponse<InterviewScore>> {
        const { data } = await api.get<PageResponse<InterviewScore>>(
            `/interview-scores/interview/${interviewId}?page=${page}&size=${size}`
        );
        return data;
    },

    // ── Evaluation ───────────────────────────────────────────────────────
    async getEvaluation(applicationId: string): Promise<CandidateEvaluation> {
        const { data } = await api.get<CandidateEvaluation>(`/evaluations/application/${applicationId}`);
        return data;
    },

    // ── Ranking ──────────────────────────────────────────────────────────
    async getRanking(jobId: string): Promise<CandidateRanking[]> {
        const { data } = await api.get<CandidateRanking[]>(`/rankings/job/${jobId}`);
        return data;
    },
};
