import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { ClipboardCheck, Send } from "lucide-react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { interviewService } from "@/services/interviewService";
import { scorecardService } from "@/services/scorecardService";
import type { InterviewScorecard, ScoreEntry } from "@/types/interview";
import type { ScorecardTemplate } from "@/types/scorecard";
import { useAuth } from "@/hooks/useAuth";

export default function InterviewScorecardPage() {
  const { interviewId } = useParams<{ interviewId: string }>();
  const { user } = useAuth();
  const [scorecards, setScorecards] = useState<InterviewScorecard[]>([]);
  const [myScorecard, setMyScorecard] = useState<InterviewScorecard | null>(null);
  const [templates, setTemplates] = useState<ScorecardTemplate[]>([]);
  const [selectedTemplate, setSelectedTemplate] = useState<ScorecardTemplate | null>(null);
  const [scores, setScores] = useState<ScoreEntry[]>([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");
  const [submitted, setSubmitted] = useState(false);

  useEffect(() => {
    if (!interviewId) return;
    const load = async () => {
      setLoading(true);
      try {
        const [allScorecards, allTemplates] = await Promise.all([
          interviewService.getAllScorecards(interviewId),
          scorecardService.getAll(),
        ]);
        setScorecards(allScorecards);
        setTemplates(allTemplates);

        try {
          const mine = await interviewService.getMyScorecard(interviewId);
          setMyScorecard(mine);
          setSubmitted(true);
        } catch {
          setMyScorecard(null);
          setSubmitted(false);
        }
      } catch {
        setError("Failed to load scorecard data.");
      } finally {
        setLoading(false);
      }
    };
    void load();
  }, [interviewId]);

  const selectTemplate = (tpl: ScorecardTemplate) => {
    setSelectedTemplate(tpl);
    setScores(
      tpl.criteria.map((c) => ({
        criterionId: c.id,
        score: 5,
        comment: "",
      })),
    );
  };

  const updateScore = (index: number, field: "score" | "comment", value: string | number) => {
    const updated = [...scores];
    updated[index] = { ...updated[index], [field]: value };
    setScores(updated);
  };

  const handleSubmit = async () => {
    if (!interviewId) return;
    setSubmitting(true);
    try {
      await interviewService.submitScores(interviewId, { scores });
      setSubmitted(true);
      const mine = await interviewService.getMyScorecard(interviewId);
      setMyScorecard(mine);
      const allScorecards = await interviewService.getAllScorecards(interviewId);
      setScorecards(allScorecards);
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : "Failed to submit scores.";
      alert(msg);
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div className="flex h-64 items-center justify-center text-muted-foreground">
        Loading scorecard data...
      </div>
    );
  }

  if (error) {
    return <p className="py-8 text-center text-sm text-destructive">{error}</p>;
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-semibold tracking-tight">Interview Scorecard</h1>
        <p className="mt-1 text-sm text-muted-foreground">
          Interview ID: {interviewId}
        </p>
      </div>

      {/* Submit Score Section */}
      {!submitted && (
        <section className="rounded-lg border border-border bg-card p-6 shadow-sm">
          <h3 className="mb-4 text-lg font-semibold text-card-foreground">Submit Your Scores</h3>

          {!selectedTemplate ? (
            <div>
              <p className="mb-3 text-sm text-muted-foreground">
                Select a template to start scoring:
              </p>
              <div className="grid grid-cols-1 gap-3 md:grid-cols-2 lg:grid-cols-3">
                {templates.map((tpl) => (
                  <button
                    key={tpl.id}
                    onClick={() => selectTemplate(tpl)}
                    className="rounded-lg border border-border p-4 text-left transition-colors hover:bg-muted/30"
                  >
                    <p className="font-medium">{tpl.name}</p>
                    <p className="text-sm text-muted-foreground">
                      {tpl.criteria.length} criteria
                    </p>
                  </button>
                ))}
              </div>
              {templates.length === 0 && (
                <p className="text-sm text-muted-foreground">No templates available.</p>
              )}
            </div>
          ) : (
            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <p className="text-sm font-medium">
                  Using template: <span className="text-primary">{selectedTemplate.name}</span>
                </p>
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => setSelectedTemplate(null)}
                >
                  Change Template
                </Button>
              </div>

              <div className="space-y-3">
                {selectedTemplate.criteria.map((criterion, i) => (
                  <div
                    key={criterion.id}
                    className="rounded-lg border border-border p-4"
                  >
                    <div className="mb-2 flex items-center justify-between">
                      <p className="font-medium">{criterion.name}</p>
                      <Badge variant="outline">Weight: {criterion.weight}</Badge>
                    </div>
                    <div className="flex items-center gap-4">
                      <div className="flex items-center gap-2">
                        <label className="text-sm text-muted-foreground">Score (1-10):</label>
                        <Input
                          type="number"
                          min={1}
                          max={10}
                          value={scores[i]?.score ?? 5}
                          onChange={(e) =>
                            updateScore(i, "score", Math.min(10, Math.max(1, parseInt(e.target.value) || 1)))
                          }
                          className="w-20"
                        />
                      </div>
                      <Input
                        value={scores[i]?.comment ?? ""}
                        onChange={(e) => updateScore(i, "comment", e.target.value)}
                        placeholder="Comment (optional)"
                        className="flex-1"
                      />
                    </div>
                  </div>
                ))}
              </div>

              <div className="flex justify-end">
                <Button onClick={handleSubmit} disabled={submitting}>
                  <Send className="h-4 w-4" />
                  {submitting ? "Submitting..." : "Submit Scores"}
                </Button>
              </div>
            </div>
          )}
        </section>
      )}

      {/* My Scorecard */}
      {myScorecard && (
        <section className="rounded-lg border border-border bg-card p-6 shadow-sm">
          <h3 className="mb-4 text-lg font-semibold text-card-foreground">
            <ClipboardCheck className="mb-0.5 mr-2 inline-block h-5 w-5 text-green-600" />
            Your Scorecard
          </h3>
          <ScorecardView scorecard={myScorecard} />
        </section>
      )}

      {/* All Scorecards */}
      <section className="rounded-lg border border-border bg-card p-6 shadow-sm">
        <h3 className="mb-4 text-lg font-semibold text-card-foreground">All Scorecards</h3>
        {scorecards.length === 0 ? (
          <p className="text-sm text-muted-foreground">No scores submitted yet.</p>
        ) : (
          <div className="space-y-4">
            {scorecards.map((sc) => (
              <div key={sc.participantUserId} className="rounded-lg border border-border p-4">
                <div className="mb-3 flex items-center justify-between">
                  <p className="font-medium">{sc.participantName}</p>
                  <Badge
                    variant="outline"
                    className={
                      sc.overallScore != null && sc.overallScore >= 7
                        ? "bg-green-50 text-green-700 ring-1 ring-green-700/10"
                        : sc.overallScore != null && sc.overallScore >= 4
                          ? "bg-yellow-50 text-yellow-800 ring-1 ring-yellow-600/20"
                          : "bg-red-50 text-red-700 ring-1 ring-red-700/10"
                    }
                  >
                    Overall: {sc.overallScore ?? "—"}
                  </Badge>
                </div>
                <ScorecardView scorecard={sc} />
              </div>
            ))}
          </div>
        )}
      </section>
    </div>
  );
}

function ScorecardView({ scorecard }: { scorecard: InterviewScorecard }) {
  if (scorecard.scores.length === 0) {
    return <p className="text-sm text-muted-foreground">No detailed scores.</p>;
  }
  return (
    <div className="overflow-hidden rounded-lg border border-border">
      <table className="min-w-full divide-y divide-border">
        <thead className="bg-muted/30">
          <tr>
            <th className="px-4 py-2 text-left text-sm font-semibold">Criterion</th>
            <th className="px-4 py-2 text-left text-sm font-semibold">Weight</th>
            <th className="px-4 py-2 text-left text-sm font-semibold">Score</th>
            <th className="px-4 py-2 text-left text-sm font-semibold">Comment</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-border">
          {scorecard.scores.map((s) => (
            <tr key={s.criterionId}>
              <td className="px-4 py-2 text-sm">{s.criterionName}</td>
              <td className="px-4 py-2 text-sm text-muted-foreground">{s.weight}</td>
              <td className="px-4 py-2 text-sm font-medium">{s.score}</td>
              <td className="px-4 py-2 text-sm text-muted-foreground">
                {s.comment || "—"}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
