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
import CandidateProfilePage from "@/pages/recruiter/CandidateProfilePage";
import AdminUsersPage from "@/pages/admin/AdminUsersPage";
import AdminCreateUserPage from "@/pages/admin/AdminCreateUserPage";
import AdminEditUserPage from "@/pages/admin/AdminEditUserPage";
import ScorecardTemplatesPage from "@/pages/recruiter/ScorecardTemplatesPage";
import InterviewScorecardPage from "@/pages/recruiter/InterviewScorecardPage";
import CandidateRankingPage from "@/pages/recruiter/CandidateRankingPage";
import OffersListPage from "@/pages/recruiter/OffersListPage";
import OfferFormPage from "@/pages/recruiter/OfferFormPage";
import OfferDetailPage from "@/pages/recruiter/OfferDetailPage";
import OnboardingPage from "@/pages/recruiter/OnboardingPage";
import JobsListPage from "@/pages/recruiter/JobsListPage";

import AdminDashboardPage from "@/pages/admin/AdminDashboardPage";
import RolesPage from "@/pages/admin/RolesPage";
import DepartmentsPage from "@/pages/admin/DepartmentsPage";
import SystemConfigPage from "@/pages/admin/SystemConfigPage";
import AuditLogPage from "@/pages/admin/AuditLogPage";
import NotificationsPage from "@/pages/admin/NotificationsPage";
import { PermissionGuard } from "@/components/shared/PermissionGuard";
import { AccessDenied } from "@/components/shared/AccessDenied";
import { PERMISSIONS } from "@/constants/permissions";

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

              {/* Placeholder routes */}
              <Route path="/interviews" element={<ComingSoon title="Interviews" />} />

              {/* Interview scorecard — accessible to any authenticated user */}
              <Route path="/interviews/:interviewId/scores" element={<InterviewScorecardPage />} />

              {/* HR-only routes */}
              <Route element={<HrRoute />}>
                <Route path="/jobs" element={<JobsListPage />} />
                <Route path="/candidates" element={<CandidateListPage />} />
                <Route path="/candidates/:candidateId" element={<CandidateProfilePage />} />
                <Route path="/scorecard-templates" element={<ScorecardTemplatesPage />} />
                <Route path="/jobs/:jobId/ranking" element={<CandidateRankingPage />} />
                <Route path="/offers" element={<OffersListPage />} />
                <Route path="/offers/new" element={<OfferFormPage />} />
                <Route path="/offers/:id" element={<OfferDetailPage />} />
                <Route path="/offers/:id/edit" element={<OfferFormPage />} />
                <Route path="/onboarding/:id" element={<OnboardingPage />} />
                <Route path="/onboarding/application/:applicationId" element={<OnboardingPage />} />
              </Route>

              {/* Admin-only routes */}
              <Route element={<AdminRoute />}>
                <Route path="/admin/dashboard" element={<PermissionGuard permission={PERMISSIONS.DASHBOARD_VIEW} fallback={<AccessDenied />}><AdminDashboardPage /></PermissionGuard>} />
                <Route path="/admin/users" element={<PermissionGuard permission={PERMISSIONS.USER_MANAGE} fallback={<AccessDenied />}><AdminUsersPage /></PermissionGuard>} />
                <Route path="/admin/users/create" element={<PermissionGuard permission={PERMISSIONS.USER_MANAGE} fallback={<AccessDenied />}><AdminCreateUserPage /></PermissionGuard>} />
                <Route path="/admin/users/:id/edit" element={<PermissionGuard permission={PERMISSIONS.USER_MANAGE} fallback={<AccessDenied />}><AdminEditUserPage /></PermissionGuard>} />
                <Route path="/admin/roles" element={<PermissionGuard permission={PERMISSIONS.ROLE_MANAGE} fallback={<AccessDenied />}><RolesPage /></PermissionGuard>} />
                <Route path="/admin/departments" element={<PermissionGuard permission={PERMISSIONS.DEPARTMENT_MANAGE} fallback={<AccessDenied />}><DepartmentsPage /></PermissionGuard>} />
                <Route path="/admin/system-config" element={<PermissionGuard permission={PERMISSIONS.SYSTEM_CONFIG_MANAGE} fallback={<AccessDenied />}><SystemConfigPage /></PermissionGuard>} />
                <Route path="/admin/audit-logs" element={<PermissionGuard permission={PERMISSIONS.AUDIT_LOG_VIEW} fallback={<AccessDenied />}><AuditLogPage /></PermissionGuard>} />
                <Route path="/admin/notifications" element={<PermissionGuard permission={PERMISSIONS.NOTIFICATION_MANAGE} fallback={<AccessDenied />}><NotificationsPage /></PermissionGuard>} />
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

function ComingSoon({ title }: { title: string }) {
  return (
    <div className="flex h-64 items-center justify-center text-muted-foreground">
      <p className="text-lg font-medium">{title} — Coming Soon</p>
    </div>
  );
}

export default App;
