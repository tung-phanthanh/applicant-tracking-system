import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "@/hooks/useAuth";

/** Only HR and SYSTEM_ADMIN may create job postings. HR_MANAGER must use /jobs/:id/edit only. */
export default function CreateJobRoute() {
    const { user } = useAuth();

    if (!user) {
        return <Navigate to="/login" replace />;
    }

    if (user.role !== "HR" && user.role !== "SYSTEM_ADMIN") {
        return <Navigate to="/jobs" replace />;
    }

    return <Outlet />;
}
