import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { Switch } from "@/components/ui/switch";
import { useAuth } from "@/hooks/useAuth";
import { Lock, KeyRound } from "lucide-react";

export default function ProfilePage() {
    const { user } = useAuth();
    const navigate = useNavigate();

    const [emailNotifications, setEmailNotifications] = useState(true);
    const [darkMode, setDarkMode] = useState(false);

    if (!user) return null;

    return (
        <div className="mx-auto max-w-4xl space-y-6">
            {/* Profile Header */}
            <div className="overflow-hidden rounded-lg border border-border bg-card shadow-sm">
                <div className="h-32 bg-gradient-to-r from-slate-700 to-slate-900" />
                <div className="px-6 pb-6">
                    <div className="relative -mt-12 mb-4 flex items-end">
                        <div className="flex h-24 w-24 items-center justify-center rounded-full bg-muted text-2xl font-bold text-muted-foreground ring-4 ring-card">
                            {user.fullName
                                .split(" ")
                                .map((n) => n[0])
                                .slice(0, 2)
                                .join("")}
                        </div>
                        <div className="mb-1 ml-4">
                            <h2 className="text-2xl font-bold text-card-foreground">
                                {user.fullName}
                            </h2>
                            <p className="text-sm text-muted-foreground">
                                {user.role.replace(/_/g, " ")} {user.department ? `• ${user.department}` : ""}
                            </p>
                        </div>
                    </div>
                </div>
            </div>

            {/* Content Grid */}
            <div className="grid grid-cols-1 gap-6 md:grid-cols-3">
                {/* Left Column */}
                <div className="space-y-6 md:col-span-2">
                    {/* Personal Information */}
                    <div className="rounded-lg border border-border bg-card p-6 shadow-sm">
                        <h3 className="mb-4 text-lg font-medium text-card-foreground">
                            Personal Information
                        </h3>
                        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                            <div className="space-y-1.5">
                                <Label className="text-sm font-medium text-muted-foreground">Full Name</Label>
                                <div className="rounded-md border border-border bg-muted/50 px-3 py-2 text-sm text-muted-foreground">
                                    {user.fullName}
                                </div>
                            </div>
                            <div className="space-y-1.5">
                                <Label className="text-sm font-medium text-muted-foreground">Email Address</Label>
                                <div className="rounded-md border border-border bg-muted/50 px-3 py-2 text-sm text-muted-foreground">
                                    {user.email}
                                </div>
                            </div>
                            <div className="space-y-1.5">
                                <Label className="text-sm font-medium text-muted-foreground">Department</Label>
                                <div className="rounded-md border border-border bg-muted/50 px-3 py-2 text-sm text-muted-foreground">
                                    {user.department || "—"}
                                </div>
                            </div>
                            <div className="space-y-1.5">
                                <Label className="text-sm font-medium text-muted-foreground">Role</Label>
                                <div className="rounded-md border border-border bg-muted/50 px-3 py-2 text-sm capitalize text-muted-foreground">
                                    {user.role.replace(/_/g, " ")}
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* Security */}
                    <div className="rounded-lg border border-border bg-card p-6 shadow-sm">
                        <h3 className="mb-4 text-lg font-medium text-card-foreground">
                            Security
                        </h3>
                        <div className="flex items-center justify-between rounded-lg border border-border bg-muted/30 px-4 py-3">
                            <div className="flex items-center gap-3">
                                <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-primary/10 text-primary">
                                    <Lock className="h-4 w-4" />
                                </div>
                                <div>
                                    <p className="text-sm font-medium text-foreground">Password</p>
                                    <p className="text-xs text-muted-foreground">Update your account password</p>
                                </div>
                            </div>
                            <Button
                                variant="outline"
                                size="sm"
                                onClick={() => navigate("/change-password")}
                            >
                                <KeyRound className="mr-1.5 h-3.5 w-3.5" />
                                Change Password
                            </Button>
                        </div>
                    </div>
                </div>

                {/* Right Column */}
                <div className="space-y-6">
                    {/* Activity Overview */}
                    <div className="rounded-lg border border-border bg-card p-6 shadow-sm">
                        <h3 className="mb-4 text-xs font-medium uppercase tracking-wider text-muted-foreground">
                            Activity Overview
                        </h3>
                        <ul className="space-y-4">
                            {[
                                { label: "Jobs Created", value: 12 },
                                { label: "Interviews Conducted", value: 45 },
                                { label: "Candidates Hired", value: 8 },
                            ].map(({ label, value }) => (
                                <li key={label} className="flex items-center justify-between">
                                    <span className="text-sm text-muted-foreground">{label}</span>
                                    <span className="text-sm font-bold text-foreground">{value}</span>
                                </li>
                            ))}
                        </ul>
                    </div>

                    {/* Preferences */}
                    <div className="rounded-lg border border-border bg-card p-6 shadow-sm">
                        <h3 className="mb-4 text-xs font-medium uppercase tracking-wider text-muted-foreground">
                            Preferences
                        </h3>
                        <div className="space-y-4">
                            <div className="flex items-center justify-between">
                                <Label htmlFor="email-notifications" className="cursor-pointer text-sm text-foreground">
                                    Email Notifications
                                </Label>
                                <Switch
                                    id="email-notifications"
                                    checked={emailNotifications}
                                    onCheckedChange={setEmailNotifications}
                                />
                            </div>
                            <div className="flex items-center justify-between">
                                <Label htmlFor="dark-mode" className="cursor-pointer text-sm text-foreground">
                                    Dark Mode
                                </Label>
                                <Switch
                                    id="dark-mode"
                                    checked={darkMode}
                                    onCheckedChange={setDarkMode}
                                />
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
