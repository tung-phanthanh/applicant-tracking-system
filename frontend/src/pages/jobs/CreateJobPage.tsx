import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { ArrowLeft } from "lucide-react";
import axios from "axios";
import { Button } from "@/components/ui/button";
import CreateJobReviewPanel from "@/pages/jobs/components/CreateJobReviewPanel";
import CreateJobStepNav from "@/pages/jobs/components/CreateJobStepNav";
import JobFormFields from "@/pages/jobs/components/JobFormFields";
import { resolveApiError } from "@/lib/resolveApiError";
import { useAuth } from "@/hooks/useAuth";
import { useCreateJobMutation } from "@/services/jobs/queries";
import type { JobFormValues } from "@/types/job";

const TOTAL_STEPS = 2;

const initialValues: JobFormValues = {
    title: "",
    description: "",
    location: "",
    salary: "",
    departmentName: "",
};

export default function CreateJobPage() {
    const navigate = useNavigate();
    const { user } = useAuth();
    const createMutation = useCreateJobMutation();
    const [step, setStep] = useState(1);
    const [values, setValues] = useState<JobFormValues>(initialValues);
    const [apiError, setApiError] = useState("");

    const patchValues = (patch: Partial<JobFormValues>) => {
        setValues((prev) => ({ ...prev, ...patch }));
    };

    const goNext = () => {
        setApiError("");
        if (step < TOTAL_STEPS) {
            setStep((s) => s + 1);
        }
    };

    const goBack = () => {
        setApiError("");
        if (step > 1) {
            setStep((s) => s - 1);
        }
    };

    const handleSubmit = async () => {
        setApiError("");
        if (!values.title.trim()) {
            setApiError("Job title is required.");
            setStep(1);
            return;
        }
        try {
            const isHr = user?.role === "HR";
            const isHrManager = user?.role === "HR_MANAGER";
            if (isHrManager && !values.departmentName.trim()) {
                setApiError("Please select a department.");
                setStep(1);
                return;
            }
            await createMutation.mutateAsync({
                title: values.title.trim(),
                description: values.description.trim() || undefined,
                location: values.location.trim() || undefined,
                salary: values.salary.trim() || undefined,
                ...(isHr && user.departmentId
                    ? { departmentId: user.departmentId }
                    : isHrManager
                      ? { departmentName: values.departmentName.trim() }
                      : { departmentName: values.departmentName.trim() || undefined }),
            });
            navigate("/jobs");
        } catch (err) {
            if (axios.isAxiosError(err) && err.response?.status === 403) {
                setApiError("You do not have permission to create jobs.");
            } else {
                setApiError(resolveApiError(err));
            }
        }
    };

    return (
        <div className="mx-auto max-w-5xl space-y-6">
            <div className="flex items-center gap-4">
                <Button variant="ghost" size="icon" asChild>
                    <Link to="/jobs" aria-label="Back to jobs">
                        <ArrowLeft className="h-4 w-4" />
                    </Link>
                </Button>
                <div>
                    <h1 className="text-xl font-semibold text-foreground">Create new job</h1>
                    <p className="text-sm text-muted-foreground">
                        Enter job details, then review before submitting for approval.
                    </p>
                </div>
            </div>

            <CreateJobStepNav currentStep={step} />

            <div className="rounded-lg border border-border bg-card p-6 shadow-sm sm:p-8">
                {apiError && (
                    <div className="mb-6 rounded-md border border-destructive/30 bg-destructive/10 px-4 py-3 text-sm text-destructive">
                        {apiError}
                    </div>
                )}

                {step === 1 && (
                    <div className="space-y-6">
                        <div>
                            <h2 className="text-lg font-medium text-foreground">Job details</h2>
                            <p className="mt-1 text-sm text-muted-foreground">
                                Basic information about the role.
                            </p>
                        </div>
                        <JobFormFields
                            values={values}
                            onChange={patchValues}
                            hideDepartment={user?.role !== "HR_MANAGER"}
                        />
                    </div>
                )}

                {step === 2 && (
                    <CreateJobReviewPanel
                        values={values}
                        hrDepartmentName={user?.department ?? ""}
                    />
                )}

                <div className="mt-8 flex flex-wrap items-center justify-between gap-3 border-t border-border pt-6">
                    <Button
                        type="button"
                        variant="outline"
                        onClick={goBack}
                        disabled={step === 1 || createMutation.isPending}
                    >
                        Back
                    </Button>
                    <div className="flex flex-wrap gap-3">
                        {step < TOTAL_STEPS && (
                            <Button type="button" onClick={goNext}>
                                Next
                            </Button>
                        )}
                        {step === TOTAL_STEPS && (
                            <Button
                                type="button"
                                onClick={() => void handleSubmit()}
                                disabled={createMutation.isPending}
                            >
                                {createMutation.isPending ? "Submitting…" : "Submit for approval"}
                            </Button>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}
