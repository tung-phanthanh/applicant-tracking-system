import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { cn } from "@/lib/utils";
import { JOB_DEPARTMENT_OPTIONS, type JobFormValues } from "@/types/job";

interface JobFormFieldsProps {
    values: JobFormValues;
    onChange: (patch: Partial<JobFormValues>) => void;
    disabled?: boolean;
    className?: string;
    /** Omit department row (e.g. create job — department set from HR on review/submit). */
    hideDepartment?: boolean;
    /** HR cannot choose another department — show read-only department. */
    lockDepartment?: boolean;
    /** Label shown when department is locked (falls back to values.departmentName). */
    departmentDisplayLabel?: string;
}

const selectClassName = cn(
    "flex h-9 w-full rounded-md border border-input bg-background px-3 py-1 text-sm text-foreground shadow-sm",
    "focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring",
    "disabled:cursor-not-allowed disabled:opacity-50",
);

export default function JobFormFields({
    values,
    onChange,
    disabled,
    className,
    hideDepartment,
    lockDepartment,
    departmentDisplayLabel,
}: JobFormFieldsProps) {
    return (
        <div className={cn("grid grid-cols-1 gap-6 sm:grid-cols-6", className)}>
            <div className="sm:col-span-4">
                <Label htmlFor="job-title">Job title</Label>
                <Input
                    id="job-title"
                    className="mt-1"
                    value={values.title}
                    onChange={(e) => onChange({ title: e.target.value })}
                    placeholder="e.g. Senior Frontend Developer"
                    disabled={disabled}
                />
            </div>
            {!hideDepartment && (
                <div className="sm:col-span-3">
                    <Label htmlFor="job-department">Department</Label>
                    {lockDepartment ? (
                        <Input
                            id="job-department"
                            className="mt-1"
                            readOnly
                            value={
                                departmentDisplayLabel?.trim() ||
                                values.departmentName ||
                                "—"
                            }
                            disabled={disabled}
                            aria-readonly="true"
                        />
                    ) : (
                        <select
                            id="job-department"
                            className={cn("mt-1", selectClassName)}
                            value={values.departmentName}
                            onChange={(e) => onChange({ departmentName: e.target.value })}
                            disabled={disabled}
                        >
                            <option value="">Select department</option>
                            {JOB_DEPARTMENT_OPTIONS.map((d) => (
                                <option key={d} value={d}>
                                    {d}
                                </option>
                            ))}
                        </select>
                    )}
                </div>
            )}
            <div className="sm:col-span-3">
                <Label htmlFor="job-location">Location</Label>
                <Input
                    id="job-location"
                    className="mt-1"
                    value={values.location}
                    onChange={(e) => onChange({ location: e.target.value })}
                    placeholder="e.g. Remote / Ho Chi Minh City"
                    disabled={disabled}
                />
            </div>
            <div className="sm:col-span-3">
                <Label htmlFor="job-salary">Salary</Label>
                <Input
                    id="job-salary"
                    className="mt-1"
                    value={values.salary}
                    onChange={(e) => onChange({ salary: e.target.value })}
                    placeholder="e.g. Competitive / 20–30M VND"
                    disabled={disabled}
                />
            </div>
            <div className="sm:col-span-6">
                <Label htmlFor="job-description">Description</Label>
                <textarea
                    id="job-description"
                    rows={5}
                    className={cn(
                        "mt-1 min-h-[120px] w-full rounded-md border border-input bg-transparent px-3 py-2 text-sm text-foreground shadow-sm",
                        "placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring",
                        "disabled:cursor-not-allowed disabled:opacity-50",
                    )}
                    value={values.description}
                    onChange={(e) => onChange({ description: e.target.value })}
                    disabled={disabled}
                />
            </div>
        </div>
    );
}
