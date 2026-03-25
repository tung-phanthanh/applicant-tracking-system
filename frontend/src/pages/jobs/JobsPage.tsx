import { useMemo, useState } from "react";
import JobsTable from "@/pages/jobs/components/JobsTable";
import JobsToolbar from "@/pages/jobs/components/JobsToolbar";
import { useAuth } from "@/hooks/useAuth";
import { useApprovedJobsQuery } from "@/services/jobs/queries";

export default function JobsPage() {
    const { user } = useAuth();
    const { data, isLoading, isError, error } = useApprovedJobsQuery();
    const [search, setSearch] = useState("");
    const [departmentFilter, setDepartmentFilter] = useState("");

    const canPostJob = user?.role === "HR" || user?.role === "HR_MANAGER";

    const filtered = useMemo(() => {
        const list = data ?? [];
        const q = search.trim().toLowerCase();
        return list.filter((job) => {
            const deptOk =
                !departmentFilter ||
                (job.departmentName ?? "").toLowerCase() ===
                    departmentFilter.toLowerCase();
            const searchOk =
                !q ||
                job.title.toLowerCase().includes(q) ||
                (job.description ?? "").toLowerCase().includes(q) ||
                (job.location ?? "").toLowerCase().includes(q);
            return deptOk && searchOk;
        });
    }, [data, search, departmentFilter]);

    if (isLoading) {
        return (
            <div className="flex min-h-[240px] items-center justify-center text-muted-foreground">
                Loading jobs…
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

    return (
        <div className="space-y-8">
            <JobsToolbar
                search={search}
                onSearchChange={setSearch}
                departmentFilter={departmentFilter}
                onDepartmentFilterChange={setDepartmentFilter}
                canPostJob={canPostJob}
            />
            <JobsTable jobs={filtered} />
        </div>
    );
}
