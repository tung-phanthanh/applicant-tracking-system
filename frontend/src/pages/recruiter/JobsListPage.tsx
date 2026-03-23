import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Briefcase, ArrowRight } from "lucide-react";
import { jobService } from "@/services/jobService";
import type { JobOption } from "@/types/job";
import { Badge } from "@/components/ui/badge";

const STATUS_VARIANT: Record<string, "default" | "secondary" | "outline" | "destructive"> = {
  DRAFT: "secondary",
  PENDING_APPROVAL: "outline",
  APPROVED: "default",
  REJECTED: "destructive",
  CLOSED: "secondary",
};

export default function JobsListPage() {
  const navigate = useNavigate();
  const [jobs, setJobs] = useState<JobOption[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const loadJobs = async () => {
      setLoading(true);
      setError("");
      try {
        const data = await jobService.getJobs();
        setJobs(data);
      } catch {
        setError("Failed to load jobs.");
      } finally {
        setLoading(false);
      }
    };
    void loadJobs();
  }, []);

  if (loading) {
    return (
      <div className="flex h-64 items-center justify-center text-muted-foreground">
        Loading jobs...
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-semibold tracking-tight">Active Jobs</h1>
        <p className="mt-1 text-sm text-muted-foreground">Select a job to view its candidate ranking board.</p>
      </div>

      {error ? (
        <p className="text-sm text-destructive">{error}</p>
      ) : jobs.length === 0 ? (
        <div className="flex h-64 flex-col items-center justify-center rounded-lg border border-border bg-card text-muted-foreground">
          <Briefcase className="mb-3 h-10 w-10 opacity-30" />
          <p className="text-lg font-medium">No active jobs found</p>
        </div>
      ) : (
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
          {jobs.map((job) => (
            <div
              key={job.jobId}
              onClick={() => navigate(`/jobs/${job.jobId}/ranking`)}
              className="group cursor-pointer rounded-lg border border-border bg-card p-5 transition-shadow hover:shadow-md"
            >
              <div className="flex items-start justify-between">
                <div className="flex h-10 w-10 items-center justify-center rounded-md bg-primary/10 text-primary">
                  <Briefcase className="h-5 w-5" />
                </div>
                <Badge variant={STATUS_VARIANT[job.status] || "default"}>{job.status}</Badge>
              </div>
              <h3 className="mt-4 text-lg font-semibold leading-tight text-foreground transition-colors group-hover:text-primary">
                {job.title}
              </h3>
              <div className="mt-4 flex items-center gap-1 text-sm font-medium text-primary">
                View Candidates
                <ArrowRight className="h-4 w-4 transition-transform group-hover:translate-x-1" />
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
