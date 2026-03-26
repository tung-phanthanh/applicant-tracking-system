import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "@/hooks/useAuth";

/** HR and HR Manager may create job postings (pending HR Manager approval). */
export default function CreateJobRoute() {
    const { user } = useAuth();

    if (!user) {
        return <Navigate to="/login" replace />;
    }

    if (user.role !== "HR" && user.role !== "HR_MANAGER") {
        return <Navigate to="/jobs" replace />;
    }

    return <Outlet />;
}
