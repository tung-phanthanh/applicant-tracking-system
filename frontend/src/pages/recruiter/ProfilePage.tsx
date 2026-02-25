import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Switch } from "@/components/ui/switch";
import { useAuth } from "@/hooks/useAuth";

export default function ProfilePage() {
    const { user } = useAuth();

    const [emailNotifications, setEmailNotifications] = useState(true);
    const [darkMode, setDarkMode] = useState(false);

    const [currentPassword, setCurrentPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");

    const handlePasswordUpdate = (e: React.FormEvent) => {
        e.preventDefault();
        // Password update logic will be wired to backend later
        console.log("Password update submitted");
    };

    if (!user) return null;

    return (
        <div className="mx-auto max-w-4xl space-y-6">
            {/* Profile Header */}
            <div className="overflow-hidden rounded-lg border border-border bg-card shadow-sm">
                <div className="h-32 bg-gradient-to-r from-slate-700 to-slate-900" />
                <div className="px-6 pb-6">
                    <div className="relative -mt-12 mb-4 flex items-end">
                        <div className="flex h-24 w-24 items-center justify-center rounded-full bg-muted text-2xl font-bold text-muted-foreground ring-4 ring-card">
                            {user.initials}
                        </div>
                        <div className="mb-1 ml-4">
                            <h2 className="text-2xl font-bold text-card-foreground">
                                {user.name}
                            </h2>
                            <p className="text-sm text-muted-foreground">
                                Recruitment Manager &bull; {user.department}
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
                                <Label className="text-sm font-medium text-muted-foreground">
                                    Full Name
                                </Label>
                                <div className="rounded-md border border-border bg-muted/50 px-3 py-2 text-sm text-muted-foreground">
                                    {user.name}
                                </div>
                            </div>
                            <div className="space-y-1.5">
                                <Label className="text-sm font-medium text-muted-foreground">
                                    Email Address
                                </Label>
                                <div className="rounded-md border border-border bg-muted/50 px-3 py-2 text-sm text-muted-foreground">
                                    {user.email}
                                </div>
                            </div>
                            <div className="space-y-1.5">
                                <Label className="text-sm font-medium text-muted-foreground">
                                    Phone Number
                                </Label>
                                <div className="rounded-md border border-border bg-muted/50 px-3 py-2 text-sm text-muted-foreground">
                                    {user.phone}
                                </div>
                            </div>
                            <div className="space-y-1.5">
                                <Label className="text-sm font-medium text-muted-foreground">
                                    Role
                                </Label>
                                <div className="rounded-md border border-border bg-muted/50 px-3 py-2 text-sm capitalize text-muted-foreground">
                                    {user.role}
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* Security */}
                    <div className="rounded-lg border border-border bg-card p-6 shadow-sm">
                        <h3 className="mb-4 text-lg font-medium text-card-foreground">
                            Security
                        </h3>
                        <form onSubmit={handlePasswordUpdate} className="space-y-4">
                            <div className="space-y-1.5">
                                <Label htmlFor="current-password">Current Password</Label>
                                <Input
                                    id="current-password"
                                    type="password"
                                    value={currentPassword}
                                    onChange={(e) => setCurrentPassword(e.target.value)}
                                    placeholder="Enter current password"
                                />
                            </div>
                            <div className="space-y-1.5">
                                <Label htmlFor="new-password">New Password</Label>
                                <Input
                                    id="new-password"
                                    type="password"
                                    value={newPassword}
                                    onChange={(e) => setNewPassword(e.target.value)}
                                    placeholder="Enter new password"
                                />
                                <p className="text-xs text-muted-foreground">
                                    Must be at least 8 characters.
                                </p>
                            </div>
                            <div className="space-y-1.5">
                                <Label htmlFor="confirm-password">Confirm Password</Label>
                                <Input
                                    id="confirm-password"
                                    type="password"
                                    value={confirmPassword}
                                    onChange={(e) => setConfirmPassword(e.target.value)}
                                    placeholder="Confirm new password"
                                />
                            </div>
                            <div className="pt-2">
                                <Button type="submit">Update Password</Button>
                            </div>
                        </form>
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
                                    <span className="text-sm font-bold text-foreground">
                                        {value}
                                    </span>
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
                                <Label
                                    htmlFor="email-notifications"
                                    className="cursor-pointer text-sm text-foreground"
                                >
                                    Email Notifications
                                </Label>
                                <Switch
                                    id="email-notifications"
                                    checked={emailNotifications}
                                    onCheckedChange={setEmailNotifications}
                                />
                            </div>
                            <div className="flex items-center justify-between">
                                <Label
                                    htmlFor="dark-mode"
                                    className="cursor-pointer text-sm text-foreground"
                                >
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
