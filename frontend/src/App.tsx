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
import JobsPage from "@/pages/jobs/JobsPage";
import JobDetailPage from "@/pages/jobs/JobDetailPage";
import CreateJobPage from "@/pages/jobs/CreateJobPage";
import EditJobPage from "@/pages/jobs/EditJobPage";
import PendingJobsPage from "@/pages/jobs/PendingJobsPage";
import ManageJobsRoute from "@/routes/ManageJobsRoute";
import HrManagerRoute from "@/routes/HrManagerRoute";

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

              <Route path="/jobs" element={<JobsPage />} />
              <Route element={<ManageJobsRoute />}>
                <Route path="/jobs/create" element={<CreateJobPage />} />
                <Route path="/jobs/:jobId/edit" element={<EditJobPage />} />
              </Route>
              <Route element={<HrManagerRoute />}>
                <Route
                  path="/jobs/pending-approvals"
                  element={<PendingJobsPage />}
                />
              </Route>
              <Route path="/jobs/:jobId" element={<JobDetailPage />} />

              {/* Placeholder routes */}
              <Route path="/interviews" element={<ComingSoon title="Interviews" />} />

              {/* HR-only route */}
              <Route element={<HrRoute />}>
                <Route path="/candidates" element={<CandidateListPage />} />
                <Route path="/candidates/:candidateId" element={<CandidateProfilePage />} />
              </Route>

              {/* Admin-only routes — SYSTEM_ADMIN only */}
              <Route element={<AdminRoute />}>
                <Route path="/admin/users" element={<AdminUsersPage />} />
                <Route path="/admin/users/create" element={<AdminCreateUserPage />} />
                <Route path="/admin/users/:id/edit" element={<AdminEditUserPage />} />
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
