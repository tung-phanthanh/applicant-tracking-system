import { useEffect, useState } from "react";
import { 
    Briefcase, Users, Calendar, Award, AlertCircle 
} from "lucide-react";
import StatCard from "@/components/shared/StatCard";
import { dashboardService, type DashboardStats } from "@/services/api/dashboard.service";

export default function AdminDashboardPage() {
    const [stats, setStats] = useState<DashboardStats | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchStats = async () => {
            try {
                const data = await dashboardService.getStats();
                setStats(data);
                setError(null);
            } catch (err: unknown) {
                console.error("Failed to fetch dashboard stats", err);
                setError("Failed to load dashboard data. Please try again.");
            } finally {
                setLoading(false);
            }
        };

        fetchStats();
    }, []);

    if (loading) {
        return (
            <div className="flex h-64 items-center justify-center">
                <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent"></div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="rounded-md border border-destructive/50 bg-destructive/10 p-4 text-destructive flex items-center gap-3">
                <AlertCircle className="h-5 w-5" />
                <div>
                    <h3 className="font-medium">Error</h3>
                    <p className="text-sm">{error}</p>
                </div>
            </div>
        );
    }

    if (!stats) return null;

    return (
        <div className="space-y-8">
            <div>
                <h1 className="text-2xl font-bold tracking-tight text-foreground">Admin System Dashboard</h1>
                <p className="text-muted-foreground mt-2">Overview of recruitment activity and system status.</p>
            </div>
            
            <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-4">
                <StatCard
                    title="Active Jobs"
                    value={stats.activeJobs || 0}
                    icon={Briefcase}
                    iconBgClass="bg-blue-100 dark:bg-blue-900/30"
                    iconColorClass="text-blue-600 dark:text-blue-400"
                />
                <StatCard
                    title="New Candidates"
                    value={stats.newCandidates || 0}
                    icon={Users}
                    iconBgClass="bg-green-100 dark:bg-green-900/30"
                    iconColorClass="text-green-600 dark:text-green-400"
                />
                <StatCard
                    title="Interviews Today"
                    value={stats.interviewsToday || 0}
                    icon={Calendar}
                    iconBgClass="bg-purple-100 dark:bg-purple-900/30"
                    iconColorClass="text-purple-600 dark:text-purple-400"
                />
                <StatCard
                    title="Offers Sent"
                    value={stats.offersSent || 0}
                    icon={Award}
                    iconBgClass="bg-orange-100 dark:bg-orange-900/30"
                    iconColorClass="text-orange-600 dark:text-orange-400"
                />
            </div>

            <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
                <div className="rounded-lg border border-border bg-card p-6 shadow-sm">
                    <h3 className="mb-4 text-lg font-semibold text-card-foreground">Hiring Pipeline</h3>
                    {stats.hiringPipeline && Object.keys(stats.hiringPipeline).length > 0 ? (
                        <div className="space-y-4">
                            {Object.entries(stats.hiringPipeline).map(([stage, count]) => (
                                <div key={stage} className="flex justify-between items-center pb-2 border-b border-border last:border-0">
                                    <span className="text-sm text-foreground capitalize">{stage.replace(/_/g, ' ')}</span>
                                    <span className="font-medium text-foreground">{count}</span>
                                </div>
                            ))}
                        </div>
                    ) : (
                        <div className="flex h-32 items-center justify-center text-muted-foreground">
                            <p className="text-sm">No pipeline data available</p>
                        </div>
                    )}
                </div>

                <div className="rounded-lg border border-border bg-card p-6 shadow-sm">
                    <h3 className="mb-4 text-lg font-semibold text-card-foreground">System Overview</h3>
                    <div className="flex h-32 items-center justify-center text-muted-foreground border border-dashed border-border rounded-md">
                        <div className="text-center">
                            <p className="text-sm">More system metrics coming soon</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
