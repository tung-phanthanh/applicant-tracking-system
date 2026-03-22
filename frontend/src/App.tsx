import { Navigate, BrowserRouter, Route, Routes } from "react-router-dom";
import { AuthProvider } from "@/contexts/AuthContext";
import ProtectedRoute from "@/routes/ProtectedRoute";
import AdminRoute from "@/routes/AdminRoute";
import HrRoute from "@/routes/HrRoute";
import AppLayout from "@/layouts/AppLayout";
import AuthLayout from "@/layouts/AuthLayout";
import LoginPage from "@/pages/auth/LoginPage";
import ForgotPasswordPage from "@/pages/auth/ForgotPasswordPage";
import SetPasswordPage from "@/pages/auth/SetPasswordPage";
import ResetPasswordPage from "@/pages/auth/ResetPasswordPage";
import DashboardPage from "@/pages/recruiter/DashboardPage";
import ProfilePage from "@/pages/recruiter/ProfilePage";
import ChangePasswordPage from "@/pages/recruiter/ChangePasswordPage";
import CandidateListPage from "@/pages/recruiter/CandidateListPage";
import AdminUsersPage from "@/pages/admin/AdminUsersPage";
import AdminCreateUserPage from "@/pages/admin/AdminCreateUserPage";
import AdminEditUserPage from "@/pages/admin/AdminEditUserPage";

// Features
// 1. Scorecard Templates
import ScorecardTemplateListPage from "@/pages/recruiter/scorecard/ScorecardTemplateListPage";
import ScorecardTemplateFormPage from "@/pages/recruiter/scorecard/ScorecardTemplateFormPage";
import ScorecardTemplateDetailPage from "@/pages/recruiter/scorecard/ScorecardTemplateDetailPage";

// 2. Interview Scorecard
import InterviewsPage from "@/pages/recruiter/interviews/InterviewsPage";
import InterviewEvaluationFormPage from "@/pages/recruiter/interviews/InterviewEvaluationFormPage";

// 3. Candidate Evaluation Summary & 4. Candidate Ranking
import CandidatesPage from "@/pages/recruiter/candidates/CandidatesPage";
import CandidateEvaluationSummaryPage from "@/pages/recruiter/candidates/CandidateEvaluationSummaryPage";
import CandidateRankingPage from "@/pages/recruiter/candidates/CandidateRankingPage";

// 5, 6, 7. Offers
import CreateOfferPage from "@/pages/recruiter/offers/CreateOfferPage";
import OfferApprovalPage from "@/pages/recruiter/offers/OfferApprovalPage";
import OfferPdfPreviewPage from "@/pages/recruiter/offers/OfferPdfPreviewPage";
import SendOfferPage from "@/pages/recruiter/offers/SendOfferPage";

// 8. Onboarding Checklist
import OnboardingChecklistPage from "@/pages/recruiter/onboarding/OnboardingChecklistPage";

// Jobs
import JobsPage from "@/pages/recruiter/jobs/JobsPage";

// Admin Pages
import AdminDashboardPage from "@/pages/admin/AdminDashboardPage";
import RolesPage from "@/pages/admin/RolesPage";
import DepartmentsPage from "@/pages/admin/DepartmentsPage";
import SystemConfigPage from "@/pages/admin/SystemConfigPage";
import AuditLogPage from "@/pages/admin/AuditLogPage";
import NotificationsPage from "@/pages/admin/NotificationsPage";

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          {/* Public auth routes (centered card layout) */}
          <Route element={<AuthLayout />}>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/forgot-password" element={<ForgotPasswordPage />} />
            {/* Token-based pages (linked from emails) */}
            <Route path="/activate" element={<SetPasswordPage />} />
            <Route path="/reset-password" element={<ResetPasswordPage />} />
          </Route>

          {/* Protected routes — any authenticated user */}
          <Route element={<ProtectedRoute />}>
            <Route element={<AppLayout />}>
              <Route path="/" element={<Navigate to="/dashboard" replace />} />
              <Route path="/dashboard" element={<DashboardPage />} />
              <Route path="/profile" element={<ProfilePage />} />
              <Route path="/change-password" element={<ChangePasswordPage />} />
              <Route path="/notifications" element={<NotificationsPage />} />

              {/* 1. Scorecard Templates */}
              <Route path="/scorecards" element={<ScorecardTemplateListPage />} />
              <Route path="/scorecards/new" element={<ScorecardTemplateFormPage />} />
              <Route path="/scorecards/:id" element={<ScorecardTemplateDetailPage />} />
              <Route path="/scorecards/:id/edit" element={<ScorecardTemplateFormPage />} />

              {/* 2. Interview Scorecard */}
              <Route path="/interviews" element={<InterviewsPage />} />
              <Route path="/interviews/:interviewId/evaluate" element={<InterviewEvaluationFormPage />} />

              {/* 3 & 4. Candidate Evaluation & Ranking */}
              <Route path="/candidates" element={<CandidatesPage />} />
              <Route path="/candidates/:applicationId/evaluation" element={<CandidateEvaluationSummaryPage />} />
              <Route path="/jobs/:jobId/ranking" element={<CandidateRankingPage />} />

              {/* 5, 6, 7. Offers */}
              <Route path="/offers/create/:applicationId" element={<CreateOfferPage />} />
              <Route path="/offers/:offerId/approve" element={<OfferApprovalPage />} />
              <Route path="/offers/:offerId/preview" element={<OfferPdfPreviewPage />} />
              <Route path="/offers/:offerId/send" element={<SendOfferPage />} />

              {/* 8. Onboarding Checklist */}
              <Route path="/onboarding/:applicationId" element={<OnboardingChecklistPage />} />

              {/* Jobs */}
              <Route path="/jobs" element={<JobsPage />} />

              {/* HR-only route */}
              <Route element={<HrRoute />}>
                <Route path="/hr/candidates" element={<CandidateListPage />} />
              </Route>

              {/* Admin-only routes — SYSTEM_ADMIN only */}
              <Route element={<AdminRoute />}>
                <Route path="/admin/dashboard" element={<AdminDashboardPage />} />
                <Route path="/admin/users" element={<AdminUsersPage />} />
                <Route path="/admin/users/create" element={<AdminCreateUserPage />} />
                <Route path="/admin/users/:id/edit" element={<AdminEditUserPage />} />
                <Route path="/admin/roles" element={<RolesPage />} />
                <Route path="/admin/departments" element={<DepartmentsPage />} />
                <Route path="/admin/system-config" element={<SystemConfigPage />} />
                <Route path="/admin/audit-logs" element={<AuditLogPage />} />
              </Route>
            </Route>
          </Route>

          {/* Fallback */}
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
