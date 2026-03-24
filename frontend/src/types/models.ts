// =============================================
// ENUMS — mirror backend Java enums
// =============================================

export type UserStatus = "ACTIVE" | "INACTIVE";
export type UserRole = "SYSTEM_ADMIN" | "HR" | "HR_MANAGER" | "INTERVIEWER";

export type JobStatus = "DRAFT" | "PENDING_APPROVAL" | "APPROVED" | "REJECTED" | "CLOSED";

export type ApplicationStage = "APPLIED" | "SCREENING" | "INTERVIEW" | "OFFER" | "HIRED" | "REJECTED";
export type ApplicationStatus = "ACTIVE" | "WITHDRAWN" | "REJECTED";

export type InterviewStatus = "SCHEDULED" | "COMPLETED" | "CANCELLED";
export type InterviewType = "ONLINE" | "OFFLINE";

export type OfferStatus = "DRAFT" | "PENDING_APPROVAL" | "APPROVED" | "REJECTED" | "SENT";
export type ApprovalStatus = "APPROVED" | "REJECTED";

export type HiringRecommendation =
  | "STRONG_HIRE"
  | "HIRE"
  | "NEUTRAL"
  | "NO_HIRE"
  | "STRONG_NO_HIRE";

export type OnboardingItemStatus = "PENDING" | "IN_PROGRESS" | "DONE";

// =============================================
// CORE ENTITIES
// =============================================

export interface Department {
  id: number;
  name: string;
  description?: string;
}

export interface User {
  id: number;
  email: string;
  fullName: string;
  status: UserStatus;
  department?: Department;
  roles: UserRole[];
}

export interface Job {
  id: number;
  title: string;
  description?: string;
  departmentId?: number;
  hiringManagerId?: number;
  status: string;
  headcount?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface Candidate {
  id: number;
  fullName: string;
  email: string;
  phone?: string;
  currentCompany?: string;
  createdAt?: string;
}

export interface Application {
  id: number;
  candidateId: number;
  jobId: number;
  stage: ApplicationStage;
  status: ApplicationStatus;
  appliedAt?: string;
  updatedAt?: string;

  // Extra properties mixed in by DTO
  candidateName?: string;
  jobTitle?: string;
}

export interface InterviewResponse {
  id: number;
  applicationId: number;
  scheduledAt: string;
  location?: string;
  status: InterviewStatus;

  // Extra properties mixed in by DTO
  candidateName?: string;
  jobTitle?: string;
  type?: string;
  participant?: string;
}

// =============================================
// SCORECARD MODULE
// =============================================

export interface ScorecardCriterion {
  id: number;
  templateId: number;
  name: string;
  weight: number;
  displayOrder?: number;
}

export interface ScorecardTemplate {
  id: number;
  name: string;
  department?: Department;
  createdAt: string;
  criteria: ScorecardCriterion[];
  archived?: boolean;
}

// =============================================
// INTERVIEW / SCORING MODULE
// =============================================

export interface Interview {
  id: number;
  applicationId: number;
  scheduledAt: string;
  location?: string;
  type: InterviewType;
  status: InterviewStatus;
  participants?: InterviewParticipant[];
}

export interface InterviewParticipant {
  userId: number;
  fullName: string;
  role: string; // INTERVIEWER | OBSERVER
}

export interface ScoreEntry {
  criterionId: number;
  criterionName: string;
  score: number; // 1-5
  comment?: string;
}

export interface InterviewScoresResponse {
  interviewId: number;
  interviewerId: number;
  interviewerName: string;
  recommendation?: HiringRecommendation;
  strengths?: string;
  weaknesses?: string;
  generalComment?: string;
  submitted: boolean;
  scores: ScoreEntry[];
}

// =============================================
// CANDIDATE EVALUATION SUMMARY
// =============================================

export interface CriteriaScore {
  criterionName: string;
  averageScore: number;
  maxScore: number;
}

export interface InterviewerSummary {
  interviewerName: string;
  totalScore: number;
  recommendation: HiringRecommendation;
  comment?: string;
  date: string;
}

export interface EvaluationSummaryResponse {
  applicationId: number;
  candidateName: string;
  jobTitle: string;
  aggregateScore: number;
  maxPossibleScore: number;
  overallRecommendation: HiringRecommendation;
  topStrengths: string[];
  mainConcerns: string[];
  criteriaScores: CriteriaScore[];
  interviewerSummaries: InterviewerSummary[];
}

// =============================================
// CANDIDATE RANKING
// =============================================

export interface RankedCandidate {
  rank: number;
  applicationId: number;
  candidateId: number;
  candidateName: string;
  candidateEmail?: string;
  jobTitle: string;
  stage: ApplicationStage;
  aggregateScore: number;
  recommendation?: HiringRecommendation;
  experienceYears?: number;
}

export interface CandidateRankingResponse {
  jobId: number;
  jobTitle: string;
  candidates: RankedCandidate[];
}

// =============================================
// OFFER MODULE
// =============================================

export interface OfferApprovalEntry {
  id: number;
  approvedBy: string;
  status: ApprovalStatus;
  comment?: string;
  approvedAt: string;
}

export interface OfferResponse {
  id: number;
  applicationId: number;
  candidateName: string;
  jobTitle: string;
  department?: string;
  positionTitle: string;
  salary: number;
  bonus?: number;
  startDate?: string;
  contractType?: string;
  workLocation?: string;
  benefits?: string;
  notes?: string;
  status: OfferStatus;
  createdBy: string;
  createdAt: string;
  approvals: OfferApprovalEntry[];
}

export interface CreateOfferRequest {
  applicationId: number;
  positionTitle: string;
  salary: number;
  bonus?: number;
  startDate?: string;
  contractType?: string;
  workLocation?: string;
  benefits?: string;
  notes?: string;
}

export interface UpdateOfferRequest {
  positionTitle?: string;
  salary?: number;
  bonus?: number;
  startDate?: string;
  contractType?: string;
  workLocation?: string;
  benefits?: string;
  notes?: string;
}

export interface ApprovalDecisionRequest {
  comment?: string;
}

// =============================================
// ONBOARDING MODULE
// =============================================

export interface OnboardingItem {
  id: number;
  taskName: string;
  assignedTo?: string;
  dueDate?: string;
  status: OnboardingItemStatus;
}

export interface OnboardingChecklistResponse {
  applicationId: number;
  candidateName: string;
  items: OnboardingItem[];
  completedCount: number;
  totalCount: number;
  progressPercent: number;
}

// =============================================
// PAGINATION
// =============================================

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number; // current page (0-indexed)
}

export interface PageParams {
  page?: number;
  size?: number;
  sort?: string;
}
