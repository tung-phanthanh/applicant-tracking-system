import { Navigate, BrowserRouter, Route, Routes } from "react-router-dom";
import { AuthProvider } from "@/contexts/AuthContext";
import ProtectedRoute from "@/routes/ProtectedRoute";
import AppLayout from "@/layouts/AppLayout";
import AuthLayout from "@/layouts/AuthLayout";
import LoginPage from "@/pages/auth/LoginPage";
import DashboardPage from "@/pages/recruiter/DashboardPage";
import ProfilePage from "@/pages/recruiter/ProfilePage";
import JobsPage from "@/pages/recruiter/JobsPage";
import CreateJobPage from "@/pages/recruiter/CreateJobPage";
import EditJobPage from "@/pages/recruiter/EditJobPage";

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

              {/* Jobs module */}
              <Route path="/jobs" element={<JobsPage />} />
              <Route path="/jobs/new" element={<CreateJobPage />} />
              <Route path="/jobs/:id/edit" element={<EditJobPage />} />

              {/* Placeholder routes for other future modules */}
              <Route
                path="/candidates"
                element={<ComingSoon title="Candidates" />}
              />
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
