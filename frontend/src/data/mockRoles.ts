import type { Permission, Role } from "@/types/index";

export const ALL_PERMISSIONS: Permission[] = [
    // Recruitment
    { key: "view_jobs", label: "View Jobs", description: "View job postings", category: "Recruitment" },
    { key: "manage_jobs", label: "Manage Jobs", description: "Create, edit, delete job postings", category: "Recruitment" },
    { key: "view_candidates", label: "View Candidates", description: "View candidate profiles", category: "Recruitment" },
    { key: "manage_candidates", label: "Manage Candidates", description: "Edit candidate info and pipeline status", category: "Recruitment" },
    { key: "view_interviews", label: "View Interviews", description: "View scheduled interviews", category: "Recruitment" },
    { key: "manage_interviews", label: "Manage Interviews", description: "Schedule and manage interviews", category: "Recruitment" },
    // Administration
    { key: "manage_users", label: "Manage Users", description: "Add, edit, deactivate users", category: "Administration" },
    { key: "manage_roles", label: "Manage Roles", description: "Create and edit roles & permissions", category: "Administration" },
    { key: "manage_departments", label: "Manage Departments", description: "Create and manage departments", category: "Administration" },
    { key: "view_audit_log", label: "View Audit Log", description: "Access system audit trail", category: "Administration" },
    { key: "manage_system_config", label: "System Config", description: "Change system-wide settings", category: "Administration" },
    // Reports
    { key: "view_reports", label: "View Reports", description: "Access recruitment analytics", category: "Reports" },
];

export const MOCK_ROLES: Role[] = [
    {
        id: "role-1",
        name: "Administrator",
        description: "Full access to all system features and configuration",
        color: "bg-destructive",
        permissions: [
            "view_jobs", "manage_jobs",
            "view_candidates", "manage_candidates",
            "view_interviews", "manage_interviews",
            "view_reports",
            "manage_users", "manage_roles", "manage_departments",
            "view_audit_log", "manage_system_config",
        ],
        userCount: 3,
        isSystem: true,
        createdAt: "2024-01-01T00:00:00Z",
    },
    {
        id: "role-2",
        name: "Recruiter",
        description: "Manages the full recruitment pipeline",
        color: "bg-blue-500",
        permissions: [
            "view_jobs", "manage_jobs",
            "view_candidates", "manage_candidates",
            "view_interviews", "manage_interviews",
            "view_reports",
        ],
        userCount: 12,
        isSystem: true,
        createdAt: "2024-01-01T00:00:00Z",
    },
    {
        id: "role-3",
        name: "Hiring Manager",
        description: "Reviews candidates and participates in interviews",
        color: "bg-purple-500",
        permissions: [
            "view_jobs",
            "view_candidates", "manage_candidates",
            "view_interviews", "manage_interviews",
            "view_reports",
        ],
        userCount: 8,
        isSystem: false,
        createdAt: "2024-02-15T10:00:00Z",
    },
    {
        id: "role-4",
        name: "Viewer",
        description: "Read-only access to jobs and candidates",
        color: "bg-muted-foreground",
        permissions: ["view_jobs", "view_candidates", "view_interviews"],
        userCount: 5,
        isSystem: false,
        createdAt: "2024-03-10T09:00:00Z",
    },
];
