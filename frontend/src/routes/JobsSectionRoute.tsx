import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "@/hooks/useAuth";

/** SYSTEM_ADMIN cannot access job list, job detail, create, or edit. */
export default function JobsSectionRoute() {
    const { user } = useAuth();

    if (!user) {
        return <Navigate to="/login" replace />;
    }

    if (user.role === "SYSTEM_ADMIN") {
        return <Navigate to="/admin/dashboard" replace />;
    }

    return <Outlet />;
}
