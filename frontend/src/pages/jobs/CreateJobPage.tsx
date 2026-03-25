import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { ArrowLeft } from "lucide-react";
import axios from "axios";
import { Button } from "@/components/ui/button";
import CreateJobReviewPanel from "@/pages/jobs/components/CreateJobReviewPanel";
import CreateJobStaticStep from "@/pages/jobs/components/CreateJobStaticStep";
import CreateJobStepNav from "@/pages/jobs/components/CreateJobStepNav";
import JobFormFields from "@/pages/jobs/components/JobFormFields";
import { resolveApiError } from "@/lib/resolveApiError";
import { useCreateJobMutation } from "@/services/jobs/queries";
import type { JobFormValues } from "@/types/job";

const TOTAL_STEPS = 4;

const initialValues: JobFormValues = {
    title: "",
    description: "",
    location: "",
    salary: "",
    departmentName: "",
};

export default function CreateJobPage() {
    const navigate = useNavigate();
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
            await createMutation.mutateAsync({
                title: values.title.trim(),
                description: values.description.trim() || undefined,
                location: values.location.trim() || undefined,
                salary: values.salary.trim() || undefined,
                departmentName: values.departmentName.trim() || undefined,
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
                        Multi-step posting aligned with your ATS workflow.
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
                        <JobFormFields values={values} onChange={patchValues} />
                    </div>
                )}

                {step === 2 && (
                    <CreateJobStaticStep
                        title="Requirements"
                        description="Define skills and experience (detailed screening criteria will tie into the candidates module later)."
                    >
                        <p className="text-sm text-muted-foreground">
                            For now, include must-have skills and seniority in the job
                            description on step 1. Structured skills and experience bands will
                            be added in a follow-up iteration.
                        </p>
                    </CreateJobStaticStep>
                )}

                {step === 3 && (
                    <CreateJobStaticStep
                        title="Hiring workflow"
                        description="Default pipeline stages apply to this posting."
                    >
                        <ul className="list-inside list-disc space-y-2 text-sm text-muted-foreground">
                            <li>Screening</li>
                            <li>Technical interview</li>
                            <li>Culture fit</li>
                        </ul>
                    </CreateJobStaticStep>
                )}

                {step === 4 && <CreateJobReviewPanel values={values} />}

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
