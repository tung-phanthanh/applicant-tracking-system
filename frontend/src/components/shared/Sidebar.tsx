import { NavLink } from "react-router-dom";
import {
    Briefcase,
    Calendar,
    LayoutDashboard,
    User,
    Users,
    Shield,
    Building2,
    Settings,
    History,
    Bell,
} from "lucide-react";
import { cn } from "@/lib/utils";
import { useAuth } from "@/hooks/useAuth";

const recruiterItems = [
    { to: "/dashboard", icon: LayoutDashboard, label: "Dashboard" },
    { to: "/jobs", icon: Briefcase, label: "Jobs" },
    { to: "/candidates", icon: Users, label: "Candidates" },
    { to: "/interviews", icon: Calendar, label: "Interviews" },
];

const adminItems = [
    { to: "/admin/dashboard", icon: LayoutDashboard, label: "Admin Dashboard" },
    { to: "/admin/roles", icon: Shield, label: "Roles & Permissions" },
    { to: "/admin/departments", icon: Building2, label: "Departments" },
    { to: "/admin/system-config", icon: Settings, label: "System Config" },
    { to: "/admin/audit-logs", icon: History, label: "Audit Logs" },
];

const commonItems = [
    { to: "/notifications", icon: Bell, label: "Notifications" },
    { to: "/profile", icon: User, label: "My Profile" },
];

export default function Sidebar() {
    const { user } = useAuth();
    
    // Determine which items to show based on user role
    const isSystemAdmin = user?.role === "admin";
    const primaryNavItems = isSystemAdmin ? adminItems : recruiterItems;

    return (
        <aside className="flex h-screen w-64 flex-col border-r border-border bg-sidebar">
            {/* Logo */}
            <div className="flex h-16 shrink-0 items-center gap-2 border-b border-sidebar-border px-6">
                <div className="flex h-8 w-8 items-center justify-center rounded-full bg-primary text-primary-foreground">
                    <Briefcase className="h-4 w-4" />
                </div>
                <span className="text-lg font-bold text-sidebar-foreground">
                    Enterprise ATS
                </span>
            </div>

            {/* Navigation */}
            <nav className="flex-1 overflow-y-auto px-3 py-4 space-y-6">
                <ul className="space-y-1">
                    {primaryNavItems.map(({ to, icon: Icon, label }) => (
                        <li key={to}>
                            <NavLink
                                to={to}
                                className={({ isActive }) =>
                                    cn(
                                        "flex items-center gap-3 rounded-md px-3 py-2 text-sm font-medium transition-colors",
                                        isActive
                                            ? "bg-sidebar-primary text-sidebar-primary-foreground"
                                            : "text-sidebar-foreground hover:bg-sidebar-accent hover:text-sidebar-accent-foreground",
                                    )
                                }
                            >
                                <Icon className="h-4 w-4 shrink-0" />
                                {label}
                            </NavLink>
                        </li>
                    ))}
                </ul>

                <div>
                    <h4 className="mb-2 px-3 text-xs font-semibold uppercase tracking-wider text-sidebar-foreground/50">
                        Account
                    </h4>
                    <ul className="space-y-1">
                        {commonItems.map(({ to, icon: Icon, label }) => (
                            <li key={to}>
                                <NavLink
                                    to={to}
                                    className={({ isActive }) =>
                                        cn(
                                            "flex items-center gap-3 rounded-md px-3 py-2 text-sm font-medium transition-colors",
                                            isActive
                                                ? "bg-sidebar-primary text-sidebar-primary-foreground"
                                                : "text-sidebar-foreground hover:bg-sidebar-accent hover:text-sidebar-accent-foreground",
                                        )
                                    }
                                >
                                    <Icon className="h-4 w-4 shrink-0" />
                                    {label}
                                </NavLink>
                            </li>
                        ))}
                    </ul>
                </div>
            </nav>

            {/* Footer */}
            <div className="shrink-0 border-t border-sidebar-border p-4">
                <p className="text-center text-xs text-muted-foreground">
                    © 2025 Enterprise ATS
                </p>
            </div>
        </aside>
    );
}
