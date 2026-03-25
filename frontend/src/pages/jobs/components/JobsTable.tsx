import { Link } from "react-router-dom";
import { ChevronRight } from "lucide-react";
import { formatJobDate } from "@/lib/formatJobDate";
import JobStatusBadge from "@/pages/jobs/components/JobStatusBadge";
import type { JobDTO } from "@/types/job";

interface JobsTableProps {
    jobs: JobDTO[];
}

export default function JobsTable({ jobs }: JobsTableProps) {
    if (jobs.length === 0) {
        return (
            <div className="rounded-lg border border-border bg-card p-12 text-center text-sm text-muted-foreground">
                No jobs match your filters.
            </div>
        );
    }

    return (
        <div className="overflow-hidden rounded-lg border border-border bg-card shadow-sm">
            <div className="overflow-x-auto">
                <table className="min-w-full divide-y divide-border">
                    <thead className="bg-muted/50">
                        <tr>
                            <th
                                scope="col"
                                className="py-3.5 pl-4 pr-3 text-left text-sm font-semibold text-foreground sm:pl-6"
                            >
                                Title
                            </th>
                            <th
                                scope="col"
                                className="px-3 py-3.5 text-left text-sm font-semibold text-foreground"
                            >
                                Department
                            </th>
                            <th
                                scope="col"
                                className="px-3 py-3.5 text-left text-sm font-semibold text-foreground"
                            >
                                Location
                            </th>
                            <th
                                scope="col"
                                className="px-3 py-3.5 text-left text-sm font-semibold text-foreground"
                            >
                                Status
                            </th>
                            <th
                                scope="col"
                                className="px-3 py-3.5 text-left text-sm font-semibold text-foreground"
                            >
                                Posted
                            </th>
                            <th scope="col" className="relative py-3.5 pl-3 pr-4 sm:pr-6">
                                <span className="sr-only">Open</span>
                            </th>
                        </tr>
                    </thead>
                    <tbody className="divide-y divide-border bg-card">
                        {jobs.map((job) => (
                            <tr key={job.id} className="hover:bg-muted/30">
                                <td className="whitespace-nowrap py-4 pl-4 pr-3 text-sm font-medium text-foreground sm:pl-6">
                                    <Link
                                        to={`/jobs/${job.id}`}
                                        className="hover:underline"
                                    >
                                        {job.title}
                                    </Link>
                                </td>
                                <td className="whitespace-nowrap px-3 py-4 text-sm text-muted-foreground">
                                    {job.departmentName ?? "—"}
                                </td>
                                <td className="whitespace-nowrap px-3 py-4 text-sm text-muted-foreground">
                                    {job.location ?? "—"}
                                </td>
                                <td className="whitespace-nowrap px-3 py-4 text-sm">
                                    <JobStatusBadge status={job.status} />
                                </td>
                                <td className="whitespace-nowrap px-3 py-4 text-sm text-muted-foreground">
                                    {formatJobDate(job.createdAt)}
                                </td>
                                <td className="relative whitespace-nowrap py-4 pl-3 pr-4 text-right text-sm sm:pr-6">
                                    <Link
                                        to={`/jobs/${job.id}`}
                                        className="inline-flex items-center gap-1 font-medium text-primary hover:underline"
                                    >
                                        View
                                        <ChevronRight className="h-4 w-4" />
                                    </Link>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
}
