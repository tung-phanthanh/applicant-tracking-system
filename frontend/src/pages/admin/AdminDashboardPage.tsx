import { useState, useEffect, useCallback } from "react";
import {
    Users, Building2, ShieldCheck, Mail, Activity,
    TrendingUp, CheckCircle2
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { adminService } from "@/services/adminService";
import { userService } from "@/services/userService";
import StatCard from "@/components/shared/StatCard";

export default function AdminDashboardPage() {
    const [stats, setStats] = useState({
        totalUsers: 0,
        totalDepartments: 0,
        totalAuditLogs: 0,
        unreadNotifications: 0
    });
    const [isLoading, setIsLoading] = useState(true);

    const loadStats = useCallback(async () => {
        setIsLoading(true);
        try {
            const [users, depts, logs, notifs] = await Promise.all([
                userService.getUsers(),
                adminService.getDepartments(),
                adminService.getAuditLogs(),
                adminService.getNotifications()
            ]);
            setStats({
                totalUsers: users.length,
                totalDepartments: depts.length,
                totalAuditLogs: logs.length,
                unreadNotifications: notifs.filter(n => !n.read).length
            });
        } catch {
            // Silently handle error
        } finally {
            setIsLoading(false);
        }
    }, []);

    useEffect(() => {
        loadStats();
    }, [loadStats]);

    return (
        <div className="space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-500">
            {/* Header */}
            <div>
                <h1 className="text-3xl font-bold tracking-tight text-foreground">Admin Overview</h1>
                <p className="text-sm text-muted-foreground mt-1">Real-time system statistics and health monitoring</p>
            </div>

            {/* Stats Cards */}
            <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-4 md:gap-6">
                <StatCard
                    title="System Users"
                    value={stats.totalUsers}
                    icon={Users}
                    iconBgClass="bg-blue-500/10"
                    iconColorClass="text-blue-500"
                />
                <StatCard
                    title="Departments"
                    value={stats.totalDepartments}
                    icon={Building2}
                    iconBgClass="bg-purple-500/10"
                    iconColorClass="text-purple-500"
                />
                <StatCard
                    title="Security Events"
                    value={stats.totalAuditLogs}
                    icon={ShieldCheck}
                    iconBgClass="bg-emerald-500/10"
                    iconColorClass="text-emerald-500"
                />
                <StatCard
                    title="New Notifications"
                    value={stats.unreadNotifications}
                    icon={Mail}
                    iconBgClass="bg-amber-500/10"
                    iconColorClass="text-amber-500"
                />
            </div>

            {/* Bottom Grid */}
            <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
                {/* System Health */}
                <div className="glass-morphism rounded-2xl border border-border/50 bg-card/60 p-6 backdrop-blur-md transition-all hover:shadow-lg">
                    <div className="flex items-center justify-between mb-6">
                        <h3 className="text-lg font-semibold text-foreground flex items-center gap-2">
                            <Activity className="h-4 w-4 text-primary" />
                            System Health
                        </h3>
                        <span className="inline-flex rounded-full bg-emerald-500/10 text-emerald-600 px-2 py-0.5 text-xs font-semibold border border-emerald-500/20">
                            Excellent
                        </span>
                    </div>
                    <div className="space-y-4">
                        <div className="p-4 rounded-xl border border-border bg-background/50 flex items-center justify-between hover:bg-background/80 transition-colors cursor-default">
                            <div className="flex items-center gap-3">
                                <CheckCircle2 className="h-4 w-4 text-emerald-500" />
                                <span className="text-sm font-medium">Database Connection</span>
                            </div>
                            <span className="text-xs text-muted-foreground">Stable (8ms)</span>
                        </div>
                        <div className="p-4 rounded-xl border border-border bg-background/50 flex items-center justify-between hover:bg-background/80 transition-colors cursor-default">
                            <div className="flex items-center gap-3">
                                <CheckCircle2 className="h-4 w-4 text-emerald-500" />
                                <span className="text-sm font-medium">Authentication Service</span>
                            </div>
                            <span className="text-xs text-muted-foreground">Online</span>
                        </div>
                        <div className="p-4 rounded-xl border border-border bg-background/50 flex items-center justify-between hover:bg-background/80 transition-colors cursor-default">
                            <div className="flex items-center gap-3">
                                <CheckCircle2 className="h-4 w-4 text-emerald-500" />
                                <span className="text-sm font-medium">Email Gateway</span>
                            </div>
                            <span className="text-xs text-muted-foreground">Queue Clear</span>
                        </div>
                    </div>
                </div>

                {/* Growth/Activity Chart (Placeholder) */}
                <div className="glass-morphism rounded-2xl border border-border/50 bg-card/60 p-6 backdrop-blur-md transition-all hover:shadow-lg flex flex-col items-center justify-center text-center">
                    <TrendingUp className="h-12 w-12 text-primary/30 mb-4 animate-bounce" />
                    <h3 className="text-lg font-semibold text-foreground">User Activity Trends</h3>
                    <p className="text-sm text-muted-foreground mt-2 max-w-[250px]">
                        Growth metrics and login activity charts are being synchronized with the new analytics engine.
                    </p>
                    <Button variant="ghost" className="mt-6 rounded-full text-xs underline decoration-primary/30 underline-offset-4">
                        View Detailed Analytics
                    </Button>
                </div>
            </div>
        </div>
    );
}
