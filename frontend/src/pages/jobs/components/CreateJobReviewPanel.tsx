import { AlertTriangle } from "lucide-react";
import type { JobFormValues } from "@/types/job";

interface CreateJobReviewPanelProps {
    values: JobFormValues;
}

export default function CreateJobReviewPanel({ values }: CreateJobReviewPanelProps) {
    return (
        <div className="space-y-6">
            <div>
                <h2 className="text-lg font-medium text-foreground">Review & submit</h2>
                <p className="mt-1 text-sm text-muted-foreground">
                    Confirm details before submitting for HR Manager approval.
                </p>
            </div>
            <div className="space-y-4 rounded-lg border border-border bg-muted/40 p-4">
                <div>
                    <h3 className="text-sm font-medium text-muted-foreground">Job title</h3>
                    <p className="mt-1 text-sm font-semibold text-foreground">
                        {values.title || "—"}
                    </p>
                </div>
                <div>
                    <h3 className="text-sm font-medium text-muted-foreground">Department</h3>
                    <p className="mt-1 text-sm text-foreground">
                        {values.departmentName || "—"}
                    </p>
                </div>
                <div>
                    <h3 className="text-sm font-medium text-muted-foreground">Location</h3>
                    <p className="mt-1 text-sm text-foreground">{values.location || "—"}</p>
                </div>
                <div>
                    <h3 className="text-sm font-medium text-muted-foreground">Salary</h3>
                    <p className="mt-1 text-sm text-foreground">{values.salary || "—"}</p>
                </div>
            </div>
            <div
                className="flex gap-3 rounded-lg border border-border bg-muted/60 p-4 text-sm text-foreground"
                role="status"
            >
                <AlertTriangle className="mt-0.5 h-4 w-4 shrink-0 text-muted-foreground" />
                <p>
                    <span className="font-medium">Note:</span> This posting will be submitted
                    with status <strong>Pending</strong> and will not appear on the public job
                    list until an HR Manager approves it.
                </p>
            </div>
        </div>
    );
}
