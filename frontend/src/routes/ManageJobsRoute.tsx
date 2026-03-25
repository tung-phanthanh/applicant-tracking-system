import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "@/hooks/useAuth";

/** Allows HR and HR_MANAGER to create or edit job postings. */
export default function ManageJobsRoute() {
    const { user } = useAuth();

    if (!user) {
        return <Navigate to="/login" replace />;
    }

    if (user.role !== "HR" && user.role !== "HR_MANAGER") {
        return <Navigate to="/jobs" replace />;
    }

    return <Outlet />;
}
