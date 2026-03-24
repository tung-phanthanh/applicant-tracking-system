import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "@/hooks/useAuth";
import { PERMISSIONS } from "@/constants/permissions";

const ADMIN_PERMISSIONS = [
    PERMISSIONS.DASHBOARD_VIEW,
    PERMISSIONS.USER_MANAGE,
    PERMISSIONS.ROLE_MANAGE,
    PERMISSIONS.DEPARTMENT_MANAGE,
    PERMISSIONS.SYSTEM_CONFIG_MANAGE,
    PERMISSIONS.AUDIT_LOG_VIEW,
    PERMISSIONS.NOTIFICATION_MANAGE
];

/** Wraps routes that require admin access. Redirects to /dashboard if not admin. */
export default function AdminRoute() {
    const { user, hasPermission } = useAuth();

    if (!user) {
        return <Navigate to="/login" replace />;
    }

    const isSystemAdmin = user.role === "SYSTEM_ADMIN";
    const hasAnyAdminPerm = ADMIN_PERMISSIONS.some(p => hasPermission(p));

    if (!isSystemAdmin && !hasAnyAdminPerm) {
        return <Navigate to="/dashboard" replace />;
    }

    return <Outlet />;
}
