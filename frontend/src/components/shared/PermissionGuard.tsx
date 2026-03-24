import { ReactNode } from "react";
import { useAuth } from "@/hooks/useAuth";

interface PermissionGuardProps {
    permission: string;
    children: ReactNode;
    fallback?: ReactNode;
}

export function PermissionGuard({
    permission,
    children,
    fallback = null,
}: PermissionGuardProps) {
    const { hasPermission } = useAuth();

    if (!hasPermission(permission)) {
        return <>{fallback}</>;
    }

    return <>{children}</>;
}
