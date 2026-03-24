import { NavLink } from "react-router-dom";
import {
    Briefcase,
    Calendar,
    LayoutDashboard,
    User,
    Users,
    ShieldCheck,
} from "lucide-react";
import { cn } from "@/lib/utils";
import { useAuth } from "@/hooks/useAuth";

const navItems = [
    { to: "/dashboard", icon: LayoutDashboard, label: "Dashboard" },
    { to: "/jobs", icon: Briefcase, label: "Jobs" },
    { to: "/interviews", icon: Calendar, label: "Interviews" },
    { to: "/profile", icon: User, label: "My Profile" },
];

const adminNavItems = [
    { to: "/admin/users", icon: ShieldCheck, label: "Manage Users" },
];

export default function Sidebar() {
    const { user } = useAuth();
    const isAdmin = user?.role === "SYSTEM_ADMIN";
    const isHr = user?.role === "HR";

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
            <nav className="flex-1 overflow-y-auto px-3 py-4">
                <ul className="space-y-1">
                    {navItems.map(({ to, icon: Icon, label }) => (
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

                    {isHr && (
                        <li>
                            <NavLink
                                to="/candidates"
                                className={({ isActive }) =>
                                    cn(
                                        "flex items-center gap-3 rounded-md px-3 py-2 text-sm font-medium transition-colors",
                                        isActive
                                            ? "bg-sidebar-primary text-sidebar-primary-foreground"
                                            : "text-sidebar-foreground hover:bg-sidebar-accent hover:text-sidebar-accent-foreground",
                                    )
                                }
                            >
                                <Users className="h-4 w-4 shrink-0" />
                                Candidates
                            </NavLink>
                        </li>
                    )}

                    {/* Admin section */}
                    {isAdmin && (
                        <>
                            <li className="pt-3">
                                <p className="mb-1 px-3 text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                                    Administration
                                </p>
                            </li>
                            {adminNavItems.map(({ to, icon: Icon, label }) => (
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
                        </>
                    )}
                </ul>
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
