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
import { NotificationBell } from "@/components/shared/NotificationBell";

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

    const pageTitle = PAGE_TITLES[location.pathname] ?? "Enterprise ATS";

    const handleLogout = async () => {
        await logout();
        navigate("/login");
    };

    return (
        <header className="flex h-16 shrink-0 items-center justify-between border-b border-border bg-background px-6">
            <h1 className="text-xl font-semibold text-foreground">{pageTitle}</h1>

            {user && (
                <div className="flex items-center gap-4">
                    <NotificationBell />
                    <DropdownMenu open={open} onOpenChange={setOpen}>
                        <DropdownMenuTrigger asChild>
                            <Button
                                variant="ghost"
                                className="flex items-center gap-2 rounded-full p-1 pr-3 hover:bg-accent"
                            >
                                <div className="flex h-8 w-8 items-center justify-center rounded-full bg-muted text-sm font-bold text-muted-foreground">
                                    {user.initials}
                                </div>
                                <span className="text-sm font-medium text-foreground">
                                    {user.name}
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
                </div>
                <DropdownMenu open={open} onOpenChange={setOpen}>
                    <DropdownMenuTrigger asChild>
                        <Button
                            variant="ghost"
                            className="flex items-center gap-2 rounded-full p-1 pr-3 hover:bg-accent"
                        >
                            <div className="flex h-8 w-8 items-center justify-center rounded-full bg-muted text-sm font-bold text-muted-foreground">
                                {user.fullName
                                    .split(" ")
                                    .map((n) => n[0])
                                    .slice(0, 2)
                                    .join("")}
                            </div>
                            <span className="text-sm font-medium text-foreground">
                                {user.fullName}
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
