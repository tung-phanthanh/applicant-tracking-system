import type { JobDTO } from "@/types/job";

interface JobDetailOverviewProps {
    job: JobDTO;
}

export default function JobDetailOverview({ job }: JobDetailOverviewProps) {
    return (
        <div className="rounded-lg border border-border bg-card p-6 shadow-sm">
            <h2 className="text-lg font-semibold text-foreground">Description</h2>
            <div className="mt-4 space-y-4 text-sm leading-relaxed text-foreground">
                {job.description ? (
                    <p className="whitespace-pre-wrap">{job.description}</p>
                ) : (
                    <p className="text-muted-foreground">No description provided.</p>
                )}
            </div>
            <dl className="mt-8 grid gap-4 border-t border-border pt-6 sm:grid-cols-2">
                <div>
                    <dt className="text-xs font-medium uppercase tracking-wide text-muted-foreground">
                        Headcount
                    </dt>
                    <dd className="mt-1 text-sm text-foreground">{job.headcount}</dd>
                </div>
                <div>
                    <dt className="text-xs font-medium uppercase tracking-wide text-muted-foreground">
                        Department
                    </dt>
                    <dd className="mt-1 text-sm text-foreground">
                        {job.departmentName ?? "—"}
                    </dd>
                </div>
            </dl>
        </div>
    );
}
