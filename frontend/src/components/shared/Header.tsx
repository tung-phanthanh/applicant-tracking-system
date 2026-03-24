import { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import api from "@/lib/api";
import { LogOut, User, Bell } from "lucide-react";
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
    "/candidates": "Candidates",
    "/interviews": "Interviews",
    "/profile": "User Profile",
};

export default function Header() {
    const { user, logout } = useAuth();
    const location = useLocation();
    const navigate = useNavigate();
    const [open, setOpen] = useState(false);
    const [unreadCount, setUnreadCount] = useState(0);

    useEffect(() => {
        if (!user) return;
        api.get<number>("/notifications/unread-count")
            .then((res) => setUnreadCount(res.data))
            .catch(() => {});
    }, [user]);

    const pageTitle = PAGE_TITLES[location.pathname] ?? "Enterprise ATS";
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
                    <div className="flex h-8 w-8 items-center justify-center rounded-full bg-muted text-sm font-bold text-muted-foreground mr-2 cursor-pointer hover:bg-accent relative">
                        <Bell className="h-4 w-4" />
                        {unreadCount > 0 && (
                            <span className="absolute top-0 right-0 flex h-3 w-3 rounded-full bg-destructive text-[8px] text-destructive-foreground items-center justify-center font-bold">
                                {unreadCount > 9 ? "9+" : unreadCount}
                            </span>
                        )}
                    </div>
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
