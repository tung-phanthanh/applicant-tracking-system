import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { User, Calendar, ArrowLeft } from "lucide-react";
import { Button } from "@/components/ui/button";
import { candidateService, type CandidateEvaluation as CandidateEvaluationType } from "@/services/candidateService";

function getInitials(name: string): string {
    return name.split(" ").map((n) => n[0]).slice(0, 2).join("").toUpperCase();
}

function getStatusBadge(status: string) {
    const cls = status === "Completed" || status === "Passed"
        ? "bg-green-100 text-green-800"
        : status === "Scheduled" || status === "Pending"
          ? "bg-yellow-100 text-yellow-800"
          : "bg-red-100 text-red-800";
    return <span className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium ${cls}`}>{status}</span>;
}

function getScoreBadge(score: number | null) {
    if (score === null) return <span className="inline-flex rounded-full bg-muted px-2 text-xs font-semibold leading-5 text-muted-foreground">- / 5</span>;
    const cls = score >= 4 ? "bg-green-100 text-green-800" : score >= 3 ? "bg-yellow-100 text-yellow-800" : "bg-red-100 text-red-800";
    return <span className={`inline-flex rounded-full px-2 text-xs font-semibold leading-5 ${cls}`}>{score}/5</span>;
}

export default function CandidateEvaluationPage() {
    const { applicationId } = useParams<{ applicationId: string }>();
    const navigate = useNavigate();
    const [data, setData] = useState<CandidateEvaluationType | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        if (!applicationId) return;
        
        candidateService.getEvaluation(applicationId)
            .then(setData)
            .catch(err => {
                console.error("Failed to load evaluation:", err);
                setError("Failed to load candidate evaluation");
            })
            .finally(() => setLoading(false));
    }, [applicationId]);

    if (loading) {
        return (
            <div className="flex items-center justify-center h-64">
                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
            </div>
        );
    }

    if (error || !data) {
        return (
            <div className="space-y-4">
                <Button variant="ghost" onClick={() => navigate(-1)}>
                    <ArrowLeft className="mr-1.5 h-4 w-4" /> Back
                </Button>
                <div className="text-center text-muted-foreground py-8">
                    {error || "No evaluation data found"}
                </div>
            </div>
        );
    }

    const recColor = data.recommendation === "Strong Hire" || data.recommendation === "Hire" 
        ? "text-green-600" 
        : data.recommendation === "No Hire" 
          ? "text-red-600" 
          : "text-foreground";

    return (
        <div className="space-y-6">
            {/* Header */}
            <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
                <div className="flex items-center gap-4">
                    <div className="flex h-16 w-16 items-center justify-center rounded-full bg-pink-100 text-xl font-bold text-pink-600 ring-4 ring-background">
                        {getInitials(data.candidateName)}
                    </div>
                    <div>
                        <h2 className="text-2xl font-bold text-foreground">{data.candidateName}</h2>
                        <p className="text-sm text-muted-foreground">
                            Evaluation for <strong>{data.positionTitle}</strong>
                        </p>
                    </div>
                </div>
                <div className="flex gap-2">
                    <Button variant="outline" onClick={() => navigate(`/candidates/${data.candidateId}`)}>View Profile</Button>
                    <Button onClick={() => navigate(`/offers/create?applicationId=${applicationId}`)}>Create Offer</Button>
                </div>
            </div>

            {/* Stats */}
            <div className="grid grid-cols-1 gap-5 sm:grid-cols-3">
                <div className="rounded-lg border border-border bg-card p-5 shadow-sm">
                    <dt className="text-sm font-medium text-muted-foreground">Overall Score</dt>
                    <dd className="mt-1 text-3xl font-semibold text-foreground">
                        {data.overallScore} <span className="text-sm font-normal text-muted-foreground">/ 5.0</span>
                    </dd>
                </div>
                <div className="rounded-lg border border-border bg-card p-5 shadow-sm">
                    <dt className="text-sm font-medium text-muted-foreground">Recommendation</dt>
                    <dd className={`mt-1 text-3xl font-semibold ${recColor}`}>{data.recommendation}</dd>
                </div>
                <div className="rounded-lg border border-border bg-card p-5 shadow-sm">
                    <dt className="text-sm font-medium text-muted-foreground">Interviews Completed</dt>
                    <dd className="mt-1 text-3xl font-semibold text-foreground">
                        {data.interviewsCompleted} <span className="text-sm font-normal text-muted-foreground">/ {data.interviewsTotal}</span>
                    </dd>
                </div>
            </div>

            {/* Feedback */}
            <div className="rounded-lg border border-border bg-card shadow-sm overflow-hidden">
                <div className="border-b border-border px-6 py-4">
                    <h3 className="text-lg font-medium text-foreground">Detailed Feedback</h3>
                    <p className="text-sm text-muted-foreground">Breakdown by interview stage.</p>
                </div>
                <ul className="divide-y divide-border">
                    {data.stages.map((stage) => (
                        <li key={stage.interviewId} className="px-6 py-4 hover:bg-muted/30 transition-colors">
                            <div className="flex items-center justify-between">
                                <div className="flex items-center gap-2">
                                    <p className="text-sm font-medium text-primary">{stage.stageName}</p>
                                    {getStatusBadge(stage.status)}
                                </div>
                                {getScoreBadge(stage.score)}
                            </div>
                            <div className="mt-2 flex flex-col gap-1 sm:flex-row sm:justify-between">
                                <p className="flex items-center text-sm text-muted-foreground">
                                    <User className="mr-1.5 h-4 w-4" /> {stage.interviewerName || "TBD"}
                                </p>
                                <p className="flex items-center text-sm text-muted-foreground">
                                    <Calendar className="mr-1.5 h-4 w-4" /> {stage.date || "Scheduled"}
                                </p>
                            </div>
                            {stage.feedback && (
                                <p className="mt-2 text-sm italic text-muted-foreground">"{stage.feedback}"</p>
                            )}
                        </li>
                    ))}
                </ul>
            </div>
        </div>
    );
}
