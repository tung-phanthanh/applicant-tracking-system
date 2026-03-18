import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "@/hooks/useAuth";

/** Wraps routes that require HR role. Redirects to /dashboard if not HR. */
export default function HrRoute() {
    const { user } = useAuth();

    if (!user) {
        return <Navigate to="/login" replace />;
    }

    if (user.role !== "HR" && user.role !== "SYSTEM_ADMIN") {
        return <Navigate to="/dashboard" replace />;
    }

    return <Outlet />;
}
