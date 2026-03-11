import { Navigate, BrowserRouter, Route, Routes } from "react-router-dom";
import { AuthProvider } from "@/contexts/AuthContext";
import ProtectedRoute from "@/routes/ProtectedRoute";
import AppLayout from "@/layouts/AppLayout";
import AuthLayout from "@/layouts/AuthLayout";
import LoginPage from "@/pages/auth/LoginPage";
import DashboardPage from "@/pages/recruiter/DashboardPage";
import ProfilePage from "@/pages/recruiter/ProfilePage";
import CandidatesPage from "@/pages/recruiter/CandidatesPage";
import CandidateProfilePage from "@/pages/recruiter/CandidateProfilePage";

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
              <Route path="/candidates" element={<CandidatesPage />} />
              <Route path="/candidates/:id" element={<CandidateProfilePage />} />
              <Route
                path="/interviews"
                element={<ComingSoon title="Interviews" />}
              />

              {/* Future: Admin routes */}
              {/* <Route path="/admin" element={<AdminGuard />}> */}
              {/*   <Route path="users" element={<AdminUsersPage />} /> */}
              {/* </Route> */}
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
