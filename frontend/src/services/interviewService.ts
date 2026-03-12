import { apiFetch } from "@/lib/api";
import type {
    InterviewScoresResponse,
    InterviewResponse,
} from "@/types/models";

export interface SubmitScoresBody {
    recommendation?: string;
    strengths?: string;
    weaknesses?: string;
    generalComment?: string;
    scores: { criterionId: number; score: number; comment?: string }[];
}

export const interviewService = {
    getAllInterviews() {
        return apiFetch<InterviewResponse[]>("/interviews");
    },

    getUpcomingInterviews() {
        return apiFetch<InterviewResponse[]>("/interviews/upcoming");
    },

    getInterviewById(id: number) {
        return apiFetch<InterviewResponse>(`/interviews/${id}`);
    },

    getScores(interviewId: number) {
        return apiFetch<InterviewScoresResponse[]>(
            `/interviews/${interviewId}/scores`,
        );
    },

    getMyScores(interviewId: number) {
        return apiFetch<InterviewScoresResponse>(
            `/interviews/${interviewId}/scores/my`,
        );
    },

    submitScores(interviewId: number, body: SubmitScoresBody) {
        return apiFetch<InterviewScoresResponse>(
            `/interviews/${interviewId}/scores`,
            { method: "POST", body },
        );
    },

    reschedule(interviewId: number, body: { scheduledAt: string; location?: string; type?: string }) {
        return apiFetch<InterviewResponse>(
            `/interviews/${interviewId}/reschedule`,
            { method: "PUT", body },
        );
    },
};
