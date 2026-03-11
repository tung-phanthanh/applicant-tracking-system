import { Navigate, BrowserRouter, Route, Routes } from "react-router-dom";
import { AuthProvider } from "@/contexts/AuthContext";
import ProtectedRoute from "@/routes/ProtectedRoute";
import AppLayout from "@/layouts/AppLayout";
import AuthLayout from "@/layouts/AuthLayout";
import LoginPage from "@/pages/auth/LoginPage";
import DashboardPage from "@/pages/recruiter/DashboardPage";
import ProfilePage from "@/pages/recruiter/ProfilePage";

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
          {/* Public routes */}
          <Route element={<AuthLayout />}>
            <Route path="/login" element={<LoginPage />} />
          </Route>

          {/* Protected routes */}
          <Route element={<ProtectedRoute />}>
            <Route element={<AppLayout />}>
              <Route path="/" element={<Navigate to="/dashboard" replace />} />
              <Route path="/dashboard" element={<DashboardPage />} />
              <Route path="/profile" element={<ProfilePage />} />

              {/* Placeholder routes for future pages */}
              <Route path="/jobs" element={<ComingSoon title="Jobs" />} />
              <Route
                path="/candidates"
                element={<ComingSoon title="Candidates" />}
              />
              <Route
                path="/interviews"
                element={<ComingSoon title="Interviews" />}
              />

              {/* Admin routes */}
              <Route path="/admin">
                <Route path="dashboard" element={<AdminDashboardPage />} />
                <Route path="roles" element={<RolesPage />} />
                <Route path="departments" element={<DepartmentsPage />} />
                <Route path="system-config" element={<SystemConfigPage />} />
                <Route path="audit-logs" element={<AuditLogPage />} />
              </Route>

              {/* Shared or standalone routes */}
              <Route path="/notifications" element={<NotificationsPage />} />
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
