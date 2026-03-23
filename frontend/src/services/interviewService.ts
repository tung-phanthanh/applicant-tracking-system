import api from "@/lib/api";
import type { InterviewScorecard, SubmitScoreRequest } from "@/types/interview";

export const interviewService = {
  async submitScores(interviewId: string, request: SubmitScoreRequest): Promise<void> {
    await api.post(`/interviews/${interviewId}/scores`, request);
  },

  async getAllScorecards(interviewId: string): Promise<InterviewScorecard[]> {
    const { data } = await api.get<InterviewScorecard[]>(`/interviews/${interviewId}/scores`);
    return data;
  },

  async getMyScorecard(interviewId: string): Promise<InterviewScorecard> {
    const { data } = await api.get<InterviewScorecard>(`/interviews/${interviewId}/scores/me`);
    return data;
  },
};
