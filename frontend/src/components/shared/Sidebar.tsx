import { NavLink } from "react-router-dom";
import {
    Briefcase,
    Calendar,
    LayoutDashboard,
    User,
    Users,
} from "lucide-react";
import { cn } from "@/lib/utils";

const navItems = [
    { to: "/dashboard", icon: LayoutDashboard, label: "Dashboard" },
    { to: "/jobs", icon: Briefcase, label: "Jobs" },
    { to: "/candidates", icon: Users, label: "Candidates" },
    { to: "/interviews", icon: Calendar, label: "Interviews" },
    { to: "/profile", icon: User, label: "My Profile" },
];

export default function Sidebar() {
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
