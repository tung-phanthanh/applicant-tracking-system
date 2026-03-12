import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
    ArrowLeft,
    ArrowRight,
    Building2,
    Calendar,
    Mail,
    Phone,
    Star,
    X,
    MapPin,
} from "lucide-react";
import { getCandidateById } from "@/api/candidateApi";
import type { CandidateProfileResponse } from "@/types/candidate";

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

function formatDate(dateStr: string): string {
    if (!dateStr) return "–";
    return new Date(dateStr).toLocaleDateString("en-US", {
        year: "numeric",
        month: "long",
        day: "numeric",
    });
}

const STAGE_STYLES: Record<string, string> = {
    APPLIED: "bg-gray-100 text-gray-700",
    SCREENING: "bg-blue-100 text-blue-700",
    INTERVIEW: "bg-amber-100 text-amber-700",
    OFFER: "bg-green-100 text-green-700",
    HIRED: "bg-emerald-100 text-emerald-700",
    REJECTED: "bg-red-100 text-red-700",
    // lowercase fallback
    Applied: "bg-gray-100 text-gray-700",
    Screening: "bg-blue-100 text-blue-700",
    Interview: "bg-amber-100 text-amber-700",
    Offer: "bg-green-100 text-green-700",
    Hired: "bg-emerald-100 text-emerald-700",
    Rejected: "bg-red-100 text-red-700",
};

type Tab = "profile" | "scorecards" | "history";

// ─── component ───────────────────────────────────────────────────────────────

export default function CandidateProfilePage() {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();

    const [candidate, setCandidate] = useState<CandidateProfileResponse | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [activeTab, setActiveTab] = useState<Tab>("profile");

    useEffect(() => {
        if (!id) return;
        getCandidateById(id)
            .then(setCandidate)
            .catch(() => setError("Failed to load candidate profile."))
            .finally(() => setLoading(false));
    }, [id]);

    if (loading) {
        return (
            <div className="flex h-64 items-center justify-center text-sm text-muted-foreground">
                Loading candidate profile…
            </div>
        );
    }

    if (error || !candidate) {
        return (
            <div className="flex h-64 items-center justify-center text-sm text-red-500">
                {error ?? "Candidate not found."}
            </div>
        );
    }

    const stageBadgeClass =
        STAGE_STYLES[candidate.stage] ?? "bg-gray-100 text-gray-700";

    return (
        <div className="space-y-6">
            {/* ── Back button ─────────────────────────────────────────── */}
            <button
                onClick={() => navigate("/candidates")}
                className="flex items-center gap-2 text-sm text-muted-foreground hover:text-foreground transition-colors"
            >
                <ArrowLeft className="h-4 w-4" />
                Back to Candidates
            </button>

            {/* ── Page title ──────────────────────────────────────────── */}
            <h1 className="text-2xl font-bold text-foreground">Candidate Profile</h1>

            {/* ── Header card ─────────────────────────────────────────── */}
            <div className="rounded-lg border border-border bg-card p-6 shadow-sm">
                <div className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
                    {/* Left: avatar + info */}
                    <div className="flex items-start gap-4">
                        <div
                            className={`flex h-16 w-16 shrink-0 items-center justify-center rounded-full text-lg font-bold text-white ${avatarColor(candidate.name)}`}
                        >
                            {getInitials(candidate.name)}
                        </div>
                        <div className="space-y-1">
                            <h2 className="text-xl font-bold text-foreground">{candidate.name}</h2>
                            <div className="flex flex-wrap items-center gap-x-4 gap-y-1 text-sm text-muted-foreground">
                                <span className="flex items-center gap-1">
                                    <Mail className="h-3.5 w-3.5" />
                                    {candidate.email}
                                </span>
                                {candidate.phone && (
                                    <span className="flex items-center gap-1">
                                        <Phone className="h-3.5 w-3.5" />
                                        {candidate.phone}
                                    </span>
                                )}
                            </div>
                            <div className="flex items-center gap-2 pt-1">
                                <span className="text-sm text-muted-foreground">
                                    Applied for{" "}
                                    <span className="font-semibold text-foreground">
                                        {candidate.appliedFor}
                                    </span>
                                </span>
                                <span
                                    className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium ${stageBadgeClass}`}
                                >
                                    {candidate.stage}
                                </span>
                            </div>
                        </div>
                    </div>

                    {/* Right: action buttons */}
                    <div className="flex shrink-0 items-center gap-2">
                        <button className="flex items-center gap-1.5 rounded-md border border-border bg-background px-4 py-2 text-sm font-medium text-foreground hover:bg-muted transition-colors">
                            <X className="h-4 w-4" />
                            Reject
                        </button>
                        <button className="flex items-center gap-1.5 rounded-md bg-foreground px-4 py-2 text-sm font-semibold text-background hover:opacity-90 transition-opacity">
                            Move to Interview
                            <ArrowRight className="h-4 w-4" />
                        </button>
                    </div>
                </div>

                {/* ── Tabs ──────────────────────────────────────────────── */}
                <div className="mt-6 border-b border-border">
                    <nav className="flex gap-6">
                        {(["profile", "scorecards", "history"] as Tab[]).map((tab) => (
                            <button
                                key={tab}
                                onClick={() => setActiveTab(tab)}
                                className={`pb-3 text-sm font-medium capitalize transition-colors ${
                                    activeTab === tab
                                        ? "border-b-2 border-foreground text-foreground"
                                        : "text-muted-foreground hover:text-foreground"
                                }`}
                            >
                                {tab === "profile" ? "Profile & CV" : tab.charAt(0).toUpperCase() + tab.slice(1)}
                            </button>
                        ))}
                    </nav>
                </div>
            </div>

            {/* ── Tab content ─────────────────────────────────────────── */}
            {activeTab === "profile" && (
                <div className="grid grid-cols-1 gap-6 lg:grid-cols-3">
                    {/* CV Preview – takes 2/3 */}
                    <div className="lg:col-span-2 space-y-6">
                        {/* CV section */}
                        <div className="rounded-lg border border-border bg-card p-6 shadow-sm">
                            <div className="mb-4 flex items-center justify-between">
                                <h3 className="font-semibold text-foreground">CV Preview</h3>
                                <button className="flex items-center gap-1.5 text-sm text-primary hover:underline">
                                    <svg
                                        xmlns="http://www.w3.org/2000/svg"
                                        className="h-4 w-4"
                                        fill="none"
                                        viewBox="0 0 24 24"
                                        stroke="currentColor"
                                        strokeWidth={2}
                                    >
                                        <path
                                            strokeLinecap="round"
                                            strokeLinejoin="round"
                                            d="M4 16v2a2 2 0 002 2h12a2 2 0 002-2v-2M7 10l5 5 5-5M12 15V3"
                                        />
                                    </svg>
                                    Download PDF
                                </button>
                            </div>
                            {/* Placeholder viewer */}
                            <div className="flex h-64 flex-col items-center justify-center rounded-md border border-dashed border-border bg-muted/30 text-muted-foreground">
                                <svg
                                    xmlns="http://www.w3.org/2000/svg"
                                    className="mb-2 h-12 w-12 opacity-20"
                                    fill="none"
                                    viewBox="0 0 24 24"
                                    stroke="currentColor"
                                    strokeWidth={1}
                                >
                                    <path
                                        strokeLinecap="round"
                                        strokeLinejoin="round"
                                        d="M9 12h6m-6 4h6m2 4H7a2 2 0 01-2-2V6a2 2 0 012-2h7l5 5v13a2 2 0 01-2 2z"
                                    />
                                </svg>
                                <p className="text-sm">PDF Viewer Placeholder</p>
                                <p className="text-xs opacity-60">
                                    resume_{candidate.name.toLowerCase().replace(/\s/g, "_")}.pdf
                                </p>
                            </div>
                        </div>

                        {/* Summary */}
                        <div className="rounded-lg border border-border bg-card p-6 shadow-sm">
                            <h3 className="mb-3 font-semibold text-foreground">Summary</h3>
                            <p className="text-sm leading-relaxed text-muted-foreground whitespace-pre-wrap">
                                {candidate.summary ? (
                                    candidate.summary
                                ) : (
                                    <>
                                        {candidate.currentCompany
                                            ? `Currently working at ${candidate.currentCompany}. Applied for the ${candidate.appliedFor} position. `
                                            : ""}
                                        No additional summary available. Please review the candidate's CV for more details.
                                    </>
                                )}
                            </p>
                        </div>
                    </div>

                    {/* Sidebar – 1/3 */}
                    <div className="space-y-6">
                        {/* Details */}
                        <div className="rounded-lg border border-border bg-card p-6 shadow-sm">
                            <h3 className="mb-4 text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                                Details
                            </h3>
                            <dl className="space-y-3 text-sm">
                                <div className="flex items-center justify-between">
                                    <dt className="text-muted-foreground">Status</dt>
                                    <dd className="font-medium text-foreground">{candidate.status}</dd>
                                </div>
                                <div className="flex items-center justify-between">
                                    <dt className="text-muted-foreground">Rating</dt>
                                    <dd className="flex items-center gap-1 font-medium text-foreground">
                                        {candidate.rating != null ? (
                                            <>
                                                <Star className="h-3.5 w-3.5 fill-amber-400 text-amber-400" />
                                                {candidate.rating.toFixed(1)}
                                            </>
                                        ) : (
                                            "–"
                                        )}
                                    </dd>
                                </div>
                                <div className="flex items-center justify-between">
                                    <dt className="text-muted-foreground">Applied Date</dt>
                                    <dd className="font-medium text-foreground">
                                        {formatDate(candidate.appliedDate)}
                                    </dd>
                                </div>
                                {candidate.currentCompany && (
                                    <div className="flex items-center justify-between">
                                        <dt className="text-muted-foreground">Current Company</dt>
                                        <dd className="flex items-center gap-1 font-medium text-foreground">
                                            <Building2 className="h-3.5 w-3.5 text-muted-foreground" />
                                            {candidate.currentCompany}
                                        </dd>
                                    </div>
                                )}
                                {candidate.experienceYears != null && (
                                    <div className="flex items-center justify-between">
                                        <dt className="text-muted-foreground">Experience</dt>
                                        <dd className="font-medium text-foreground">
                                            {candidate.experienceYears} {candidate.experienceYears === 1 ? 'Year' : 'Years'}
                                        </dd>
                                    </div>
                                )}
                                {candidate.source && (
                                    <div className="flex items-center justify-between">
                                        <dt className="text-muted-foreground">Source</dt>
                                        <dd className="font-medium text-foreground">{candidate.source}</dd>
                                    </div>
                                )}
                                {candidate.location && (
                                    <div className="flex items-center justify-between">
                                        <dt className="text-muted-foreground">Location</dt>
                                        <dd className="flex items-center gap-1 font-medium text-foreground">
                                            <MapPin className="h-3.5 w-3.5 text-muted-foreground" />
                                            {candidate.location}
                                        </dd>
                                    </div>
                                )}
                                {candidate.phone && (
                                    <div className="flex items-center justify-between">
                                        <dt className="text-muted-foreground">Phone</dt>
                                        <dd className="font-medium text-foreground">{candidate.phone}</dd>
                                    </div>
                                )}
                            </dl>
                        </div>

                        {/* Documents placeholder */}
                        <div className="rounded-lg border border-border bg-card p-6 shadow-sm">
                            <h3 className="mb-4 text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                                Documents
                            </h3>
                            <div className="space-y-3">
                                {candidate.documents && candidate.documents.length > 0 ? (
                                    candidate.documents.map((doc) => (
                                        <a key={doc.id} href={doc.fileUrl} target="_blank" rel="noreferrer" className="flex items-center gap-3 rounded-md border border-border p-3 hover:bg-muted/50 transition-colors">
                                            <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded bg-red-50 text-red-500">
                                                <svg className="h-4 w-4" fill="currentColor" viewBox="0 0 20 20">
                                                    <path
                                                        fillRule="evenodd"
                                                        d="M4 4a2 2 0 012-2h4.586A2 2 0 0112 2.586L15.414 6A2 2 0 0116 7.414V16a2 2 0 01-2 2H6a2 2 0 01-2-2V4zm2 6a1 1 0 011-1h6a1 1 0 110 2H7a1 1 0 01-1-1zm1 3a1 1 0 100 2h6a1 1 0 100-2H7z"
                                                        clipRule="evenodd"
                                                    />
                                                </svg>
                                            </div>
                                            <div>
                                                <p className="text-xs font-medium text-foreground">
                                                    {doc.fileName}
                                                </p>
                                                <p className="text-xs text-muted-foreground">
                                                    {doc.fileSizeBytes ? (doc.fileSizeBytes / 1024 / 1024).toFixed(1) + " MB" : ""}
                                                </p>
                                            </div>
                                        </a>
                                    ))
                                ) : (
                                    <p className="text-sm text-muted-foreground">No documents uploaded</p>
                                )}
                            </div>
                        </div>
                    </div>
                </div>
            )}

            {activeTab === "scorecards" && (
                <div className="rounded-lg border border-border bg-card p-12 text-center text-muted-foreground shadow-sm">
                    <Calendar className="mx-auto mb-3 h-10 w-10 opacity-20" />
                    <p className="text-sm">No scorecards available yet.</p>
                </div>
            )}

            {activeTab === "history" && (
                <div className="rounded-lg border border-border bg-card p-12 text-center text-muted-foreground shadow-sm">
                    <Calendar className="mx-auto mb-3 h-10 w-10 opacity-20" />
                    <p className="text-sm">No stage history available yet.</p>
                </div>
            )}
        </div>
    );
}
