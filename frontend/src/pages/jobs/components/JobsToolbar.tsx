import { Link } from "react-router-dom";
import { Plus, Search } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { cn } from "@/lib/utils";
import { JOB_DEPARTMENT_OPTIONS } from "@/types/job";

interface JobsToolbarProps {
    search: string;
    onSearchChange: (value: string) => void;
    departmentFilter: string;
    onDepartmentFilterChange: (value: string) => void;
    canPostJob: boolean;
}

const selectClassName = cn(
    "h-9 w-full rounded-md border border-input bg-background px-3 text-sm text-foreground shadow-sm sm:w-48",
    "focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring",
);

export default function JobsToolbar({
    search,
    onSearchChange,
    departmentFilter,
    onDepartmentFilterChange,
    canPostJob,
}: JobsToolbarProps) {
    return (
        <div className="space-y-6">
            <div className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
                <div className="sm:flex-auto">
                    <h1 className="text-base font-semibold text-foreground">Job postings</h1>
                    <p className="mt-2 text-sm text-muted-foreground">
                        Approved roles visible to your organization. New postings require HR
                        Manager approval before they appear here.
                    </p>
                </div>
                {canPostJob && (
                    <div className="shrink-0 sm:ml-4">
                        <Button asChild>
                            <Link to="/jobs/create">
                                <Plus className="h-4 w-4" />
                                Post job
                            </Link>
                        </Button>
                    </div>
                )}
            </div>
            <div className="flex flex-col gap-4 rounded-lg border border-border bg-card p-4 shadow-sm sm:flex-row sm:items-center">
                <div className="relative flex-1">
                    <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
                    <Input
                        className="pl-9"
                        placeholder="Search jobs…"
                        value={search}
                        onChange={(e) => onSearchChange(e.target.value)}
                        aria-label="Search jobs"
                    />
                </div>
                <select
                    className={selectClassName}
                    value={departmentFilter}
                    onChange={(e) => onDepartmentFilterChange(e.target.value)}
                    aria-label="Filter by department"
                >
                    <option value="">All departments</option>
                    {JOB_DEPARTMENT_OPTIONS.map((d) => (
                        <option key={d} value={d}>
                            {d}
                        </option>
                    ))}
                </select>
            </div>
        </div>
    );
}
