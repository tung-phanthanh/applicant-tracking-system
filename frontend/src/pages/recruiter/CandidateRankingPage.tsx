import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { candidateService, type CandidateRanking as CandidateRankingType, type CandidateRank } from "@/services/candidateService";

const RANK_COLORS = ["bg-yellow-100 text-yellow-700", "bg-gray-100 text-gray-700", "bg-amber-100 text-amber-700"];

function getInitials(name: string): string {
    return name.split(" ").map((n) => n[0]).slice(0, 2).join("").toUpperCase();
}

const AVATAR_COLORS = ["bg-pink-100 text-pink-600", "bg-purple-100 text-purple-600", "bg-blue-100 text-blue-600"];

// Mock jobs list - in real app this should come from API
const MOCK_JOBS = [
    { id: "40000000-0000-0000-0000-000000000001", title: "Senior Frontend Developer" },
    { id: "40000000-0000-0000-0000-000000000002", title: "Product Designer" },
];

export default function CandidateRankingPage() {
    const navigate = useNavigate();
    const [rankingData, setRankingData] = useState<CandidateRankingType | null>(null);
    const [selectedJobId, setSelectedJobId] = useState<string>(MOCK_JOBS[0].id);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        setLoading(true);
        candidateService.getRanking(selectedJobId)
            .then(setRankingData)
            .catch(err => {
                console.error("Failed to load rankings:", err);
            })
            .finally(() => setLoading(false));
    }, [selectedJobId]);

    const statusBadge = (status: string) => {
        const cls = status.includes("Offer")
            ? "bg-green-50 text-green-700 ring-green-600/20"
            : status === "INTERVIEW"
              ? "bg-yellow-50 text-yellow-800 ring-yellow-600/20"
              : "bg-gray-50 text-gray-700 ring-gray-600/20";
        return <span className={`inline-flex items-center rounded-md px-2 py-1 text-xs font-medium ring-1 ring-inset ${cls}`}>{status}</span>;
    };

    const handleJobChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        setSelectedJobId(e.target.value);
    };

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-xl font-semibold text-foreground">Candidate Ranking</h1>
                    <p className="text-sm text-muted-foreground">
                        Compare candidates for <strong>{rankingData?.jobTitle || "selected job"}</strong>.
                    </p>
                </div>
                <select
                    className="h-10 rounded-md border border-input bg-background px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-ring"
                    value={selectedJobId}
                    onChange={handleJobChange}
                >
                    {MOCK_JOBS.map(job => (
                        <option key={job.id} value={job.id}>{job.title}</option>
                    ))}
                </select>
            </div>

            {loading ? (
                <div className="flex items-center justify-center h-64">
                    <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
                </div>
            ) : !rankingData || rankingData.candidates.length === 0 ? (
                <div className="text-center text-muted-foreground py-8">
                    No candidates found for this job
                </div>
            ) : (
                <div className="overflow-hidden rounded-lg border border-border bg-card shadow-sm">
                    <div className="overflow-x-auto">
                        <table className="min-w-full divide-y divide-border">
                            <thead className="bg-muted/50">
                                <tr>
                                    <th className="py-3.5 pl-6 pr-3 text-left text-xs font-semibold uppercase tracking-wider text-muted-foreground">Rank</th>
                                    <th className="px-3 py-3.5 text-left text-xs font-semibold uppercase tracking-wider text-muted-foreground">Candidate</th>
                                    <th className="px-3 py-3.5 text-left text-xs font-semibold uppercase tracking-wider text-muted-foreground">Overall Score</th>
                                    <th className="px-3 py-3.5 text-left text-xs font-semibold uppercase tracking-wider text-muted-foreground">Tech</th>
                                    <th className="px-3 py-3.5 text-left text-xs font-semibold uppercase tracking-wider text-muted-foreground">Culture</th>
                                    <th className="px-3 py-3.5 text-left text-xs font-semibold uppercase tracking-wider text-muted-foreground">Status</th>
                                    <th className="py-3.5 pl-3 pr-6 text-right text-xs font-semibold uppercase tracking-wider text-muted-foreground">Actions</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-border">
                                {rankingData.candidates.map((c: CandidateRank, idx: number) => (
                                    <tr key={c.candidateId} className="transition-colors hover:bg-muted/30">
                                        <td className="whitespace-nowrap py-4 pl-6 pr-3">
                                            <div className={`flex h-8 w-8 items-center justify-center rounded-full font-bold text-sm ${RANK_COLORS[idx] || RANK_COLORS[1]}`}>
                                                {c.rank}
                                            </div>
                                        </td>
                                        <td className="whitespace-nowrap px-3 py-4">
                                            <div className="flex items-center gap-3">
                                                <div className={`flex h-10 w-10 items-center justify-center rounded-full font-bold text-sm ${AVATAR_COLORS[idx % AVATAR_COLORS.length]}`}>
                                                    {getInitials(c.candidateName)}
                                                </div>
                                                <div>
                                                    <div className="font-medium text-foreground">{c.candidateName}</div>
                                                    <div className="text-xs text-muted-foreground">{c.candidateEmail}</div>
                                                </div>
                                            </div>
                                        </td>
                                        <td className="whitespace-nowrap px-3 py-4 text-sm font-bold text-foreground">
                                            {c.overallScore} <span className="text-xs font-normal text-muted-foreground">/ 5</span>
                                        </td>
                                        <td className="whitespace-nowrap px-3 py-4">
                                            <div className="h-2 w-24 rounded-full bg-muted">
                                                <div className="h-2 rounded-full bg-green-500" style={{ width: `${c.techScore}%` }} />
                                            </div>
                                        </td>
                                        <td className="whitespace-nowrap px-3 py-4">
                                            <div className="h-2 w-24 rounded-full bg-muted">
                                                <div className="h-2 rounded-full bg-blue-500" style={{ width: `${c.cultureScore}%` }} />
                                            </div>
                                        </td>
                                        <td className="whitespace-nowrap px-3 py-4">{statusBadge(c.status)}</td>
                                        <td className="whitespace-nowrap py-4 pl-3 pr-6 text-right">
                                            <button
                                                className="text-sm font-medium text-primary hover:text-primary/80"
                                                onClick={() => navigate(`/applications/${c.applicationId}/evaluation`)}
                                            >
                                                View
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}
        </div>
    );
}
