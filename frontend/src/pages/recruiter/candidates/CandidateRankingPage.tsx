import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
    Trophy,
    ChevronLeft,
    Eye,
    MoveRight,
    FileText,
    XCircle,
    ArrowUpDown,
} from "lucide-react";
import { toast } from "sonner";
import { PageHeader } from "@/components/shared/PageHeader";
import { StatusBadge } from "@/components/shared/StatusBadge";
import { EmptyState } from "@/components/shared/EmptyState";
import { TableSkeleton } from "@/components/shared/LoadingSkeleton";
import { Button } from "@/components/ui/button";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { candidateService } from "@/services/candidateService";
import type { CandidateRankingResponse, RankedCandidate } from "@/types/models";
import { cn } from "@/lib/utils";

const RANK_MEDAL: Record<number, { bg: string; text: string }> = {
    1: { bg: "bg-amber-100", text: "text-amber-700" },
    2: { bg: "bg-slate-100", text: "text-slate-600" },
    3: { bg: "bg-orange-100", text: "text-orange-700" },
};

function ScoreBar({ score, max = 5 }: { score: number; max?: number }) {
    const pct = Math.min((score / max) * 100, 100);
    const color =
        pct >= 80 ? "bg-emerald-500" : pct >= 60 ? "bg-indigo-500" : pct >= 40 ? "bg-amber-400" : "bg-rose-400";
    return (
        <div className="flex items-center gap-2">
            <div className="h-1.5 w-24 overflow-hidden rounded-full bg-slate-100">
                <div className={cn("h-full rounded-full", color)} style={{ width: `${pct}%` }} />
            </div>
            <span className="text-xs font-semibold text-slate-700">{score.toFixed(1)}</span>
        </div>
    );
}

export default function CandidateRankingPage() {
    const { jobId } = useParams<{ jobId: string }>();
    const navigate = useNavigate();
    const [data, setData] = useState<CandidateRankingResponse | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [sortField, setSortField] = useState<"rank" | "score" | "experience">("rank");
    const [sortAsc, setSortAsc] = useState(true);

    useEffect(() => {
        if (!jobId) return;
        candidateService
            .getRanking(Number(jobId))
            .then(setData)
            .catch(() => toast.error("Failed to load candidate ranking"))
            .finally(() => setIsLoading(false));
    }, [jobId]);

    const toggleSort = (field: typeof sortField) => {
        if (sortField === field) setSortAsc((a) => !a);
        else { setSortField(field); setSortAsc(true); }
    };

    const sorted = [...(data?.candidates ?? [])].sort((a, b) => {
        const dir = sortAsc ? 1 : -1;
        if (sortField === "rank") return (a.rank - b.rank) * dir;
        if (sortField === "score") return (a.aggregateScore - b.aggregateScore) * dir;
        if (sortField === "experience")
            return ((a.experienceYears ?? 0) - (b.experienceYears ?? 0)) * dir;
        return 0;
    });

    return (
        <div className="space-y-6">
            <PageHeader
                title={data ? `${data.jobTitle} — Ranking` : "Candidate Ranking"}
                description="Ranked candidates by aggregate interview score"
                icon={Trophy}
                actions={
                    <Button variant="outline" onClick={() => navigate(-1)}>
                        <ChevronLeft className="mr-1 h-4 w-4" /> Back
                    </Button>
                }
            />

            <div className="overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm">
                {isLoading ? (
                    <div className="p-6"><TableSkeleton rows={5} /></div>
                ) : sorted.length === 0 ? (
                    <EmptyState icon={Trophy} title="No ranked candidates" description="Candidates will appear here after interview evaluations are submitted" />
                ) : (
                    <table className="w-full text-sm">
                        <thead>
                            <tr className="border-b border-slate-100 bg-slate-50">
                                <th className="px-6 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500 w-16">
                                    <button
                                        className="flex items-center gap-1 hover:text-indigo-600"
                                        onClick={() => toggleSort("rank")}
                                    >
                                        Rank <ArrowUpDown className="h-3 w-3" />
                                    </button>
                                </th>
                                <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
                                    Candidate
                                </th>
                                <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
                                    <button
                                        className="flex items-center gap-1 hover:text-indigo-600"
                                        onClick={() => toggleSort("score")}
                                    >
                                        Score <ArrowUpDown className="h-3 w-3" />
                                    </button>
                                </th>
                                <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">Stage</th>
                                <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">Recommendation</th>
                                <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
                                    <button
                                        className="flex items-center gap-1 hover:text-indigo-600"
                                        onClick={() => toggleSort("experience")}
                                    >
                                        Exp. <ArrowUpDown className="h-3 w-3" />
                                    </button>
                                </th>
                                <th className="px-4 py-3 text-right text-xs font-semibold uppercase tracking-wide text-slate-500">Actions</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-slate-100">
                            {sorted.map((c: RankedCandidate) => {
                                const medal = RANK_MEDAL[c.rank];
                                return (
                                    <tr key={c.applicationId} className="group hover:bg-indigo-50/20 transition-colors">
                                        <td className="px-6 py-4">
                                            <span
                                                className={cn(
                                                    "inline-flex h-7 w-7 items-center justify-center rounded-full text-sm font-bold",
                                                    medal?.bg ?? "bg-slate-100",
                                                    medal?.text ?? "text-slate-600",
                                                )}
                                            >
                                                {c.rank}
                                            </span>
                                        </td>
                                        <td className="px-4 py-4">
                                            <div className="flex items-center gap-2">
                                                <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-indigo-100 text-xs font-bold text-indigo-700">
                                                    {c.candidateName.split(" ").map((n) => n[0]).join("").slice(0, 2)}
                                                </div>
                                                <div>
                                                    <p className="font-medium text-slate-800">{c.candidateName}</p>
                                                    {c.candidateEmail && (
                                                        <p className="text-xs text-slate-400">{c.candidateEmail}</p>
                                                    )}
                                                </div>
                                            </div>
                                        </td>
                                        <td className="px-4 py-4">
                                            <ScoreBar score={c.aggregateScore} />
                                        </td>
                                        <td className="px-4 py-4">
                                            <StatusBadge status={c.stage} />
                                        </td>
                                        <td className="px-4 py-4">
                                            {c.recommendation ? (
                                                <StatusBadge status={c.recommendation} />
                                            ) : (
                                                <span className="text-xs text-slate-400 italic">Pending</span>
                                            )}
                                        </td>
                                        <td className="px-4 py-4 text-slate-600">
                                            {c.experienceYears !== undefined ? `${c.experienceYears}y` : "—"}
                                        </td>
                                        <td className="px-4 py-4 text-right">
                                            <DropdownMenu>
                                                <DropdownMenuTrigger asChild>
                                                    <button className="rounded-md px-2 py-1 text-xs font-medium text-slate-500 opacity-0 transition hover:bg-slate-100 group-hover:opacity-100">
                                                        Actions ▾
                                                    </button>
                                                </DropdownMenuTrigger>
                                                <DropdownMenuContent align="end" className="w-44">
                                                    <DropdownMenuItem
                                                        onClick={() =>
                                                            navigate(`/candidates/${c.applicationId}/evaluation`)
                                                        }
                                                        className="gap-2"
                                                    >
                                                        <Eye className="h-3.5 w-3.5" /> View Evaluation
                                                    </DropdownMenuItem>
                                                    <DropdownMenuItem
                                                        onClick={() => navigate(`/offers/new?applicationId=${c.applicationId}`)}
                                                        className="gap-2 text-indigo-600"
                                                    >
                                                        <FileText className="h-3.5 w-3.5" /> Create Offer
                                                    </DropdownMenuItem>
                                                    <DropdownMenuItem className="gap-2 text-slate-600">
                                                        <MoveRight className="h-3.5 w-3.5" /> Move Stage
                                                    </DropdownMenuItem>
                                                    <DropdownMenuItem className="gap-2 text-rose-600">
                                                        <XCircle className="h-3.5 w-3.5" /> Reject
                                                    </DropdownMenuItem>
                                                </DropdownMenuContent>
                                            </DropdownMenu>
                                        </td>
                                    </tr>
                                );
                            })}
                        </tbody>
                    </table>
                )}
            </div>
        </div>
    );
}
