import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
    UserCircle,
    ChevronLeft,
    AlertCircle,
    Lightbulb,
} from "lucide-react";
import {
    BarChart,
    Bar,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    ResponsiveContainer,
    RadarChart,
    Radar,
    PolarGrid,
    PolarAngleAxis,
    PolarRadiusAxis,
} from "recharts";
import { toast } from "sonner";
import { PageHeader } from "@/components/shared/PageHeader";
import { StatusBadge } from "@/components/shared/StatusBadge";
import { CardSkeleton } from "@/components/shared/LoadingSkeleton";
import { EmptyState } from "@/components/shared/EmptyState";
import { Button } from "@/components/ui/button";
import { candidateService } from "@/services/candidateService";
import type { EvaluationSummaryResponse } from "@/types/models";
import { cn } from "@/lib/utils";


type TabKey = "overview" | "evaluation" | "interview" | "offer";

export default function CandidateEvaluationSummaryPage() {
    const { applicationId } = useParams<{ applicationId: string }>();
    const navigate = useNavigate();
    const [data, setData] = useState<EvaluationSummaryResponse | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [activeTab, setActiveTab] = useState<TabKey>("evaluation");

    useEffect(() => {
        if (!applicationId) return;
        candidateService
            .getEvaluation(Number(applicationId))
            .then(setData)
            .catch(() => toast.error("Failed to load evaluation data"))
            .finally(() => setIsLoading(false));
    }, [applicationId]);

    if (isLoading) return <CardSkeleton />;
    if (!data) {
        return (
            <EmptyState
                title="No evaluation data"
                description="This application has no evaluation data yet."
                action={
                    <Button variant="outline" onClick={() => navigate(-1)}>
                        <ChevronLeft className="mr-1 h-4 w-4" /> Go Back
                    </Button>
                }
            />
        );
    }

    const scorePercent = Math.round((data.aggregateScore / data.maxPossibleScore) * 100);

    const barData = data.criteriaScores.map((c) => ({
        name: c.criterionName.length > 16 ? c.criterionName.slice(0, 14) + "…" : c.criterionName,
        score: Number(c.averageScore.toFixed(2)),
        max: c.maxScore,
    }));

    const radarData = data.criteriaScores.map((c) => ({
        subject: c.criterionName.length > 12 ? c.criterionName.slice(0, 10) + "…" : c.criterionName,
        score: Number(c.averageScore.toFixed(2)),
        fullMark: c.maxScore,
    }));

    const TABS: { key: TabKey; label: string }[] = [
        { key: "overview", label: "Overview" },
        { key: "evaluation", label: "Evaluation" },
        { key: "interview", label: "Interviews" },
        { key: "offer", label: "Offer" },
    ];

    return (
        <div className="space-y-6">
            <PageHeader
                title={data.candidateName}
                description={`Candidate Evaluation Summary — ${data.jobTitle} `}
                icon={UserCircle}
                actions={
                    <Button variant="outline" onClick={() => navigate(-1)}>
                        <ChevronLeft className="mr-1 h-4 w-4" /> Back
                    </Button>
                }
            />

            {/* Tabs */}
            <div className="border-b border-slate-200">
                <div className="flex gap-0">
                    {TABS.map((tab) => (
                        <button
                            key={tab.key}
                            onClick={() => setActiveTab(tab.key)}
                            className={cn(
                                "px-5 py-3 text-sm font-medium border-b-2 transition-colors",
                                activeTab === tab.key
                                    ? "border-indigo-600 text-indigo-600"
                                    : "border-transparent text-slate-500 hover:text-slate-800",
                            )}
                        >
                            {tab.label}
                        </button>
                    ))}
                </div>
            </div>

            {activeTab === "evaluation" && (
                <div className="space-y-6">
                    {/* Aggregate Score + Recommendation */}
                    <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
                        {/* Score card */}
                        <div className="col-span-1 rounded-xl bg-gradient-to-br from-indigo-500 to-sky-500 p-6 text-white shadow-sm">
                            <p className="text-sm font-medium text-indigo-100">Aggregate Score</p>
                            <div className="mt-2 flex items-end gap-2">
                                <span className="text-5xl font-bold">{data.aggregateScore.toFixed(1)}</span>
                                <span className="mb-1 text-lg text-indigo-200">/ {data.maxPossibleScore}</span>
                            </div>
                            <div className="mt-3 h-2 w-full overflow-hidden rounded-full bg-white/30">
                                <div
                                    className="h-full rounded-full bg-white"
                                    style={{ width: `${scorePercent}% ` }}
                                />
                            </div>
                            <p className="mt-1 text-xs text-indigo-100">{scorePercent}% of max score</p>
                        </div>

                        {/* Recommendation */}
                        <div className="col-span-1 flex flex-col items-center justify-center rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
                            <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">
                                Overall Recommendation
                            </p>
                            <div className="mt-3">
                                <StatusBadge
                                    status={data.overallRecommendation}
                                    className="text-sm px-4 py-1.5"
                                />
                            </div>
                        </div>

                        {/* Interviewers */}
                        <div className="col-span-1 flex flex-col justify-center rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
                            <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">
                                Evaluations
                            </p>
                            <p className="mt-2 text-3xl font-bold text-slate-800">
                                {data.interviewerSummaries.length}
                            </p>
                            <p className="text-sm text-slate-500">interviewers</p>
                        </div>
                    </div>

                    {/* Charts */}
                    <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
                        {/* Bar Chart */}
                        <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
                            <h3 className="mb-4 text-sm font-semibold text-slate-700">Criteria Scores (Bar)</h3>
                            <ResponsiveContainer width="100%" height={240}>
                                <BarChart data={barData} margin={{ top: 5, right: 10, left: -10, bottom: 5 }}>
                                    <CartesianGrid strokeDasharray="3 3" stroke="#f1f5f9" />
                                    <XAxis dataKey="name" tick={{ fontSize: 11 }} />
                                    <YAxis domain={[0, 5]} tick={{ fontSize: 11 }} />
                                    <Tooltip
                                        contentStyle={{ borderRadius: "8px", border: "1px solid #e2e8f0", fontSize: 12 }}
                                    />
                                    <Bar dataKey="score" fill="#6366F1" radius={[4, 4, 0, 0]} />
                                    <Bar dataKey="max" fill="#e2e8f0" radius={[4, 4, 0, 0]} />
                                </BarChart>
                            </ResponsiveContainer>
                        </div>

                        {/* Radar Chart */}
                        <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
                            <h3 className="mb-4 text-sm font-semibold text-slate-700">Criteria Scores (Radar)</h3>
                            <ResponsiveContainer width="100%" height={240}>
                                <RadarChart data={radarData}>
                                    <PolarGrid stroke="#e2e8f0" />
                                    <PolarAngleAxis dataKey="subject" tick={{ fontSize: 11 }} />
                                    <PolarRadiusAxis domain={[0, 5]} tick={{ fontSize: 10 }} />
                                    <Radar
                                        name="Score"
                                        dataKey="score"
                                        stroke="#6366F1"
                                        fill="#6366F1"
                                        fillOpacity={0.25}
                                    />
                                    <Tooltip
                                        contentStyle={{ borderRadius: "8px", border: "1px solid #e2e8f0", fontSize: 12 }}
                                    />
                                </RadarChart>
                            </ResponsiveContainer>
                        </div>
                    </div>

                    {/* Interviewer Score Table */}
                    <div className="rounded-xl border border-slate-200 bg-white shadow-sm">
                        <div className="border-b border-slate-100 px-6 py-4">
                            <h3 className="text-sm font-semibold text-slate-700">Interviewer Scores</h3>
                        </div>
                        <table className="w-full text-sm">
                            <thead>
                                <tr className="bg-slate-50 text-xs font-semibold uppercase tracking-wide text-slate-400">
                                    <th className="px-6 py-3 text-left">Interviewer</th>
                                    <th className="px-4 py-3 text-center">Score</th>
                                    <th className="px-4 py-3 text-left">Recommendation</th>
                                    <th className="px-4 py-3 text-left">Comment</th>
                                    <th className="px-4 py-3 text-left">Date</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-slate-100">
                                {data.interviewerSummaries.map((s, i) => (
                                    <tr key={i} className="hover:bg-slate-50/50">
                                        <td className="px-6 py-3 font-medium text-slate-800">{s.interviewerName}</td>
                                        <td className="px-4 py-3 text-center">
                                            <span className="inline-flex h-8 w-8 items-center justify-center rounded-full bg-indigo-100 text-sm font-bold text-indigo-700">
                                                {s.totalScore.toFixed(1)}
                                            </span>
                                        </td>
                                        <td className="px-4 py-3">
                                            <StatusBadge status={s.recommendation} />
                                        </td>
                                        <td className="px-4 py-3 max-w-[200px] truncate text-slate-600">
                                            {s.comment ?? <span className="text-slate-380 italic">—</span>}
                                        </td>
                                        <td className="px-4 py-3 text-slate-500">
                                            {new Date(s.date).toLocaleDateString("en-US", { month: "short", day: "numeric" })}
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>

                    {/* Insights */}
                    <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                        {/* Strengths */}
                        <div className="rounded-xl border border-emerald-100 bg-emerald-50 p-5">
                            <div className="mb-3 flex items-center gap-2">
                                <Lightbulb className="h-4 w-4 text-emerald-600" />
                                <h4 className="text-sm font-semibold text-emerald-800">Top Strengths</h4>
                            </div>
                            <ul className="space-y-1.5">
                                {data.topStrengths.map((s, i) => (
                                    <li key={i} className="flex items-start gap-2 text-sm text-emerald-700">
                                        <span className="mt-1 h-1.5 w-1.5 shrink-0 rounded-full bg-emerald-500" />
                                        {s}
                                    </li>
                                ))}
                            </ul>
                        </div>
                        {/* Concerns */}
                        <div className="rounded-xl border border-amber-100 bg-amber-50 p-5">
                            <div className="mb-3 flex items-center gap-2">
                                <AlertCircle className="h-4 w-4 text-amber-600" />
                                <h4 className="text-sm font-semibold text-amber-800">Main Concerns</h4>
                            </div>
                            <ul className="space-y-1.5">
                                {data.mainConcerns.map((c, i) => (
                                    <li key={i} className="flex items-start gap-2 text-sm text-amber-700">
                                        <span className="mt-1 h-1.5 w-1.5 shrink-0 rounded-full bg-amber-500" />
                                        {c}
                                    </li>
                                ))}
                            </ul>
                        </div>
                    </div>
                </div>
            )}

            {activeTab === "overview" && (
                <div className="flex h-48 items-center justify-center text-slate-400">
                    <p className="text-sm">Overview tab — coming soon</p>
                </div>
            )}
            {activeTab === "interview" && (
                <div className="flex h-48 items-center justify-center text-slate-400">
                    <p className="text-sm">Interview history tab — coming soon</p>
                </div>
            )}
            {activeTab === "offer" && (
                <div className="flex h-48 items-center justify-center text-slate-400">
                    <p className="text-sm">Offer tab — coming soon</p>
                </div>
            )}
        </div>
    );
}
