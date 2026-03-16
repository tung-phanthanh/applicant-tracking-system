import api from "@/lib/api";

export interface StageEvaluation {
  interviewId: string;
  stageName: string;
  status: string;
  score: number | null;
  interviewerName: string | null;
  date: string | null;
  feedback: string | null;
}

export interface CandidateEvaluation {
  applicationId: string;
  candidateId: string;
  candidateName: string;
  candidateEmail: string;
  positionTitle: string;
  jobId: string;
  overallScore: number;
  recommendation: string;
  interviewsCompleted: number;
  interviewsTotal: number;
  stages: StageEvaluation[];
}

export interface CandidateRank {
  rank: number;
  applicationId: string;
  candidateId: string;
  candidateName: string;
  candidateEmail: string;
  overallScore: number;
  techScore: number;
  cultureScore: number;
  status: string;
  stage: string;
}

export interface CandidateRanking {
  jobId: string;
  jobTitle: string;
  candidates: CandidateRank[];
}

export const candidateService = {
  getEvaluation: async (applicationId: string): Promise<CandidateEvaluation> => {
    const response = await api.get(`/applications/${applicationId}/evaluation`);
    return response.data;
  },

  getRanking: async (jobId: string): Promise<CandidateRanking> => {
    const response = await api.get(`/jobs/${jobId}/ranking`);
    return response.data;
  },
};
