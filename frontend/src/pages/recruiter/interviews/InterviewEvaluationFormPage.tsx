import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
    ClipboardList,
    ChevronLeft,
    Send,
    Save,
    CheckCircle2,
    User,
    Briefcase,
    Calendar,
    Video,
} from "lucide-react";
import { toast } from "sonner";
import { PageHeader } from "@/components/shared/PageHeader";
import { StatusBadge } from "@/components/shared/StatusBadge";
import { ScoreSelector } from "@/components/shared/ScoreSelector";
import { CardSkeleton } from "@/components/shared/LoadingSkeleton";
import { Button } from "@/components/ui/button";
import { interviewService, type SubmitScoresBody } from "@/services/interviewService";
import type { HiringRecommendation } from "@/types/models";
import { cn } from "@/lib/utils";

const RECOMMENDATION_OPTIONS: { value: HiringRecommendation; label: string; color: string }[] = [
    { value: "STRONG_HIRE", label: "Strong Hire", color: "bg-emerald-500 text-white border-emerald-600" },
    { value: "HIRE", label: "Hire", color: "bg-emerald-100 text-emerald-800 border-emerald-300" },
    { value: "NEUTRAL", label: "Neutral", color: "bg-slate-100 text-slate-700 border-slate-300" },
    { value: "NO_HIRE", label: "No Hire", color: "bg-rose-100 text-rose-700 border-rose-300" },
    { value: "STRONG_NO_HIRE", label: "Strong No Hire", color: "bg-rose-500 text-white border-rose-600" },
];

// Internal interview state shape (not a mock - populated from API)
interface InterviewDisplay {
    id: number;
    candidateName: string;
    jobTitle: string;
    interviewType: "ONLINE" | "OFFLINE";
    scheduledAt: string;
    interviewerName: string;
    criteria: { id: number; name: string; weight: number }[];
}

export default function InterviewEvaluationFormPage() {
    const { interviewId } = useParams<{ interviewId: string }>();
    const navigate = useNavigate();

    const [isLoading, setIsLoading] = useState(true);
    const [isSaving, setIsSaving] = useState(false);
    const [isSubmitted, setIsSubmitted] = useState(false);
    const [interview, setInterview] = useState<InterviewDisplay | null>(null);

    // Score state
    const [scores, setScores] = useState<Record<number, number>>({});
    const [comments, setComments] = useState<Record<number, string>>({});
    const [strengths, setStrengths] = useState("");
    const [weaknesses, setWeaknesses] = useState("");
    const [generalComment, setGeneralComment] = useState("");
    const [recommendation, setRecommendation] = useState<HiringRecommendation | "">("");

    useEffect(() => {
        if (!interviewId) return;
        const numId = Number(interviewId);
        // Load interview info and existing scores in parallel
        Promise.allSettled([
            interviewService.getInterviewById(numId),
            interviewService.getMyScores(numId),
        ]).then(([interviewResult, scoresResult]) => {
            // Handle interview detail
            if (interviewResult.status === "fulfilled") {
                const iv = interviewResult.value;
                setInterview({
                    id: numId,
                    candidateName: iv.candidateName ?? "Candidate",
                    jobTitle: iv.jobTitle ?? "Position",
                    interviewType: (iv.type === "On-site" ? "OFFLINE" : "ONLINE"),
                    scheduledAt: iv.scheduledAt ?? new Date().toISOString(),
                    interviewerName: iv.participant || "You",
                    criteria: [],
                });
            } else {
                setInterview({
                    id: numId,
                    candidateName: "Candidate",
                    jobTitle: "Position",
                    interviewType: "ONLINE",
                    scheduledAt: new Date().toISOString(),
                    interviewerName: "You",
                    criteria: [],
                });
            }

            // Handle existing scores
            if (scoresResult.status === "fulfilled" && scoresResult.value) {
                const res = scoresResult.value;
                if (res.submitted) setIsSubmitted(true);
                setRecommendation(res.recommendation ?? "");
                setStrengths(res.strengths ?? "");
                setWeaknesses(res.weaknesses ?? "");
                setGeneralComment(res.generalComment ?? "");
                const scoreMap: Record<number, number> = {};
                const commentMap: Record<number, string> = {};
                res.scores.forEach((s) => {
                    scoreMap[s.criterionId] = s.score;
                    commentMap[s.criterionId] = s.comment ?? "";
                });
                setScores(scoreMap);
                setComments(commentMap);
                // Update criteria from response scores
                setInterview((prev) => prev ? {
                    ...prev,
                    criteria: res.scores.map((s) => ({
                        id: s.criterionId,
                        name: s.criterionName,
                        weight: 0,
                    })),
                } : prev);
            } else {
                // No scores yet – use default criteria
                setInterview((prev) => prev ? {
                    ...prev,
                    criteria: [
                        { id: 1, name: "Technical Skills", weight: 30 },
                        { id: 2, name: "Problem Solving", weight: 30 },
                        { id: 3, name: "Communication", weight: 20 },
                        { id: 4, name: "Cultural Fit", weight: 20 },
                    ],
                } : prev);
            }
        }).finally(() => setIsLoading(false));
    }, [interviewId]);

    const allScored = interview?.criteria.every((c) => scores[c.id] !== undefined) ?? false;

    const buildPayload = (): SubmitScoresBody => ({
        recommendation: recommendation || undefined,
        strengths: strengths || undefined,
        weaknesses: weaknesses || undefined,
        generalComment: generalComment || undefined,
        scores: (interview?.criteria ?? []).map((c) => ({
            criterionId: c.id,
            score: scores[c.id] ?? 0,
            comment: comments[c.id] || undefined,
        })),
    });

    const handleSaveDraft = async () => {
        setIsSaving(true);
        try {
            await interviewService.submitScores(Number(interviewId), buildPayload());
            toast.success("Draft saved");
        } catch {
            toast.error("Failed to save");
        } finally {
            setIsSaving(false);
        }
    };

    const handleSubmit = async () => {
        if (!allScored) {
            toast.error("Please score all criteria before submitting");
            return;
        }
        if (!recommendation) {
            toast.error("Please select a hiring recommendation");
            return;
        }
        setIsSaving(true);
        try {
            await interviewService.submitScores(Number(interviewId), buildPayload());
            setIsSubmitted(true);
            toast.success("Evaluation submitted successfully!");
        } catch {
            toast.error("Submission failed");
        } finally {
            setIsSaving(false);
        }
    };

    if (isLoading) return <CardSkeleton />;
    if (!interview) return null;

    return (
        <div className="space-y-6">
            <PageHeader
                title="Interview Evaluation"
                description="Rate this candidate based on the interview scorecard"
                icon={ClipboardList}
                actions={
                    <Button
                        variant="outline"
                        onClick={() => navigate(-1)}
                    >
                        <ChevronLeft className="mr-1 h-4 w-4" /> Back
                    </Button>
                }
            />

            {/* Submitted Banner */}
            {isSubmitted && (
                <div className="flex items-center gap-3 rounded-xl border border-emerald-200 bg-emerald-50 px-5 py-4">
                    <CheckCircle2 className="h-5 w-5 shrink-0 text-emerald-600" />
                    <div>
                        <p className="text-sm font-semibold text-emerald-800">
                            Evaluation Submitted
                        </p>
                        <p className="text-xs text-emerald-600">
                            Your evaluation has been locked and submitted for review.
                        </p>
                    </div>
                    <div className="ml-auto">
                        <StatusBadge status="COMPLETED" />
                    </div>
                </div>
            )}

            {/* Candidate Info Header */}
            <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
                <div className="grid grid-cols-2 gap-4 sm:grid-cols-4">
                    <div className="flex items-center gap-2">
                        <div className="flex h-8 w-8 items-center justify-center rounded-full bg-indigo-100">
                            <User className="h-4 w-4 text-indigo-600" />
                        </div>
                        <div>
                            <p className="text-xs text-slate-400">Candidate</p>
                            <p className="text-sm font-semibold text-slate-800">{interview.candidateName}</p>
                        </div>
                    </div>
                    <div className="flex items-center gap-2">
                        <div className="flex h-8 w-8 items-center justify-center rounded-full bg-purple-100">
                            <Briefcase className="h-4 w-4 text-purple-600" />
                        </div>
                        <div>
                            <p className="text-xs text-slate-400">Position</p>
                            <p className="text-sm font-semibold text-slate-800">{interview.jobTitle}</p>
                        </div>
                    </div>
                    <div className="flex items-center gap-2">
                        <div className="flex h-8 w-8 items-center justify-center rounded-full bg-sky-100">
                            <Video className="h-4 w-4 text-sky-600" />
                        </div>
                        <div>
                            <p className="text-xs text-slate-400">Type</p>
                            <StatusBadge status={interview.interviewType} />
                        </div>
                    </div>
                    <div className="flex items-center gap-2">
                        <div className="flex h-8 w-8 items-center justify-center rounded-full bg-amber-100">
                            <Calendar className="h-4 w-4 text-amber-600" />
                        </div>
                        <div>
                            <p className="text-xs text-slate-400">Date</p>
                            <p className="text-sm font-semibold text-slate-800">
                                {new Date(interview.scheduledAt).toLocaleDateString("en-US", {
                                    month: "short",
                                    day: "numeric",
                                    year: "numeric",
                                })}
                            </p>
                        </div>
                    </div>
                </div>
            </div>

            {/* Scoring Section */}
            <div className="rounded-xl border border-slate-200 bg-white shadow-sm">
                <div className="border-b border-slate-100 px-6 py-4">
                    <h3 className="text-sm font-semibold uppercase tracking-wide text-slate-500">
                        Criteria Scoring
                    </h3>
                </div>
                <div className="divide-y divide-slate-100">
                    {interview.criteria.map((criterion) => (
                        <div key={criterion.id} className="px-6 py-5 space-y-3">
                            <div className="flex items-center justify-between">
                                <div>
                                    <h4 className="text-sm font-semibold text-slate-800">
                                        {criterion.name}
                                    </h4>
                                    {criterion.weight > 0 && (
                                        <p className="text-xs text-slate-400">Weight: {criterion.weight}%</p>
                                    )}
                                </div>
                                <ScoreSelector
                                    value={scores[criterion.id] ?? 0}
                                    onChange={(score) =>
                                        setScores((prev) => ({ ...prev, [criterion.id]: score }))
                                    }
                                    readonly={isSubmitted}
                                />
                            </div>
                            <textarea
                                value={comments[criterion.id] ?? ""}
                                onChange={(e) =>
                                    setComments((prev) => ({
                                        ...prev,
                                        [criterion.id]: e.target.value,
                                    }))
                                }
                                disabled={isSubmitted}
                                placeholder="Add a comment for this criterion..."
                                rows={2}
                                className="w-full resize-none rounded-lg border border-slate-200 bg-slate-50 px-3 py-2 text-sm text-slate-700 outline-none transition focus:border-indigo-400 focus:bg-white focus:ring-2 focus:ring-indigo-100 disabled:cursor-not-allowed disabled:opacity-60"
                            />
                        </div>
                    ))}
                </div>
            </div>

            {/* Additional Feedback */}
            <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm space-y-4">
                <h3 className="text-sm font-semibold uppercase tracking-wide text-slate-500">
                    Additional Feedback
                </h3>
                <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                    <div>
                        <label className="mb-1.5 block text-sm font-medium text-slate-700">
                            Strengths
                        </label>
                        <textarea
                            value={strengths}
                            onChange={(e) => setStrengths(e.target.value)}
                            disabled={isSubmitted}
                            placeholder="What are the candidate's key strengths?"
                            rows={3}
                            className="w-full resize-none rounded-lg border border-slate-200 bg-slate-50 px-3 py-2 text-sm text-slate-700 outline-none transition focus:border-indigo-400 focus:bg-white focus:ring-2 focus:ring-indigo-100 disabled:opacity-60"
                        />
                    </div>
                    <div>
                        <label className="mb-1.5 block text-sm font-medium text-slate-700">
                            Weaknesses / Concerns
                        </label>
                        <textarea
                            value={weaknesses}
                            onChange={(e) => setWeaknesses(e.target.value)}
                            disabled={isSubmitted}
                            placeholder="Any concerns or areas for improvement?"
                            rows={3}
                            className="w-full resize-none rounded-lg border border-slate-200 bg-slate-50 px-3 py-2 text-sm text-slate-700 outline-none transition focus:border-indigo-400 focus:bg-white focus:ring-2 focus:ring-indigo-100 disabled:opacity-60"
                        />
                    </div>
                </div>
                <div>
                    <label className="mb-1.5 block text-sm font-medium text-slate-700">
                        General Comment
                    </label>
                    <textarea
                        value={generalComment}
                        onChange={(e) => setGeneralComment(e.target.value)}
                        disabled={isSubmitted}
                        placeholder="Overall impression of the candidate..."
                        rows={3}
                        className="w-full resize-none rounded-lg border border-slate-200 bg-slate-50 px-3 py-2 text-sm text-slate-700 outline-none transition focus:border-indigo-400 focus:bg-white focus:ring-2 focus:ring-indigo-100 disabled:opacity-60"
                    />
                </div>
            </div>

            {/* Hiring Recommendation */}
            <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
                <h3 className="mb-4 text-sm font-semibold uppercase tracking-wide text-slate-500">
                    Hiring Recommendation
                </h3>
                <div className="flex flex-wrap gap-2">
                    {RECOMMENDATION_OPTIONS.map((opt) => (
                        <button
                            key={opt.value}
                            type="button"
                            disabled={isSubmitted}
                            onClick={() => setRecommendation(opt.value)}
                            className={cn(
                                "rounded-full border-2 px-4 py-1.5 text-sm font-medium transition-all",
                                recommendation === opt.value
                                    ? opt.color
                                    : "border-slate-200 bg-white text-slate-600 hover:border-slate-300",
                                isSubmitted && "cursor-not-allowed opacity-80",
                            )}
                        >
                            {opt.label}
                        </button>
                    ))}
                </div>
            </div>

            {/* Action Buttons */}
            {!isSubmitted && (
                <div className="flex items-center justify-end gap-3 pb-4">
                    <Button
                        variant="outline"
                        onClick={() => navigate(-1)}
                    >
                        Cancel
                    </Button>
                    <Button
                        variant="outline"
                        onClick={handleSaveDraft}
                        disabled={isSaving}
                        className="gap-2"
                    >
                        <Save className="h-4 w-4" />
                        Save Draft
                    </Button>
                    <Button
                        onClick={handleSubmit}
                        disabled={isSaving || !allScored}
                        className="gap-2 bg-indigo-600 hover:bg-indigo-700"
                    >
                        <Send className="h-4 w-4" />
                        Submit Evaluation
                    </Button>
                </div>
            )}
        </div>
    );
}
