import { useState } from "react";
import { Link } from "react-router-dom";
import axios from "axios";
import { Button } from "@/components/ui/button";
import { formatJobDate } from "@/lib/formatJobDate";
import { resolveApiError } from "@/lib/resolveApiError";
import JobStatusBadge from "@/pages/jobs/components/JobStatusBadge";
import {
    useApproveJobMutation,
    usePendingJobsQuery,
    useRejectJobMutation,
} from "@/services/jobs/queries";
import type { JobDTO } from "@/types/job";

export default function PendingJobsPage() {
    const { data, isLoading, isError, error } = usePendingJobsQuery();
    const approveMutation = useApproveJobMutation();
    const rejectMutation = useRejectJobMutation();
    const [banner, setBanner] = useState("");

    const busyId =
        approveMutation.isPending || rejectMutation.isPending
            ? approveMutation.variables ?? rejectMutation.variables
            : null;

    const runApprove = async (job: JobDTO) => {
        setBanner("");
        try {
            await approveMutation.mutateAsync(job.id);
            setBanner(`Approved: ${job.title}`);
        } catch (err) {
            if (axios.isAxiosError(err) && err.response?.status === 403) {
                setBanner("You do not have permission to approve jobs.");
            } else {
                setBanner(resolveApiError(err));
            }
        }
    };

    const runReject = async (job: JobDTO) => {
        setBanner("");
        try {
            await rejectMutation.mutateAsync(job.id);
            setBanner(`Rejected: ${job.title}`);
        } catch (err) {
            if (axios.isAxiosError(err) && err.response?.status === 403) {
                setBanner("You do not have permission to reject jobs.");
            } else {
                setBanner(resolveApiError(err));
            }
        }
    };

    if (isLoading) {
        return (
            <div className="flex min-h-[240px] items-center justify-center text-muted-foreground">
                Loading pending jobs…
            </div>
        );
    }

    if (isError) {
        return (
            <div className="rounded-lg border border-destructive/30 bg-destructive/10 p-6 text-sm text-destructive">
                {error.message}
            </div>
        );
    }

    const jobs = data ?? [];

    return (
        <div className="space-y-6">
            <div>
                <h1 className="text-base font-semibold text-foreground">Pending approvals</h1>
                <p className="mt-2 text-sm text-muted-foreground">
                    Review job postings submitted by recruiters. Approving a job makes it visible
                    on the main job list.
                </p>
            </div>

            {banner && (
                <div className="rounded-md border border-border bg-muted/50 px-4 py-3 text-sm text-foreground">
                    {banner}
                </div>
            )}

            {jobs.length === 0 ? (
                <div className="rounded-lg border border-border bg-card p-10 text-center text-sm text-muted-foreground">
                    No jobs are waiting for approval.
                </div>
            ) : (
                <div className="overflow-hidden rounded-lg border border-border bg-card shadow-sm">
                    <div className="overflow-x-auto">
                        <table className="min-w-full divide-y divide-border">
                            <thead className="bg-muted/50">
                                <tr>
                                    <th className="py-3.5 pl-4 pr-3 text-left text-sm font-semibold text-foreground sm:pl-6">
                                        Title
                                    </th>
                                    <th className="px-3 py-3.5 text-left text-sm font-semibold text-foreground">
                                        Department
                                    </th>
                                    <th className="px-3 py-3.5 text-left text-sm font-semibold text-foreground">
                                        Submitted
                                    </th>
                                    <th className="px-3 py-3.5 text-left text-sm font-semibold text-foreground">
                                        Status
                                    </th>
                                    <th className="relative py-3.5 pl-3 pr-4 text-right text-sm font-semibold text-foreground sm:pr-6">
                                        Actions
                                    </th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-border">
                                {jobs.map((job) => (
                                    <tr key={job.id} className="hover:bg-muted/20">
                                        <td className="py-4 pl-4 pr-3 text-sm font-medium text-foreground sm:pl-6">
                                            <Link
                                                to={`/jobs/${job.id}`}
                                                className="hover:underline"
                                            >
                                                {job.title}
                                            </Link>
                                        </td>
                                        <td className="px-3 py-4 text-sm text-muted-foreground">
                                            {job.departmentName ?? "—"}
                                        </td>
                                        <td className="px-3 py-4 text-sm text-muted-foreground">
                                            {formatJobDate(job.createdAt)}
                                        </td>
                                        <td className="px-3 py-4 text-sm">
                                            <JobStatusBadge status={job.status} />
                                        </td>
                                        <td className="relative whitespace-nowrap py-4 pl-3 pr-4 text-right sm:pr-6">
                                            <div className="flex justify-end gap-2">
                                                <Button
                                                    size="sm"
                                                    variant="default"
                                                    disabled={busyId === job.id}
                                                    onClick={() => void runApprove(job)}
                                                >
                                                    Approve
                                                </Button>
                                                <Button
                                                    size="sm"
                                                    variant="destructive"
                                                    disabled={busyId === job.id}
                                                    onClick={() => void runReject(job)}
                                                >
                                                    Reject
                                                </Button>
                                            </div>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}
        </div>
    );
}
