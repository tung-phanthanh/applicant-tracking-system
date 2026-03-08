import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import {
    Briefcase,
    Users,
    CalendarDays,
    CheckCircle2,
    TrendingUp,
    ArrowUpRight,
    Clock,
    MoreVertical,
} from "lucide-react";
import { PageHeader } from "@/components/shared/PageHeader";
import { StatusBadge } from "@/components/shared/StatusBadge";
import { Button } from "@/components/ui/button";
import { CardSkeleton } from "@/components/shared/LoadingSkeleton";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { jobService } from "@/services/jobService";
import { candidateService } from "@/services/candidateService";
import { applicationService } from "@/services/applicationService";
import { interviewService } from "@/services/interviewService";
import type { Application, InterviewResponse } from "@/types/models";
import { toast } from "sonner";

export default function DashboardPage() {
    const [activeJobs, setActiveJobs] = useState(0);
    const [totalCandidates, setTotalCandidates] = useState(0);
    const [recentApps, setRecentApps] = useState<Application[]>([]);
    const [upcomingInterviews, setUpcomingInterviews] = useState<InterviewResponse[]>([]);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        loadDashboardData();
    }, []);

    const loadDashboardData = async () => {
        setIsLoading(true);
        try {
            const [jobsCount, candidatesCount, apps, interviews] = await Promise.all([
                jobService.getActiveJobsCount(),
                candidateService.getTotalCount(),
                applicationService.getRecentApplications(5),
                interviewService.getUpcomingInterviews()
            ]);
            setActiveJobs(jobsCount);
            setTotalCandidates(candidatesCount);
            setRecentApps(apps);
            setUpcomingInterviews(interviews);
        } catch (error) {
            console.error("Failed to load dashboard data:", error);
            toast.error("Failed to load some dashboard metrics");
        } finally {
            setIsLoading(false);
        }
    };

    if (isLoading) return <CardSkeleton />;

    return (
        <div className="space-y-6">
            <PageHeader
                title="Welcome back, Sarah 👋"
                description="Here's what's happening with your hiring pipeline today."
            />

            {/* KPI Cards */}
            <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
                {/* Total Active Jobs */}
                <div className="group relative overflow-hidden rounded-xl border border-slate-200 bg-white p-5 shadow-sm transition-all hover:-translate-y-1 hover:shadow-md">
                    <div className="mb-4 flex items-center justify-between">
                        <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-indigo-50 text-indigo-600 transition-colors group-hover:bg-indigo-600 group-hover:text-white">
                            <Briefcase className="h-5 w-5" />
                        </div>
                        <span className="flex items-center gap-1 rounded-full bg-emerald-50 px-2 py-1 text-xs font-semibold text-emerald-700">
                            <ArrowUpRight className="h-3 w-3" /> +2
                        </span>
                    </div>
                    <div>
                        <p className="text-sm font-medium text-slate-500">Active Jobs</p>
                        <p className="mt-1 flex items-baseline gap-2">
                            <span className="text-3xl font-bold text-slate-900">{activeJobs}</span>
                            <span className="text-sm text-slate-400">positions</span>
                        </p>
                    </div>
                    <div className="absolute -bottom-1 h-1 w-full scale-x-0 bg-indigo-600 transition-transform group-hover:scale-x-100 left-0" />
                </div>

                {/* Total Candidates */}
                <div className="group relative overflow-hidden rounded-xl border border-slate-200 bg-white p-5 shadow-sm transition-all hover:-translate-y-1 hover:shadow-md">
                    <div className="mb-4 flex items-center justify-between">
                        <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-sky-50 text-sky-600 transition-colors group-hover:bg-sky-600 group-hover:text-white">
                            <Users className="h-5 w-5" />
                        </div>
                        <span className="flex items-center gap-1 rounded-full bg-emerald-50 px-2 py-1 text-xs font-semibold text-emerald-700">
                            <ArrowUpRight className="h-3 w-3" /> +15%
                        </span>
                    </div>
                    <div>
                        <p className="text-sm font-medium text-slate-500">Total Candidates</p>
                        <p className="mt-1 flex items-baseline gap-2">
                            <span className="text-3xl font-bold text-slate-900">{totalCandidates}</span>
                            <span className="text-sm text-slate-400">in pipeline</span>
                        </p>
                    </div>
                    <div className="absolute -bottom-1 h-1 w-full scale-x-0 bg-sky-600 transition-transform group-hover:scale-x-100 left-0" />
                </div>

                {/* Interviews Today */}
                <div className="group relative overflow-hidden rounded-xl border border-slate-200 bg-white p-5 shadow-sm transition-all hover:-translate-y-1 hover:shadow-md">
                    <div className="mb-4 flex items-center justify-between">
                        <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-amber-50 text-amber-600 transition-colors group-hover:bg-amber-500 group-hover:text-white">
                            <CalendarDays className="h-5 w-5" />
                        </div>
                        <span className="text-xs font-medium text-slate-400">Today</span>
                    </div>
                    <div>
                        <p className="text-sm font-medium text-slate-500">Interviews</p>
                        <p className="mt-1 flex items-baseline gap-2">
                            <span className="text-3xl font-bold text-slate-900">{upcomingInterviews.length}</span>
                            <span className="text-sm text-slate-400">scheduled</span>
                        </p>
                    </div>
                    <div className="absolute -bottom-1 h-1 w-full scale-x-0 bg-amber-500 transition-transform group-hover:scale-x-100 left-0" />
                </div>

                {/* Pending Decisions */}
                <div className="group relative overflow-hidden rounded-xl border border-rose-100 bg-rose-50/50 p-5 shadow-sm transition-all hover:-translate-y-1 hover:shadow-md">
                    <div className="mb-4 flex items-center justify-between">
                        <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-rose-100 text-rose-600 transition-colors group-hover:bg-rose-600 group-hover:text-white">
                            <CheckCircle2 className="h-5 w-5" />
                        </div>
                        <span className="flex h-5 items-center justify-center rounded-full bg-rose-100 px-2 text-xs font-bold text-rose-700">
                            Needs action
                        </span>
                    </div>
                    <div>
                        <p className="text-sm font-medium text-slate-500">Pending Offers</p>
                        <p className="mt-1 flex items-baseline gap-2">
                            <span className="text-3xl font-bold text-slate-900">7</span>
                            <span className="text-sm text-slate-400">awaiting</span>
                        </p>
                    </div>
                    <div className="absolute -bottom-1 h-1 w-full scale-x-0 bg-rose-500 transition-transform group-hover:scale-x-100 left-0" />
                </div>
            </div>

            <div className="grid grid-cols-1 gap-6 xl:grid-cols-3">
                {/* Main Content: Recent Activity / Pipeline */}
                <div className="space-y-6 xl:col-span-2">
                    {/* Active Pipeline Chart (Simulated with simple CSS layout for now) */}
                    <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
                        <div className="mb-6 flex items-center justify-between">
                            <div>
                                <h3 className="text-base font-semibold text-slate-800">Pipeline Metrics</h3>
                                <p className="text-sm text-slate-500">Conversion rates across stages</p>
                            </div>
                            <Button variant="outline" size="sm" className="gap-2">
                                <TrendingUp className="h-4 w-4" /> View Full Report
                            </Button>
                        </div>

                        <div className="flex flex-col gap-4">
                            {[
                                { stage: "Applied", value: 1284, pct: 100, color: "bg-slate-300" },
                                { stage: "Screening", value: 850, pct: 66, color: "bg-sky-400" },
                                { stage: "Interview", value: 320, pct: 25, color: "bg-indigo-500" },
                                { stage: "Offer", value: 45, pct: 3.5, color: "bg-emerald-500" },
                            ].map((s) => (
                                <div key={s.stage} className="flex items-center gap-4">
                                    <div className="w-24 text-sm font-medium text-slate-600">{s.stage}</div>
                                    <div className="flex-1">
                                        <div className="h-8 w-full rounded-md bg-slate-50 p-1 relative overflow-hidden">
                                            <div
                                                className={`h-full rounded-sm ${s.color} flex items-center px-3 transition-all duration-1000`}
                                                style={{ width: `${Math.max(s.pct, 5)}%` }}
                                            >
                                                {s.pct > 15 && <span className="text-xs font-bold text-white shadow-sm">{s.value}</span>}
                                            </div>
                                        </div>
                                    </div>
                                    <div className="w-12 text-right text-sm font-semibold text-slate-700">{s.pct}%</div>
                                </div>
                            ))}
                        </div>
                    </div>

                    {/* Recent Candidate Activity */}
                    <div className="rounded-xl border border-slate-200 bg-white shadow-sm">
                        <div className="flex items-center justify-between border-b border-slate-100 px-6 py-4">
                            <h3 className="text-base font-semibold text-slate-800">Recent Applications</h3>
                            <Link to="/candidates" className="text-sm font-medium text-indigo-600 hover:text-indigo-700">
                                View All
                            </Link>
                        </div>
                        <div className="divide-y divide-slate-100">
                            {recentApps.length === 0 ? (
                                <div className="p-6 text-center text-slate-500">No recent applications</div>
                            ) : recentApps.map((app) => (
                                <div key={app.id} className="flex items-center justify-between px-6 py-4 transition-colors hover:bg-slate-50 group">
                                    <div className="flex items-center gap-4">
                                        <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-indigo-50 text-sm font-bold text-indigo-700 group-hover:bg-indigo-100 transition-colors uppercase">
                                            {app.candidateName?.substring(0, 2) || "NA"}
                                        </div>
                                        <div>
                                            <p className="text-sm font-semibold text-slate-800">{app.candidateName || `Candidate #${app.candidateId}`}</p>
                                            <p className="text-xs text-slate-500">{app.jobTitle || `Job #${app.jobId}`}</p>
                                        </div>
                                    </div>
                                    <div className="flex items-center gap-6">
                                        <div className="hidden sm:block text-right">
                                            <span className="text-xs italic text-slate-400">Awaiting score</span>
                                        </div>
                                        <StatusBadge status={app.stage} className="w-24 justify-center" />
                                        <span className="hidden text-xs text-slate-400 md:block w-16 text-right">
                                            {app.appliedAt ? new Date(app.appliedAt).toLocaleDateString() : 'New'}
                                        </span>
                                        <DropdownMenu>
                                            <DropdownMenuTrigger asChild>
                                                <Button variant="ghost" size="icon" className="h-8 w-8 text-slate-400 opacity-0 group-hover:opacity-100">
                                                    <MoreVertical className="h-4 w-4" />
                                                </Button>
                                            </DropdownMenuTrigger>
                                            <DropdownMenuContent align="end">
                                                <DropdownMenuItem asChild>
                                                    <Link to={`/candidates/${app.id}/evaluation`}>View Evaluation</Link>
                                                </DropdownMenuItem>
                                                <DropdownMenuItem>Advance Stage</DropdownMenuItem>
                                            </DropdownMenuContent>
                                        </DropdownMenu>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>

                {/* Sidebar: Upcoming Interviews */}
                <div className="space-y-6">
                    <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
                        <div className="mb-6 flex items-center justify-between">
                            <h3 className="text-base font-semibold text-slate-800">Today's Schedule</h3>
                            <div className="flex h-8 items-center rounded-lg border border-slate-200 bg-slate-50 px-3 text-xs font-semibold text-slate-600">
                                {new Date().toLocaleDateString('en-US', { month: 'short', day: 'numeric' })}
                            </div>
                        </div>

                        {upcomingInterviews.length > 0 ? (
                            <div className="space-y-4">
                                {upcomingInterviews.map((interview, index) => (
                                    <div key={interview.id} className="relative pl-4">
                                        {/* Timeline line */}
                                        {index !== upcomingInterviews.length - 1 && (
                                            <div className="absolute top-6 bottom-[-24px] left-[5px] w-px bg-slate-200" />
                                        )}
                                        {/* Timeline dot */}
                                        <div className="absolute top-2 left-0 h-3 w-3 rounded-full border-2 border-indigo-500 bg-white" />

                                        <div className="rounded-lg border border-slate-100 bg-slate-50 p-4 transition-colors hover:border-indigo-100 hover:bg-indigo-50/30">
                                            <p className="flex items-center gap-1.5 text-xs font-medium text-indigo-600 mb-1">
                                                <Clock className="h-3.5 w-3.5" /> {new Date(interview.scheduledAt).toLocaleTimeString([], { timeStyle: 'short' })}
                                            </p>
                                            <p className="text-sm font-semibold text-slate-800">{interview.candidateName || `Candidate #${interview.applicationId}`}</p>
                                            <p className="text-xs text-slate-500 mb-3">{interview.jobTitle} • {interview.type}</p>

                                            <div className="flex gap-2">
                                                <Button size="sm" variant="default" className="w-full text-xs h-8 bg-indigo-600 hover:bg-indigo-700" asChild>
                                                    <Link to={`/interviews/${interview.id}/evaluate`}>Evaluate</Link>
                                                </Button>
                                            </div>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        ) : (
                            <div className="flex h-32 flex-col items-center justify-center rounded-lg border border-dashed border-slate-200 bg-slate-50">
                                <CalendarDays className="mb-2 h-8 w-8 text-slate-300" />
                                <p className="text-sm font-medium text-slate-500">No interviews scheduled</p>
                                <p className="text-xs text-slate-400">Enjoy your free time!</p>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}
