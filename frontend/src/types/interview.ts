export interface ScoreDetail {
  criterionId: string;
  criterionName: string;
  weight: number;
  score: number;
  comment: string | null;
}

export interface InterviewScorecard {
  interviewId: string;
  participantUserId: string;
  participantName: string;
  overallScore: number | null;
  feedback: string | null;
  scores: ScoreDetail[];
}

export interface ScoreEntry {
  criterionId: string;
  score: number;
  comment: string;
}

export interface SubmitScoreRequest {
  scores: ScoreEntry[];
}
