import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { MoreHorizontal, Plus, Search, Star } from "lucide-react";
import { getAllCandidates } from "@/api/candidateApi";
import type { CandidateListResponse } from "@/types/candidate";

// ─── helpers ────────────────────────────────────────────────────────────────

function getInitials(name: string): string {
    return name
        .split(" ")
        .map((n) => n[0])
        .slice(0, 2)
        .join("")
        .toUpperCase();
}

const AVATAR_COLORS = [
    "bg-violet-500",
    "bg-pink-500",
    "bg-blue-500",
    "bg-green-500",
    "bg-orange-500",
    "bg-teal-500",
    "bg-red-500",
    "bg-yellow-500",
];

function avatarColor(name: string): string {
    let hash = 0;
    for (let i = 0; i < name.length; i++) hash = name.charCodeAt(i) + ((hash << 5) - hash);
    return AVATAR_COLORS[Math.abs(hash) % AVATAR_COLORS.length];
}

function timeAgo(dateStr: string): string {
    const date = new Date(dateStr);
    const diffMs = Date.now() - date.getTime();
    const diffSec = Math.floor(diffMs / 1000);
    if (diffSec < 60) return `${diffSec} seconds ago`;
    const diffMin = Math.floor(diffSec / 60);
    if (diffMin < 60) return `${diffMin} minutes ago`;
    const diffH = Math.floor(diffMin / 60);
    if (diffH < 24) return `${diffH} hours ago`;
    const diffD = Math.floor(diffH / 24);
    if (diffD < 30) return `${diffD} days ago`;
    const diffM = Math.floor(diffD / 30);
    return `${diffM} months ago`;
}

// Stage badge colours
const STAGE_STYLES: Record<string, string> = {
    Applied: "bg-gray-100 text-gray-700",
    Screening: "bg-blue-100 text-blue-700",
    Interview: "bg-amber-100 text-amber-700",
    Offer: "bg-green-100 text-green-700",
    Rejected: "bg-red-100 text-red-700",
};

// ─── component ───────────────────────────────────────────────────────────────

export default function CandidatesPage() {
    const navigate = useNavigate();
    const [candidates, setCandidates] = useState<CandidateListResponse[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    // filter state
    const [searchText, setSearchText] = useState("");
    const [filterJob, setFilterJob] = useState("All Jobs");
    const [filterStage, setFilterStage] = useState("All Stages");

    useEffect(() => {
        getAllCandidates()
            .then((data) => setCandidates(data))
            .catch(() => setError("Failed to load candidates. Please try again."))
            .finally(() => setLoading(false));
    }, []);

    // Derived lists for dropdowns
    const uniqueJobs = ["All Jobs", ...Array.from(new Set(candidates.map((c) => c.appliedFor)))];
    const uniqueStages = ["All Stages", ...Array.from(new Set(candidates.map((c) => c.stage)))];

    // Filtered candidates
    const filtered = candidates.filter((c) => {
        const matchSearch =
            c.name.toLowerCase().includes(searchText.toLowerCase()) ||
            c.email.toLowerCase().includes(searchText.toLowerCase());
        const matchJob = filterJob === "All Jobs" || c.appliedFor === filterJob;
        const matchStage = filterStage === "All Stages" || c.stage === filterStage;
        return matchSearch && matchJob && matchStage;
    });

    return (
        <div className="space-y-6">
            {/* ── Page header ─────────────────────────────────────────── */}
            <div className="flex items-start justify-between">
                <div>
                    <h1 className="text-2xl font-bold text-foreground">Candidate List</h1>
                    <div className="mt-4">
                        <p className="text-sm font-semibold text-foreground">Candidates</p>
                        <p className="text-xs text-muted-foreground">
                            A list of{" "}
                            <span className="font-medium text-primary">all candidates</span> across
                            all jobs.
                        </p>
                    </div>
                </div>
                <button className="flex items-center gap-2 rounded-md bg-foreground px-4 py-2 text-sm font-semibold text-background hover:opacity-90 transition-opacity">
                    <Plus className="h-4 w-4" />
                    Add Candidate
                </button>
            </div>

            {/* ── Search + Filters ─────────────────────────────────────── */}
            <div className="flex flex-col gap-3 sm:flex-row sm:items-center">
                {/* Search */}
                <div className="relative flex-1">
                    <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
                    <input
                        type="text"
                        placeholder="Search candidates..."
                        value={searchText}
                        onChange={(e) => setSearchText(e.target.value)}
                        className="w-full rounded-md border border-border bg-background py-2 pl-9 pr-3 text-sm text-foreground placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary/40"
                    />
                </div>

                {/* Job filter */}
                <select
                    value={filterJob}
                    onChange={(e) => setFilterJob(e.target.value)}
                    className="rounded-md border border-border bg-background px-3 py-2 text-sm text-foreground focus:outline-none focus:ring-2 focus:ring-primary/40"
                >
                    {uniqueJobs.map((j) => (
                        <option key={j}>{j}</option>
                    ))}
                </select>

                {/* Stage filter */}
                <select
                    value={filterStage}
                    onChange={(e) => setFilterStage(e.target.value)}
                    className="rounded-md border border-border bg-background px-3 py-2 text-sm text-foreground focus:outline-none focus:ring-2 focus:ring-primary/40"
                >
                    {uniqueStages.map((s) => (
                        <option key={s}>{s}</option>
                    ))}
                </select>
            </div>

            {/* ── Table card ───────────────────────────────────────────── */}
            <div className="rounded-lg border border-border bg-card shadow-sm overflow-hidden">
                {loading && (
                    <div className="flex items-center justify-center py-16 text-muted-foreground text-sm">
                        Loading candidates…
                    </div>
                )}

                {error && (
                    <div className="flex items-center justify-center py-16 text-red-500 text-sm">
                        {error}
                    </div>
                )}

                {!loading && !error && (
                    <div className="overflow-x-auto">
                        <table className="w-full text-sm">
                            <thead>
                                <tr className="border-b border-border bg-muted/40">
                                    <th className="px-6 py-3 text-left font-semibold text-muted-foreground">
                                        Name
                                    </th>
                                    <th className="px-6 py-3 text-left font-semibold text-muted-foreground">
                                        Applied For
                                    </th>
                                    <th className="px-6 py-3 text-left font-semibold text-muted-foreground">
                                        Stage
                                    </th>
                                    <th className="px-6 py-3 text-left font-semibold text-muted-foreground">
                                        Rating
                                    </th>
                                    <th className="px-6 py-3 text-left font-semibold text-muted-foreground">
                                        Applied Date
                                    </th>
                                    <th className="px-6 py-3" />
                                </tr>
                            </thead>
                            <tbody>
                                {filtered.length === 0 ? (
                                    <tr>
                                        <td
                                            colSpan={6}
                                            className="py-12 text-center text-muted-foreground"
                                        >
                                            No candidates found.
                                        </td>
                                    </tr>
                                ) : (
                                    filtered.map((c, idx) => (
                                        <tr
                                            key={idx}
                                            onClick={() => navigate(`/candidates/${c.id}`)}
                                            className="border-b border-border last:border-0 hover:bg-muted/50 transition-colors cursor-pointer"
                                        >
                                            {/* Name + avatar */}
                                            <td className="px-6 py-4">
                                                <div className="flex items-center gap-3">
                                                    <div
                                                        className={`flex h-9 w-9 shrink-0 items-center justify-center rounded-full text-xs font-bold text-white ${avatarColor(c.name)}`}
                                                    >
                                                        {getInitials(c.name)}
                                                    </div>
                                                    <div>
                                                        <p className="font-medium text-foreground">
                                                            {c.name}
                                                        </p>
                                                        <p className="text-xs text-muted-foreground">
                                                            {c.email}
                                                        </p>
                                                    </div>
                                                </div>
                                            </td>

                                            {/* Applied for */}
                                            <td className="px-6 py-4 text-muted-foreground">
                                                {c.appliedFor}
                                            </td>

                                            {/* Stage badge */}
                                            <td className="px-6 py-4">
                                                <span
                                                    className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium ${STAGE_STYLES[c.stage] ?? "bg-gray-100 text-gray-700"}`}
                                                >
                                                    {c.stage}
                                                </span>
                                            </td>

                                            {/* Rating */}
                                            <td className="px-6 py-4">
                                                {c.rating != null ? (
                                                    <span className="flex items-center gap-1">
                                                        <Star className="h-3.5 w-3.5 fill-amber-400 text-amber-400" />
                                                        <span className="text-foreground">
                                                            {c.rating.toFixed(1)}
                                                        </span>
                                                    </span>
                                                ) : (
                                                    <span className="flex items-center gap-1 text-muted-foreground">
                                                        <Star className="h-3.5 w-3.5" />
                                                        <span>–</span>
                                                    </span>
                                                )}
                                            </td>

                                            {/* Applied date */}
                                            <td className="px-6 py-4 text-muted-foreground">
                                                {c.appliedDate ? timeAgo(c.appliedDate) : "–"}
                                            </td>

                                            {/* Actions */}
                                            <td className="px-6 py-4 text-right">
                                                <button className="rounded p-1 text-muted-foreground hover:bg-muted hover:text-foreground transition-colors">
                                                    <MoreHorizontal className="h-4 w-4" />
                                                </button>
                                            </td>
                                        </tr>
                                    ))
                                )}
                            </tbody>
                        </table>
                    </div>
                )}
            </div>
        </div>
    );
}
