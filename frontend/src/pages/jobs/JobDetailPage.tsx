import { useState } from "react";
import { Link, useParams } from "react-router-dom";
import { Button } from "@/components/ui/button";
import JobDetailCandidatesPlaceholder from "@/pages/jobs/components/JobDetailCandidatesPlaceholder";
import JobDetailHeader from "@/pages/jobs/components/JobDetailHeader";
import JobDetailOverview from "@/pages/jobs/components/JobDetailOverview";
import JobDetailTabList, {
    type JobDetailTabId,
} from "@/pages/jobs/components/JobDetailTabList";
import { useAuth } from "@/hooks/useAuth";
import { useJobQuery } from "@/services/jobs/queries";

export default function JobDetailPage() {
    const { jobId } = useParams<{ jobId: string }>();
    const { user } = useAuth();
    const { data: job, isLoading, isError, error, refetch } = useJobQuery(jobId);
    const [tab, setTab] = useState<JobDetailTabId>("overview");

    const canEdit = user?.role === "HR" || user?.role === "HR_MANAGER";

    if (isLoading) {
        return (
            <div className="flex min-h-[240px] items-center justify-center text-muted-foreground">
                Loading job…
            </div>
        );
    }

    if (isError || !job) {
        return (
            <div className="space-y-4 rounded-lg border border-destructive/30 bg-destructive/10 p-6">
                <p className="text-sm text-destructive">{error?.message ?? "Job not found."}</p>
                <div className="flex flex-wrap gap-2">
                    <Button variant="outline" size="sm" asChild>
                        <Link to="/jobs">Back to jobs</Link>
                    </Button>
                    <Button
                        variant="ghost"
                        size="sm"
                        type="button"
                        onClick={() => void refetch()}
                    >
                        Retry
                    </Button>
                </div>
            </div>
        );
    }

    return (
        <div className="space-y-6">
            <JobDetailHeader job={job} canEdit={canEdit} />
            <div className="rounded-lg border border-border bg-card shadow-sm">
                <div className="px-4 pt-2 sm:px-6">
                    <JobDetailTabList active={tab} onChange={setTab} />
                </div>
                <div className="p-4 sm:p-6">
                    {tab === "overview" && <JobDetailOverview job={job} />}
                    {tab === "candidates" && <JobDetailCandidatesPlaceholder />}
                </div>
            </div>
        </div>
    );
}
