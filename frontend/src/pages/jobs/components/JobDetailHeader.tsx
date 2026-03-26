import { Link } from "react-router-dom";
import { Edit, Plus } from "lucide-react";
import { Button } from "@/components/ui/button";
import { formatJobDate } from "@/lib/formatJobDate";
import JobStatusBadge from "@/pages/jobs/components/JobStatusBadge";
import type { JobDTO } from "@/types/job";

interface JobDetailHeaderProps {
    job: JobDTO;
    canEdit: boolean;
}

export default function JobDetailHeader({ job, canEdit }: JobDetailHeaderProps) {
    const metaParts = [
        job.departmentName ?? "Department TBD",
        job.location ?? "Location TBD",
        `Posted ${formatJobDate(job.createdAt)}`,
    ];

    return (
        <div className="rounded-lg border border-border bg-card shadow-sm">
            <div className="flex flex-col gap-4 px-4 py-5 sm:flex-row sm:items-start sm:justify-between sm:px-6">
                <div className="min-w-0">
                    <h1 className="text-2xl font-bold text-foreground">{job.title}</h1>
                    <p className="mt-1 max-w-2xl text-sm text-muted-foreground">
                        {metaParts.join(" · ")}
                    </p>
                    <div className="mt-3 flex flex-wrap items-center gap-2">
                        <JobStatusBadge status={job.status} />
                        {job.salary && (
                            <span className="text-sm text-muted-foreground">{job.salary}</span>
                        )}
                    </div>
                </div>
                <div className="flex flex-wrap gap-2">
                    {canEdit && (
                        <Button variant="outline" asChild>
                            <Link to={`/jobs/${job.id}/edit`}>
                                <Edit className="h-4 w-4" />
                                Edit
                            </Link>
                        </Button>
                    )}
                    <Button type="button" variant="secondary" disabled>
                        <Plus className="h-4 w-4" />
                        Add candidate
                    </Button>
                </div>
            </div>
        </div>
    );
}
