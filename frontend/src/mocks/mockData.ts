/**
 * ================================================================
 * MOCK DATA — ATS Frontend
 * Dữ liệu mẫu cho tất cả 8 chức năng:
 *   1. Scorecard Templates
 *   2. Interview Scorecard
 *   3. Candidate Evaluation Summary
 *   4. Candidate Ranking
 *   5. Offer Draft
 *   6. Offer Approval
 *   7. Offer PDF Preview
 *   8. Onboarding Checklist
 * ================================================================
 */

import type {
  ScorecardTemplate,
  InterviewScoresResponse,
  EvaluationSummaryResponse,
  CandidateRankingResponse,
  OfferResponse,
  OnboardingChecklistResponse,
  InterviewResponse,
  Candidate,
  Application,
  Job,
  Page,
} from "@/types/models";

// ================================================
// 1. SCORECARD TEMPLATES
// ================================================
export const mockScorecardTemplates: ScorecardTemplate[] = [
  {
    id: 1,
    name: "Technical Interview Template",
    department: { id: 1, name: "Engineering", description: "Tech team" },
    createdAt: "2025-10-01T08:00:00Z",
    archived: false,
    criteria: [
      { id: 1, templateId: 1, name: "Problem Solving", weight: 0.4, displayOrder: 1 },
      { id: 2, templateId: 1, name: "System Design", weight: 0.35, displayOrder: 2 },
      { id: 3, templateId: 1, name: "Communication", weight: 0.25, displayOrder: 3 },
    ],
  },
  {
    id: 2,
    name: "HR Screening Template",
    department: { id: 2, name: "Human Resources", description: "HR team" },
    createdAt: "2025-10-05T08:00:00Z",
    archived: false,
    criteria: [
      { id: 4, templateId: 2, name: "Culture Fit", weight: 0.5, displayOrder: 1 },
      { id: 5, templateId: 2, name: "Experience Relevance", weight: 0.3, displayOrder: 2 },
      { id: 6, templateId: 2, name: "Motivation", weight: 0.2, displayOrder: 3 },
    ],
  },
  {
    id: 3,
    name: "Manager Round Template",
    department: { id: 1, name: "Engineering", description: "Tech team" },
    createdAt: "2025-11-01T08:00:00Z",
    archived: true,
    criteria: [
      { id: 7, templateId: 3, name: "Leadership Potential", weight: 0.6, displayOrder: 1 },
      { id: 8, templateId: 3, name: "Strategic Thinking", weight: 0.4, displayOrder: 2 },
    ],
  },
];

export const mockScorecardTemplatePage: Page<ScorecardTemplate> = {
  content: mockScorecardTemplates.filter(t => !t.archived),
  totalElements: 2,
  totalPages: 1,
  size: 10,
  number: 0,
};

// ================================================
// 2. INTERVIEW SCORECARD (per interview)
// ================================================
export const mockInterviewScores: Record<number, InterviewScoresResponse[]> = {
  // Interview ID 1 — 2 interviewers submitted scores
  1: [
    {
      interviewId: 1,
      interviewerId: 10,
      interviewerName: "Nguyen Van A (Interviewer)",
      recommendation: "HIRE",
      strengths: "Strong algorithmic thinking, clean code",
      weaknesses: "Needs improvement in system design at scale",
      generalComment: "Good candidate overall, showed good fundamentals",
      submitted: true,
      scores: [
        { criterionId: 1, criterionName: "Problem Solving", score: 4, comment: "Solved DP problem efficiently" },
        { criterionId: 2, criterionName: "System Design", score: 3, comment: "Basic design, missed caching" },
        { criterionId: 3, criterionName: "Communication", score: 5, comment: "Very clear and structured" },
      ],
    },
    {
      interviewId: 1,
      interviewerId: 11,
      interviewerName: "Tran Thi B (Senior Engineer)",
      recommendation: "HIRE",
      strengths: "Fast learner, proactive",
      weaknesses: "Limited experience with distributed systems",
      generalComment: "Would fit well in team",
      submitted: true,
      scores: [
        { criterionId: 1, criterionName: "Problem Solving", score: 5, comment: "Excellent" },
        { criterionId: 2, criterionName: "System Design", score: 3, comment: "Room to grow" },
        { criterionId: 3, criterionName: "Communication", score: 4, comment: "Good" },
      ],
    },
  ],
  // Interview ID 2 — 1 interviewer
  2: [
    {
      interviewId: 2,
      interviewerId: 10,
      interviewerName: "Nguyen Van A (Interviewer)",
      recommendation: "NEUTRAL",
      strengths: "Relevant experience",
      weaknesses: "Struggled with live coding",
      generalComment: "Average performance",
      submitted: true,
      scores: [
        { criterionId: 4, criterionName: "Culture Fit", score: 3, comment: "OK" },
        { criterionId: 5, criterionName: "Experience Relevance", score: 4, comment: "Relevant background" },
        { criterionId: 6, criterionName: "Motivation", score: 3, comment: "Seems hesitant" },
      ],
    },
  ],
};

// My scores (INTERVIEWER role view)
export const mockMyScores: Record<number, InterviewScoresResponse> = {
  1: mockInterviewScores[1][0],
  2: mockInterviewScores[2][0],
};

// ================================================
// 3. CANDIDATE EVALUATION SUMMARY
// ================================================
export const mockEvaluations: Record<number, EvaluationSummaryResponse> = {
  // applicationId → evaluation
  101: {
    applicationId: 101,
    candidateName: "Le Hoang Nam",
    jobTitle: "Backend Engineer",
    aggregateScore: 4.2,
    maxPossibleScore: 5,
    overallRecommendation: "HIRE",
    topStrengths: [
      "Excellent problem-solving skills",
      "Strong communication",
      "Fast learner",
    ],
    mainConcerns: [
      "System design needs improvement",
      "Limited distributed systems experience",
    ],
    criteriaScores: [
      { criterionName: "Problem Solving", averageScore: 4.5, maxScore: 5 },
      { criterionName: "System Design", averageScore: 3.0, maxScore: 5 },
      { criterionName: "Communication", averageScore: 4.5, maxScore: 5 },
    ],
    interviewerSummaries: [
      {
        interviewerName: "Nguyen Van A",
        totalScore: 4.0,
        recommendation: "HIRE",
        comment: "Good candidate overall",
        date: "2026-02-10T09:00:00Z",
      },
      {
        interviewerName: "Tran Thi B",
        totalScore: 4.4,
        recommendation: "HIRE",
        comment: "Would fit well in team",
        date: "2026-02-12T10:00:00Z",
      },
    ],
  },
  102: {
    applicationId: 102,
    candidateName: "Pham Thi Thu",
    jobTitle: "Frontend Developer",
    aggregateScore: 3.5,
    maxPossibleScore: 5,
    overallRecommendation: "NEUTRAL",
    topStrengths: ["Relevant experience", "Creative approach"],
    mainConcerns: ["Struggled with live coding", "Limited React expertise"],
    criteriaScores: [
      { criterionName: "Culture Fit", averageScore: 3.5, maxScore: 5 },
      { criterionName: "Experience Relevance", averageScore: 4.0, maxScore: 5 },
      { criterionName: "Motivation", averageScore: 3.0, maxScore: 5 },
    ],
    interviewerSummaries: [
      {
        interviewerName: "Nguyen Van A",
        totalScore: 3.5,
        recommendation: "NEUTRAL",
        comment: "Average performance",
        date: "2026-02-15T09:00:00Z",
      },
    ],
  },
};

// ================================================
// 4. CANDIDATE RANKING
// ================================================
export const mockRankings: Record<number, CandidateRankingResponse> = {
  // jobId → ranking
  1: {
    jobId: 1,
    jobTitle: "Backend Engineer",
    candidates: [
      {
        rank: 1,
        applicationId: 101,
        candidateId: 201,
        candidateName: "Le Hoang Nam",
        candidateEmail: "nam.le@email.com",
        jobTitle: "Backend Engineer",
        stage: "INTERVIEW",
        aggregateScore: 4.2,
        recommendation: "HIRE",
        experienceYears: 4,
      },
      {
        rank: 2,
        applicationId: 103,
        candidateId: 203,
        candidateName: "Vo Minh Khoa",
        candidateEmail: "khoa.vo@email.com",
        jobTitle: "Backend Engineer",
        stage: "INTERVIEW",
        aggregateScore: 3.8,
        recommendation: "HIRE",
        experienceYears: 3,
      },
      {
        rank: 3,
        applicationId: 104,
        candidateId: 204,
        candidateName: "Dang Thi Lan",
        candidateEmail: "lan.dang@email.com",
        jobTitle: "Backend Engineer",
        stage: "SCREENING",
        aggregateScore: 3.2,
        recommendation: "NEUTRAL",
        experienceYears: 2,
      },
    ],
  },
  2: {
    jobId: 2,
    jobTitle: "Frontend Developer",
    candidates: [
      {
        rank: 1,
        applicationId: 102,
        candidateId: 202,
        candidateName: "Pham Thi Thu",
        candidateEmail: "thu.pham@email.com",
        jobTitle: "Frontend Developer",
        stage: "OFFER",
        aggregateScore: 3.5,
        recommendation: "NEUTRAL",
        experienceYears: 2,
      },
    ],
  },
};

// ================================================
// 5 & 6. OFFERS (Draft, Pending, Approved)
// ================================================
export const mockOffers: OfferResponse[] = [
  {
    id: 1,
    applicationId: 101,
    candidateName: "Le Hoang Nam",
    jobTitle: "Backend Engineer",
    department: "Engineering",
    positionTitle: "Senior Backend Engineer",
    salary: 2500,
    bonus: 500,
    startDate: "2026-04-01",
    contractType: "Full-time",
    workLocation: "Ho Chi Minh City",
    benefits: "Health insurance, 13th month salary, 15 days annual leave",
    notes: "Offer valid for 7 days",
    status: "DRAFT",
    createdBy: "hr@company.com",
    createdAt: "2026-03-01T08:00:00Z",
    approvals: [],
  },
  {
    id: 2,
    applicationId: 103,
    candidateName: "Vo Minh Khoa",
    jobTitle: "Backend Engineer",
    department: "Engineering",
    positionTitle: "Backend Engineer",
    salary: 1800,
    bonus: 200,
    startDate: "2026-04-15",
    contractType: "Full-time",
    workLocation: "Hanoi",
    benefits: "Health insurance, 12 days annual leave",
    notes: "Please confirm within 5 days",
    status: "PENDING_APPROVAL",
    createdBy: "hr@company.com",
    createdAt: "2026-03-05T08:00:00Z",
    approvals: [],
  },
  {
    id: 3,
    applicationId: 102,
    candidateName: "Pham Thi Thu",
    jobTitle: "Frontend Developer",
    department: "Product",
    positionTitle: "Frontend Developer",
    salary: 1500,
    startDate: "2026-05-01",
    contractType: "Full-time",
    workLocation: "Ho Chi Minh City",
    benefits: "Health insurance, 12 days annual leave",
    status: "APPROVED",
    createdBy: "hr@company.com",
    createdAt: "2026-02-20T08:00:00Z",
    approvals: [
      {
        id: 1,
        approvedBy: "hrmanager@company.com",
        status: "APPROVED",
        comment: "Looks good, please proceed",
        approvedAt: "2026-02-25T10:00:00Z",
      },
    ],
  },
];

export const mockOfferPage: Page<OfferResponse> = {
  content: mockOffers,
  totalElements: 3,
  totalPages: 1,
  size: 10,
  number: 0,
};

// ================================================
// 8. ONBOARDING CHECKLIST
// ================================================
export const mockOnboardingChecklists: Record<number, OnboardingChecklistResponse> = {
  // applicationId → checklist
  102: {
    applicationId: 102,
    candidateName: "Pham Thi Thu",
    completedCount: 2,
    totalCount: 5,
    progressPercent: 40,
    items: [
      {
        id: 1,
        taskName: "Send offer letter to candidate",
        assignedTo: "hr@company.com",
        dueDate: "2026-03-15",
        status: "DONE",
      },
      {
        id: 2,
        taskName: "Collect signed contract",
        assignedTo: "hr@company.com",
        dueDate: "2026-03-20",
        status: "DONE",
      },
      {
        id: 3,
        taskName: "Prepare laptop and equipment",
        assignedTo: "it@company.com",
        dueDate: "2026-04-28",
        status: "IN_PROGRESS",
      },
      {
        id: 4,
        taskName: "Set up email and accounts",
        assignedTo: "it@company.com",
        dueDate: "2026-04-30",
        status: "PENDING",
      },
      {
        id: 5,
        taskName: "Schedule orientation session",
        assignedTo: "hr@company.com",
        dueDate: "2026-05-01",
        status: "PENDING",
      },
    ],
  },
  101: {
    applicationId: 101,
    candidateName: "Le Hoang Nam",
    completedCount: 0,
    totalCount: 3,
    progressPercent: 0,
    items: [
      {
        id: 6,
        taskName: "Send offer letter",
        assignedTo: "hr@company.com",
        dueDate: "2026-04-05",
        status: "PENDING",
      },
      {
        id: 7,
        taskName: "Background check",
        assignedTo: "hr@company.com",
        dueDate: "2026-04-10",
        status: "PENDING",
      },
      {
        id: 8,
        taskName: "Provide system access",
        assignedTo: "it@company.com",
        dueDate: "2026-04-01",
        status: "PENDING",
      },
    ],
  },
};

// ================================================
// SUPPORTING: Interviews, Candidates, Applications, Jobs
// ================================================
export const mockInterviews: InterviewResponse[] = [
  {
    id: 1,
    applicationId: 101,
    scheduledAt: "2026-02-10T09:00:00Z",
    location: "Google Meet",
    status: "COMPLETED",
    candidateName: "Le Hoang Nam",
    jobTitle: "Backend Engineer",
    type: "ONLINE",
  },
  {
    id: 2,
    applicationId: 102,
    scheduledAt: "2026-02-15T14:00:00Z",
    location: "Office Room A",
    status: "COMPLETED",
    candidateName: "Pham Thi Thu",
    jobTitle: "Frontend Developer",
    type: "OFFLINE",
  },
  {
    id: 3,
    applicationId: 103,
    scheduledAt: "2026-03-20T10:00:00Z",
    location: "Google Meet",
    status: "SCHEDULED",
    candidateName: "Vo Minh Khoa",
    jobTitle: "Backend Engineer",
    type: "ONLINE",
  },
];

export const mockCandidates: Candidate[] = [
  { id: 201, fullName: "Le Hoang Nam", email: "nam.le@email.com", phone: "0901234567", currentCompany: "ABC Corp", createdAt: "2026-01-01T00:00:00Z" },
  { id: 202, fullName: "Pham Thi Thu", email: "thu.pham@email.com", phone: "0912345678", currentCompany: "XYZ Tech", createdAt: "2026-01-05T00:00:00Z" },
  { id: 203, fullName: "Vo Minh Khoa", email: "khoa.vo@email.com", phone: "0923456789", currentCompany: "DEF Solutions", createdAt: "2026-01-10T00:00:00Z" },
  { id: 204, fullName: "Dang Thi Lan", email: "lan.dang@email.com", phone: "0934567890", createdAt: "2026-01-15T00:00:00Z" },
];

export const mockApplications: Application[] = [
  { id: 101, candidateId: 201, jobId: 1, stage: "INTERVIEW", status: "ACTIVE", candidateName: "Le Hoang Nam", jobTitle: "Backend Engineer" },
  { id: 102, candidateId: 202, jobId: 2, stage: "OFFER", status: "ACTIVE", candidateName: "Pham Thi Thu", jobTitle: "Frontend Developer" },
  { id: 103, candidateId: 203, jobId: 1, stage: "INTERVIEW", status: "ACTIVE", candidateName: "Vo Minh Khoa", jobTitle: "Backend Engineer" },
  { id: 104, candidateId: 204, jobId: 1, stage: "SCREENING", status: "ACTIVE", candidateName: "Dang Thi Lan", jobTitle: "Backend Engineer" },
];

export const mockJobs: Job[] = [
  { id: 1, title: "Backend Engineer", description: "Build scalable microservices", departmentId: 1, status: "APPROVED", headcount: 2 },
  { id: 2, title: "Frontend Developer", description: "Build React UI", departmentId: 2, status: "APPROVED", headcount: 1 },
];
