import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { ArrowLeft } from "lucide-react";
import axios from "axios";
import { Button } from "@/components/ui/button";
import JobFormFields from "@/pages/jobs/components/JobFormFields";
import { resolveApiError } from "@/lib/resolveApiError";
import { useAuth } from "@/hooks/useAuth";
import { useJobQuery, useUpdateJobMutation } from "@/services/jobs/queries";
import type { JobFormValues } from "@/types/job";

function jobToFormValues(job: {
    title: string;
    description: string | null;
    location: string | null;
    salary: string | null;
    departmentName: string | null;
}): JobFormValues {
    return {
        title: job.title,
        description: job.description ?? "",
        location: job.location ?? "",
        salary: job.salary ?? "",
        departmentName: job.departmentName ?? "",
    };
}

export default function EditJobPage() {
    const { jobId } = useParams<{ jobId: string }>();
    const navigate = useNavigate();
    const { user } = useAuth();
    const { data: job, isLoading, isError, error } = useJobQuery(jobId);
    const updateMutation = useUpdateJobMutation();
    const [values, setValues] = useState<JobFormValues | null>(null);
    const [apiError, setApiError] = useState("");

    useEffect(() => {
        if (job) {
            setValues(jobToFormValues(job));
        }
    }, [job]);

    const patchValues = (patch: Partial<JobFormValues>) => {
        setValues((prev) => (prev ? { ...prev, ...patch } : prev));
    };

    const handleSave = async () => {
        if (!jobId || !values) return;
        setApiError("");
        if (!values.title.trim()) {
            setApiError("Job title is required.");
            return;
        }
        try {
            await updateMutation.mutateAsync({
                id: jobId,
                payload: {
                    title: values.title.trim(),
                    description: values.description.trim() || undefined,
                    location: values.location.trim() || undefined,
                    salary: values.salary.trim() || undefined,
                    ...(user?.role === "HR" && user.departmentId
                        ? { departmentId: user.departmentId }
                        : {
                              departmentName: values.departmentName.trim() || undefined,
                          }),
                },
            });
            navigate(`/jobs/${jobId}`);
        } catch (err) {
            if (axios.isAxiosError(err) && err.response?.status === 403) {
                setApiError("You do not have permission to edit this job.");
            } else {
                setApiError(resolveApiError(err));
            }
        }
    };

    if (isLoading || !values) {
        return (
            <div className="flex min-h-[240px] items-center justify-center text-muted-foreground">
                Loading job…
            </div>
        );
    }

    if (isError || !job) {
        return (
            <div className="rounded-lg border border-destructive/30 bg-destructive/10 p-6 text-sm text-destructive">
                {error?.message ?? "Job not found."}
            </div>
        );
    }

    return (
        <div className="mx-auto max-w-5xl space-y-6">
            <div className="flex items-center gap-4">
                <Button variant="ghost" size="icon" asChild>
                    <Link to={`/jobs/${job.id}`} aria-label="Back to job">
                        <ArrowLeft className="h-4 w-4" />
                    </Link>
                </Button>
                <div>
                    <h1 className="text-xl font-semibold text-foreground">Edit job</h1>
                    <p className="text-sm text-muted-foreground">{job.title}</p>
                </div>
            </div>

            <div className="rounded-lg border border-border bg-card p-6 shadow-sm sm:p-8">
                {apiError && (
                    <div className="mb-6 rounded-md border border-destructive/30 bg-destructive/10 px-4 py-3 text-sm text-destructive">
                        {apiError}
                    </div>
                )}

                <div className="space-y-6">
                    <div>
                        <h2 className="text-lg font-medium text-foreground">Job details</h2>
                        <p className="mt-1 text-sm text-muted-foreground">
                            Update basic information about the role.
                        </p>
                    </div>
                    <JobFormFields
                        values={values}
                        onChange={patchValues}
                        lockDepartment={user?.role === "HR"}
                        departmentDisplayLabel={user?.department}
                    />
                </div>

                <div className="mt-8 flex flex-wrap justify-end gap-3 border-t border-border pt-6">
                    <Button variant="outline" asChild>
                        <Link to={`/jobs/${job.id}`}>Cancel</Link>
                    </Button>
                    <Button
                        type="button"
                        onClick={() => void handleSave()}
                        disabled={updateMutation.isPending}
                    >
                        {updateMutation.isPending ? "Saving…" : "Save changes"}
                    </Button>
                </div>
            </div>
        </div>
    );
}
