import api from "@/lib/api";
import type { CandidateListItem } from "@/types/candidate";

export const candidateService = {
  async getCandidates(): Promise<CandidateListItem[]> {
    const { data } = await api.get<CandidateListItem[]>("/candidates");
    return data;
  },
};
