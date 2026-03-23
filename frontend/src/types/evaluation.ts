export interface InterviewEvaluation {
  interviewId: string;
  scheduledAt: string;
  interviewerCount: number;
  averageScore: number;
}

export interface CandidateEvaluation {
  applicationId: string;
  candidateId: string;
  candidateName: string;
  jobTitle: string;
  overallScore: number;
  interviewCount: number;
  interviews: InterviewEvaluation[];
}

export interface CandidateRanking {
  rank: number;
  applicationId: string;
  candidateId: string;
  candidateName: string;
  overallScore: number;
  experienceYears: number | null;
  appliedAt: string;
  stage: string;
}
