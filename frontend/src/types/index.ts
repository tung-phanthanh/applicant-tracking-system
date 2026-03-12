// ─────────────────────────────────────────────
// Re-export auth types for convenience
// ─────────────────────────────────────────────
export type { User, UserRole, LoginCredentials, AuthContextType } from "@/types/auth";

// ─────────────────────────────────────────────
// Role & Permission
// ─────────────────────────────────────────────
export type PermissionKey =
    | "view_jobs"
    | "manage_jobs"
    | "view_candidates"
    | "manage_candidates"
    | "view_interviews"
    | "manage_interviews"
    | "view_reports"
    | "manage_users"
    | "manage_roles"
    | "manage_departments"
    | "view_audit_log"
    | "manage_system_config";

export interface Permission {
    key: PermissionKey;
    label: string;
    description: string;
    category: "Recruitment" | "Administration" | "Reports";
}

export interface Role {
    id: string;
    name: string;
    description: string;
    color: string;
    permissions: PermissionKey[];
    userCount: number;
    isSystem: boolean; // system roles cannot be deleted
    createdAt: string;
}

// ─────────────────────────────────────────────
// Department
// ─────────────────────────────────────────────
export type DepartmentStatus = "active" | "inactive";

export interface Department {
    id: string;
    name: string;
    description: string;
    head: string;
    employeeCount: number;
    openPositions: number;
    status: DepartmentStatus;
    createdAt: string;
}

// ─────────────────────────────────────────────
// Audit Log
// ─────────────────────────────────────────────
export type AuditAction = "CREATE" | "UPDATE" | "DELETE" | "LOGIN" | "LOGOUT" | "EXPORT";

export interface AuditLog {
    id: string;
    timestamp: string;
    actor: string;          // user name
    actorEmail: string;
    action: AuditAction;
    resource: string;       // e.g. "Job Posting", "Candidate"
    resourceId: string;
    detail: string;         // human-readable description
    ipAddress: string;
}

// ─────────────────────────────────────────────
// Notification
// ─────────────────────────────────────────────
export type NotificationType =
    | "application"
    | "interview"
    | "offer"
    | "system"
    | "alert";

export interface Notification {
    id: string;
    type: NotificationType;
    title: string;
    message: string;
    read: boolean;
    createdAt: string;
    link?: string;
}

// ─────────────────────────────────────────────
// System Config
// ─────────────────────────────────────────────
export interface SystemConfigItem {
    key: string;
    label: string;
    description: string;
    value: string | boolean | number;
    type: "text" | "toggle" | "number" | "select";
    options?: string[]; // for select type
    group: "General" | "Email" | "Security" | "Integrations";
}
