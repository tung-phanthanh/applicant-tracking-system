// ── Pagination ───────────────────────────────────────────────────────────

export interface PageResponse<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    number: number;        // current page (0-indexed)
    size: number;
    first: boolean;
    last: boolean;
}

// ── Scorecard Types ──────────────────────────────────────────────────────

export interface ScorecardCriterion {
    id: string;
    name: string;
    weight: number;
}

export interface ScorecardTemplate {
    id: string;
    name: string;
    departmentId: string | null;
    departmentName: string | null;
    criteria: ScorecardCriterion[];
}

export interface CreateScorecardTemplatePayload {
    name: string;
    departmentId?: string;
    criteria: { name: string; weight: number }[];
}

// ── Interview Score Types ────────────────────────────────────────────────

export interface InterviewScore {
    id: string;
    interviewId: string;
    interviewerId: string;
    interviewerName: string;
    criterionId: string;
    criterionName: string;
    score: number;
    comment: string;
}

export interface SubmitScorePayload {
    interviewId: string;
    scores: { criterionId: string; score: number; comment: string }[];
}

// ── Evaluation Types ─────────────────────────────────────────────────────

export interface StageEvaluation {
    stageName: string;
    status: string;
    score: number | null;
    interviewerName: string;
    date: string;
    feedback: string;
}

export interface CandidateEvaluation {
    candidateName: string;
    positionTitle: string;
    overallScore: number;
    recommendation: string;
    interviewsCompleted: number;
    interviewsTotal: number;
    stages: StageEvaluation[];
}

// ── Ranking Types ────────────────────────────────────────────────────────

export interface CandidateRanking {
    rank: number;
    candidateId: string;
    candidateName: string;
    candidateEmail: string;
    overallScore: number;
    techScore: number;
    cultureScore: number;
    status: string;
}
