export type CandidateStage =
  | "APPLIED"
  | "SCREENING"
  | "INTERVIEW"
  | "OFFER"
  | "HIRED"
  | "REJECTED";

export interface CandidateListItem {
  candidateId: string;
  fullName: string;
  email: string;
  jobTitle: string;
  stage: CandidateStage;
  rating: number | null;
  appliedAt: string;
}
