import { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { LogOut, User } from "lucide-react";
import { Button } from "@/components/ui/button";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuLabel,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { useAuth } from "@/hooks/useAuth";

const PAGE_TITLES: Record<string, string> = {
    "/dashboard": "Dashboard",
    "/jobs": "Jobs",
    "/jobs/create": "Create New Job",
    "/jobs/pending-approvals": "Pending Approvals",
    "/candidates": "Candidates",
    "/interviews": "Interviews",
    "/profile": "User Profile",
};

function resolvePageTitle(pathname: string): string {
    if (PAGE_TITLES[pathname]) {
        return PAGE_TITLES[pathname];
    }
    if (/^\/jobs\/[^/]+\/edit$/.test(pathname)) {
        return "Edit Job";
    }
    if (/^\/jobs\/[^/]+$/.test(pathname)) {
        return "Job Details";
    }
    return "Enterprise ATS";
}

export default function Header() {
    const { user, logout } = useAuth();
    const location = useLocation();
    const navigate = useNavigate();
    const [open, setOpen] = useState(false);

    const pageTitle = resolvePageTitle(location.pathname);
    const displayName =
        user?.fullName?.trim() || user?.email?.split("@")[0] || "User";
    const initials = displayName
        .split(" ")
        .filter(Boolean)
        .map((n) => n[0])
        .slice(0, 2)
        .join("")
        .toUpperCase();

    const handleLogout = async () => {
        await logout();
        navigate("/login");
    };

    return (
        <header className="flex h-16 shrink-0 items-center justify-between border-b border-border bg-background px-6">
            <h1 className="text-xl font-semibold text-foreground">{pageTitle}</h1>

            {user && (
                <DropdownMenu open={open} onOpenChange={setOpen}>
                    <DropdownMenuTrigger asChild>
                        <Button
                            variant="ghost"
                            className="flex items-center gap-2 rounded-full p-1 pr-3 hover:bg-accent"
                        >
                            <div className="flex h-8 w-8 items-center justify-center rounded-full bg-muted text-sm font-bold text-muted-foreground">
                                {initials}
                            </div>
                            <span className="text-sm font-medium text-foreground">
                                {displayName}
                            </span>
                        </Button>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent align="end" className="w-48">
                        <DropdownMenuLabel className="text-xs text-muted-foreground">
                            {user.email}
                        </DropdownMenuLabel>
                        <DropdownMenuSeparator />
                        <DropdownMenuItem
                            onClick={() => {
                                setOpen(false);
                                navigate("/profile");
                            }}
                        >
                            <User className="mr-2 h-4 w-4" />
                            Profile
                        </DropdownMenuItem>
                        <DropdownMenuSeparator />
                        <DropdownMenuItem
                            onClick={handleLogout}
                            className="text-destructive focus:text-destructive"
                        >
                            <LogOut className="mr-2 h-4 w-4" />
                            Logout
                        </DropdownMenuItem>
                    </DropdownMenuContent>
                </DropdownMenu>
            )}
        </header>
    );
}
