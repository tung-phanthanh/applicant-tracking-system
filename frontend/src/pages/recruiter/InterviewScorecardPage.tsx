import { useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Send, RefreshCw } from "lucide-react";
import { Button } from "@/components/ui/button";

interface CriterionEntry {
    criterionId: string;
    criterionName: string;
    score: number;
    comment: string;
}

// Mock criteria for demo — in production, load from template via API
const MOCK_CRITERIA: CriterionEntry[] = [
    { criterionId: "1", criterionName: "Algorithm & Data Structures", score: 0, comment: "" },
    { criterionId: "2", criterionName: "System Design", score: 0, comment: "" },
    { criterionId: "3", criterionName: "Communication Skills", score: 0, comment: "" },
    { criterionId: "4", criterionName: "Problem Solving", score: 0, comment: "" },
];

export default function InterviewScorecardPage() {
    const { interviewId } = useParams<{ interviewId: string }>();
    const navigate = useNavigate();
    const [criteria, setCriteria] = useState<CriterionEntry[]>(MOCK_CRITERIA);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [submitted, setSubmitted] = useState(false);

    const updateScore = (idx: number, score: number) =>
        setCriteria(criteria.map((c, i) => (i === idx ? { ...c, score } : c)));

    const updateComment = (idx: number, comment: string) =>
        setCriteria(criteria.map((c, i) => (i === idx ? { ...c, comment } : c)));

    const handleSubmit = async () => {
        setIsSubmitting(true);
        try {
            // In production: await scorecardService.submitScores({ interviewId, scores: criteria });
            await new Promise((r) => setTimeout(r, 800));
            setSubmitted(true);
        } finally {
            setIsSubmitting(false);
        }
    };

    if (submitted) {
        return (
            <div className="flex flex-col items-center justify-center py-20 space-y-4">
                <div className="flex h-16 w-16 items-center justify-center rounded-full bg-green-100">
                    <Send className="h-8 w-8 text-green-600" />
                </div>
                <h2 className="text-xl font-semibold text-foreground">Scorecard Submitted</h2>
                <p className="text-muted-foreground">Your feedback has been recorded.</p>
                <Button variant="outline" onClick={() => navigate(-1)}>Go Back</Button>
            </div>
        );
    }

    return (
        <div className="mx-auto max-w-3xl space-y-6">
            <div>
                <h1 className="text-xl font-semibold text-foreground">Interview Scorecard</h1>
                <p className="text-sm text-muted-foreground">
                    Interview #{interviewId?.slice(0, 8)} — Rate each criterion from 1–5.
                </p>
            </div>

            <div className="rounded-lg border border-border bg-card shadow-sm divide-y divide-border">
                {criteria.map((c, idx) => (
                    <div key={c.criterionId} className="p-6 space-y-3">
                        <h3 className="text-sm font-semibold text-foreground">{c.criterionName}</h3>

                        {/* Rating Stars */}
                        <div className="flex items-center gap-1">
                            {[1, 2, 3, 4, 5].map((val) => (
                                <button
                                    key={val}
                                    type="button"
                                    className={`flex h-10 w-10 items-center justify-center rounded-lg border text-sm font-semibold transition-colors ${
                                        c.score >= val
                                            ? "border-primary bg-primary text-primary-foreground"
                                            : "border-border bg-background text-muted-foreground hover:border-primary/50"
                                    }`}
                                    onClick={() => updateScore(idx, val)}
                                >
                                    {val}
                                </button>
                            ))}
                            <span className="ml-3 text-sm text-muted-foreground">
                                {c.score > 0 ? `${c.score}/5` : "Not rated"}
                            </span>
                        </div>

                        {/* Comment */}
                        <textarea
                            className="w-full rounded-md border border-input bg-background px-3 py-2 text-sm placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring"
                            rows={2}
                            placeholder="Add comments…"
                            value={c.comment}
                            onChange={(e) => updateComment(idx, e.target.value)}
                        />
                    </div>
                ))}
            </div>

            <div className="flex justify-end gap-3">
                <Button variant="outline" onClick={() => navigate(-1)}>Cancel</Button>
                <Button onClick={handleSubmit} disabled={isSubmitting || criteria.every((c) => c.score === 0)}>
                    {isSubmitting ? (
                        <><RefreshCw className="mr-1.5 h-4 w-4 animate-spin" /> Submitting…</>
                    ) : (
                        <><Send className="mr-1.5 h-4 w-4" /> Submit Scorecard</>
                    )}
                </Button>
            </div>
        </div>
    );
}
