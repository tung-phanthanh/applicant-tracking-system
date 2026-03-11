import { Navigate, BrowserRouter, Route, Routes } from "react-router-dom";
import { AuthProvider } from "@/contexts/AuthContext";
import ProtectedRoute from "@/routes/ProtectedRoute";
import AdminRoute from "@/routes/AdminRoute";
import AppLayout from "@/layouts/AppLayout";
import AuthLayout from "@/layouts/AuthLayout";
import LoginPage from "@/pages/auth/LoginPage";
import ForgotPasswordPage from "@/pages/auth/ForgotPasswordPage";
import SetPasswordPage from "@/pages/auth/SetPasswordPage";
import ResetPasswordPage from "@/pages/auth/ResetPasswordPage";
import DashboardPage from "@/pages/recruiter/DashboardPage";
import ProfilePage from "@/pages/recruiter/ProfilePage";
import ChangePasswordPage from "@/pages/recruiter/ChangePasswordPage";
import AdminUsersPage from "@/pages/admin/AdminUsersPage";
import AdminCreateUserPage from "@/pages/admin/AdminCreateUserPage";
import AdminEditUserPage from "@/pages/admin/AdminEditUserPage";

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
              <Route path="/jobs" element={<ComingSoon title="Jobs" />} />
              <Route path="/candidates" element={<ComingSoon title="Candidates" />} />
              <Route path="/interviews" element={<ComingSoon title="Interviews" />} />

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
