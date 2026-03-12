/**
 * ================================================================
 * MOCK HANDLER — URL pattern-based interceptor for apiFetch
 * 
 * Kích hoạt khi: VITE_MOCK=true (xem .env.mock)
 * Cách dùng: thay đổi VITE_MOCK=true trong .env hoặc .env.development.local
 * ================================================================
 */

import {
  mockScorecardTemplatePage,
  mockScorecardTemplates,
  mockInterviewScores,
  mockMyScores,
  mockEvaluations,
  mockRankings,
  mockOfferPage,
  mockOffers,
  mockOnboardingChecklists,
  mockInterviews,
  mockCandidates,
  mockApplications,
  mockJobs,
} from "./mockData";

// Simulated network delay (ms) — giả lập độ trễ mạng
const MOCK_DELAY = 300;

const delay = (ms: number) => new Promise((r) => setTimeout(r, ms));

type MockResponse = unknown;

/**
 * Try to match the request path against all mock rules.
 * Returns undefined if no rule matched (then apiFetch falls through to real backend).
 */
export async function mockHandler(
  method: string,
  path: string,
  body?: unknown
): Promise<MockResponse | undefined> {
  await delay(MOCK_DELAY);

  // ==============================
  // SCORECARD TEMPLATES
  // ==============================
  if (method === "GET" && path === "/scorecards/templates") {
    return mockScorecardTemplatePage;
  }
  if (method === "GET" && /^\/scorecards\/templates\/(\d+)$/.test(path)) {
    const id = Number(path.split("/").pop());
    return mockScorecardTemplates.find((t) => t.id === id) ?? null;
  }
  if (method === "POST" && path === "/scorecards/templates") {
    const req = body as { name: string; departmentId?: number };
    const newTemplate = {
      id: Date.now(),
      name: req.name,
      departmentId: req.departmentId,
      createdAt: new Date().toISOString(),
      archived: false,
      criteria: [],
    };
    mockScorecardTemplates.push(newTemplate as any);
    return newTemplate;
  }
  if (method === "PUT" && /^\/scorecards\/templates\/(\d+)$/.test(path)) {
    const id = Number(path.split("/")[3]);
    const idx = mockScorecardTemplates.findIndex((t) => t.id === id);
    if (idx !== -1) {
      Object.assign(mockScorecardTemplates[idx], body);
      return mockScorecardTemplates[idx];
    }
    return null;
  }
  if (method === "DELETE" && /^\/scorecards\/templates\/(\d+)$/.test(path)) {
    return undefined; // 204
  }
  if (method === "PATCH" && /^\/scorecards\/templates\/(\d+)\/archive$/.test(path)) {
    const id = Number(path.split("/")[3]);
    const t = mockScorecardTemplates.find((x) => x.id === id);
    if (t) t.archived = true;
    return t;
  }
  if (method === "PATCH" && /^\/scorecards\/templates\/(\d+)\/unarchive$/.test(path)) {
    const id = Number(path.split("/")[3]);
    const t = mockScorecardTemplates.find((x) => x.id === id);
    if (t) t.archived = false;
    return t;
  }
  if (method === "POST" && /^\/scorecards\/templates\/(\d+)\/criteria$/.test(path)) {
    const templateId = Number(path.split("/")[3]);
    const req = body as { name: string; weight: number };
    const criterion = {
      id: Date.now(),
      templateId,
      name: req.name,
      weight: req.weight,
      displayOrder: 99,
    };
    return criterion;
  }
  if (method === "PUT" && /^\/scorecards\/templates\/(\d+)\/criteria\/reorder$/.test(path)) {
    return undefined; // 204
  }
  if (method === "DELETE" && /^\/scorecards\/criteria\/(\d+)$/.test(path)) {
    return undefined; // 204
  }

  // ==============================
  // INTERVIEW SCORES (Feature 2 — Interview Scorecard)
  // ==============================
  if (method === "GET" && /^\/interviews\/(\d+)\/scores$/.test(path)) {
    const interviewId = Number(path.split("/")[2]);
    return mockInterviewScores[interviewId] ?? [];
  }
  if (method === "GET" && /^\/interviews\/(\d+)\/scores\/my$/.test(path)) {
    const interviewId = Number(path.split("/")[2]);
    return mockMyScores[interviewId] ?? null;
  }
  if (method === "POST" && /^\/interviews\/(\d+)\/scores$/.test(path)) {
    const interviewId = Number(path.split("/")[2]);
    const req = body as any;
    return {
      interviewId,
      interviewerId: 10,
      interviewerName: "Current User",
      recommendation: req.recommendation,
      strengths: req.strengths,
      weaknesses: req.weaknesses,
      generalComment: req.generalComment,
      submitted: true,
      scores: req.scores ?? [],
    };
  }

  // ==============================
  // INTERVIEWS (list)
  // ==============================
  if (method === "GET" && path === "/interviews") {
    return mockInterviews;
  }
  if (method === "GET" && path === "/interviews/upcoming") {
    return mockInterviews.filter((i) => i.status === "SCHEDULED");
  }
  if (method === "GET" && /^\/interviews\/(\d+)$/.test(path)) {
    const id = Number(path.split("/").pop());
    return mockInterviews.find((i) => i.id === id) ?? null;
  }
  if (method === "PUT" && /^\/interviews\/(\d+)\/reschedule$/.test(path)) {
    const id = Number(path.split("/")[2]);
    const req = body as any;
    const interview = mockInterviews.find((i) => i.id === id);
    if (interview) {
      if (req.scheduledAt) interview.scheduledAt = req.scheduledAt;
      if (req.location) interview.location = req.location;
      if (req.type) interview.type = req.type;
      interview.status = "SCHEDULED"; // Reset status to SCHEDULED if it was COMPLETED/CANCELLED
      return interview;
    }
    return null;
  }

  // ==============================
  // CANDIDATE EVALUATION SUMMARY (Feature 3)
  // ==============================
  if (method === "GET" && /^\/applications\/(\d+)\/evaluation$/.test(path)) {
    const applicationId = Number(path.split("/")[2]);
    return mockEvaluations[applicationId] ?? null;
  }

  // ==============================
  // CANDIDATE RANKING (Feature 4)
  // ==============================
  if (method === "GET" && /^\/jobs\/(\d+)\/ranking$/.test(path)) {
    const jobId = Number(path.split("/")[2]);
    return mockRankings[jobId] ?? { jobId, jobTitle: "Unknown", candidates: [] };
  }

  // ==============================
  // OFFERS — Draft, Approval, Preview (Features 5, 6, 7)
  // ==============================
  if (method === "GET" && path === "/offers") {
    return mockOfferPage;
  }
  if (method === "GET" && /^\/offers\/(\d+)$/.test(path)) {
    const id = Number(path.split("/").pop());
    return mockOffers.find((o) => o.id === id) ?? null;
  }
  if (method === "POST" && path === "/offers") {
    const req = body as any;
    const newOffer = {
      id: Date.now(),
      applicationId: req.applicationId,
      candidateName: "New Candidate",
      jobTitle: "New Position",
      positionTitle: req.positionTitle,
      salary: req.salary,
      bonus: req.bonus,
      startDate: req.startDate,
      contractType: req.contractType,
      workLocation: req.workLocation,
      benefits: req.benefits,
      notes: req.notes,
      status: "DRAFT" as const,
      createdBy: "hr@company.com",
      createdAt: new Date().toISOString(),
      approvals: [],
    };
    mockOffers.push(newOffer);
    return newOffer;
  }
  if (method === "PUT" && /^\/offers\/(\d+)$/.test(path)) {
    const id = Number(path.split("/").pop());
    const idx = mockOffers.findIndex((o) => o.id === id);
    if (idx !== -1) {
      Object.assign(mockOffers[idx], body);
      return mockOffers[idx];
    }
    return null;
  }
  if (method === "POST" && /^\/offers\/(\d+)\/submit$/.test(path)) {
    const id = Number(path.split("/")[2]);
    const offer = mockOffers.find((o) => o.id === id);
    if (offer) offer.status = "PENDING_APPROVAL";
    return undefined; // 204
  }
  if (method === "POST" && /^\/offers\/(\d+)\/approve$/.test(path)) {
    const id = Number(path.split("/")[2]);
    const offer = mockOffers.find((o) => o.id === id);
    if (offer) {
      offer.status = "APPROVED";
      offer.approvals.push({
        id: Date.now(),
        approvedBy: "hrmanager@company.com",
        status: "APPROVED",
        comment: (body as any)?.comment ?? "",
        approvedAt: new Date().toISOString(),
      });
    }
    return undefined; // 204
  }
  if (method === "POST" && /^\/offers\/(\d+)\/reject$/.test(path)) {
    const id = Number(path.split("/")[2]);
    const offer = mockOffers.find((o) => o.id === id);
    if (offer) {
      offer.status = "REJECTED";
      offer.approvals.push({
        id: Date.now(),
        approvedBy: "hrmanager@company.com",
        status: "REJECTED",
        comment: (body as any)?.comment ?? "",
        approvedAt: new Date().toISOString(),
      });
    }
    return undefined; // 204
  }

  // ==============================
  // ONBOARDING CHECKLIST (Feature 8)
  // ==============================
  if (method === "GET" && /^\/onboarding\/(\d+)\/checklist$/.test(path)) {
    const applicationId = Number(path.split("/")[2]);
    return mockOnboardingChecklists[applicationId] ?? null;
  }
  if (method === "POST" && /^\/onboarding\/(\d+)\/checklist$/.test(path)) {
    const applicationId = Number(path.split("/")[2]);
    const req = body as { items: { taskName: string; assignedTo?: string; dueDate?: string }[] };
    const newChecklist = {
      applicationId,
      candidateName: mockApplications.find((a) => a.id === applicationId)?.candidateName ?? "Candidate",
      completedCount: 0,
      totalCount: req.items.length,
      progressPercent: 0,
      items: req.items.map((item, i) => ({
        id: Date.now() + i,
        taskName: item.taskName,
        assignedTo: item.assignedTo,
        dueDate: item.dueDate,
        status: "PENDING" as const,
      })),
    };
    mockOnboardingChecklists[applicationId] = newChecklist;
    return newChecklist;
  }
  if (method === "PUT" && /^\/onboarding\/items\/(\d+)$/.test(path)) {
    const itemId = Number(path.split("/").pop());
    const req = body as any;
    // Find and update item in all checklists
    for (const checklist of Object.values(mockOnboardingChecklists)) {
      const item = checklist.items.find((i) => i.id === itemId);
      if (item) {
        if (req.taskName) item.taskName = req.taskName;
        if (req.assignedTo) item.assignedTo = req.assignedTo;
        if (req.dueDate) item.dueDate = req.dueDate;
        if (req.status) item.status = req.status;
        // Recalculate progress
        checklist.completedCount = checklist.items.filter((i) => i.status === "DONE").length;
        checklist.progressPercent = Math.round((checklist.completedCount / checklist.totalCount) * 100);
        return item;
      }
    }
    return null;
  }

  // ==============================
  // CANDIDATES
  // ==============================
  if (method === "GET" && path === "/candidates") {
    return mockCandidates;
  }
  if (method === "GET" && /^\/candidates\/(\d+)$/.test(path)) {
    const id = Number(path.split("/").pop());
    return mockCandidates.find((c) => c.id === id) ?? null;
  }
  if (method === "GET" && path === "/candidates/count") {
    return mockCandidates.length;
  }

  // ==============================
  // APPLICATIONS
  // ==============================
  if (method === "GET" && path === "/applications") {
    return mockApplications;
  }
  if (method === "GET" && path === "/applications/recent") {
    return mockApplications.slice(0, 5);
  }
  if (method === "GET" && /^\/applications\/(\d+)$/.test(path)) {
    const id = Number(path.split("/").pop());
    return mockApplications.find((a) => a.id === id) ?? null;
  }
  if (method === "PUT" && /^\/applications\/(\d+)\/stage$/.test(path)) {
    const id = Number(path.split("/")[2]);
    const req = body as any;
    const app = mockApplications.find((a) => a.id === id);
    if (app) {
        app.stage = req.stage;
        return app;
    }
    return null;
  }

  // ==============================
  // JOBS
  // ==============================
  if (method === "GET" && path === "/jobs") {
    return mockJobs;
  }
  if (method === "POST" && path === "/jobs") {
    const req = body as any;
    const newJob = {
      id: Date.now(),
      title: req.title || "New Position",
      description: req.description || "",
      department: req.department || "Engineering",
      location: req.location || "Remote",
      type: req.type || "FULL_TIME",
      experienceLevel: req.experienceLevel || "MID_LEVEL",
      status: req.status || "OPEN",
      headcount: req.headcount || 1,
      createdAt: new Date().toISOString()
    };
    mockJobs.push(newJob);
    return newJob;
  }
  if (method === "GET" && path === "/jobs/active/count") {
    return mockJobs.filter((j) => j.status === "OPEN" || j.status === "PUBLISHED" || j.status === "ACTIVE").length || 3;
  }
  if (method === "GET" && /^\/jobs\/(\d+)$/.test(path)) {
    const id = Number(path.split("/").pop());
    return mockJobs.find((j) => j.id === id) ?? null;
  }

  // No mock matched — return undefined to fall through to real API
  return undefined;
}
