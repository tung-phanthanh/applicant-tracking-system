import api from "@/lib/api";
import type { CandidateDetailItem, CandidateListItem, CandidateStage } from "@/types/candidate";

export const candidateService = {
  async getCandidates(): Promise<CandidateListItem[]> {
    const { data } = await api.get<CandidateListItem[]>("/candidates");
    return data;
  },

  async getCandidateDetail(candidateId: string): Promise<CandidateDetailItem> {
    const { data } = await api.get<CandidateDetailItem>(`/candidates/${candidateId}`);
    return data;
  },

  async updateCandidateStage(candidateId: string, stage: CandidateStage): Promise<CandidateDetailItem> {
    const { data } = await api.patch<CandidateDetailItem>(`/candidates/${candidateId}/stage`, { stage });
    return data;
  },
};
