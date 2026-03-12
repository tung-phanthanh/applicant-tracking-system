import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { Briefcase, Search, Plus, Trophy, ChevronRight } from "lucide-react";
import { toast } from "sonner";
import { PageHeader } from "@/components/shared/PageHeader";
import { StatusBadge } from "@/components/shared/StatusBadge";
import { EmptyState } from "@/components/shared/EmptyState";
import { TableSkeleton } from "@/components/shared/LoadingSkeleton";
import { Button } from "@/components/ui/button";
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog";
import { jobService } from "@/services/jobService";
import type { Job } from "@/types/models";

export default function JobsPage() {
    const [jobs, setJobs] = useState<Job[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
    const [isCreating, setIsCreating] = useState(false);
    const [newJobForm, setNewJobForm] = useState({
        title: "",
        department: "Engineering",
        location: "Remote",
        type: "FULL_TIME",
        headcount: 1,
    });

    const loadJobs = () => {
        setIsLoading(true);
        jobService
            .getAllJobs()
            .then(setJobs)
            .catch(() => toast.error("Failed to load jobs"))
            .finally(() => setIsLoading(false));
    };

    useEffect(() => {
        loadJobs();
    }, []);

    const handleCreateJob = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsCreating(true);
        try {
            await jobService.createJob(newJobForm as Partial<Job>);
            toast.success("Job created successfully!");
            setIsCreateModalOpen(false);
            setNewJobForm({
                title: "",
                department: "Engineering",
                location: "Remote",
                type: "FULL_TIME",
                headcount: 1,
            });
            loadJobs();
        } catch (error) {
            console.error("Failed to create job:", error);
            toast.error("Failed to create job.");
        } finally {
            setIsCreating(false);
        }
    };

    return (
        <div className="space-y-6">
            <PageHeader
                title="Jobs"
                description="Manage job postings and candidate rankings"
                icon={Briefcase}
                actions={
                    <Button onClick={() => setIsCreateModalOpen(true)}>
                        <Plus className="mr-1 h-4 w-4" /> New Job
                    </Button>
                }
            />

            <div className="overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm">
                <div className="border-b border-slate-100 p-4">
                    <div className="flex max-w-sm items-center gap-2 rounded-md border border-slate-300 px-3 py-2 text-sm">
                        <Search className="h-4 w-4 text-slate-400" />
                        <input
                            type="text"
                            placeholder="Search jobs..."
                            className="bg-transparent outline-none flex-1 placeholder:text-slate-400"
                        />
                    </div>
                </div>

                {isLoading ? (
                    <div className="p-6">
                        <TableSkeleton rows={4} />
                    </div>
                ) : jobs.length === 0 ? (
                    <EmptyState
                        icon={Briefcase}
                        title="No jobs found"
                        description="Get started by creating a new job posting."
                        action={
                            <Button onClick={() => setIsCreateModalOpen(true)}>
                                <Plus className="mr-1 h-4 w-4" /> Create Job
                            </Button>
                        }
                    />
                ) : (
                    <table className="w-full text-sm">
                        <thead>
                            <tr className="border-b border-slate-100 bg-slate-50 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
                                <th className="px-6 py-3">Job ID</th>
                                <th className="px-6 py-3">Title</th>
                                <th className="px-6 py-3">Headcount</th>
                                <th className="px-6 py-3">Status</th>
                                <th className="px-6 py-3 text-right">Actions</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-slate-100">
                            {jobs.map((job) => (
                                <tr key={job.id} className="hover:bg-slate-50/50 transition-colors">
                                    <td className="px-6 py-4 font-mono text-xs text-slate-500">
                                        #{job.id.toString().padStart(4, "0")}
                                    </td>
                                    <td className="px-6 py-4">
                                        <p className="font-semibold text-slate-800">{job.title}</p>
                                        <p className="text-xs text-slate-500 truncate mt-0.5 max-w-sm">
                                            {job.description}
                                        </p>
                                    </td>
                                    <td className="px-6 py-4 text-slate-600">
                                        {job.headcount || "Unlimited"}
                                    </td>
                                    <td className="px-6 py-4">
                                        <StatusBadge status={job.status} />
                                    </td>
                                    <td className="px-6 py-4 text-right">
                                        <div className="flex justify-end gap-2">
                                            <Button variant="outline" size="sm" asChild>
                                                <Link to={`/jobs/${job.id}/ranking`}>
                                                    <Trophy className="mr-1 h-3.5 w-3.5" /> Ranking
                                                </Link>
                                            </Button>
                                            <Button variant="ghost" size="icon" className="h-8 w-8 text-slate-400 hover:text-slate-600">
                                                <ChevronRight className="h-4 w-4" />
                                            </Button>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                )}
            </div>

            {/* Create Job Dialog */}
            <Dialog open={isCreateModalOpen} onOpenChange={setIsCreateModalOpen}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>Create New Job</DialogTitle>
                        <DialogDescription>
                            Enter the basic details to open a new position.
                        </DialogDescription>
                    </DialogHeader>

                    <form onSubmit={handleCreateJob} className="space-y-4 pt-4">
                        <div className="space-y-2">
                            <label className="text-sm font-medium">Job Title</label>
                            <input
                                type="text"
                                required
                                value={newJobForm.title}
                                onChange={(e) => setNewJobForm({ ...newJobForm, title: e.target.value })}
                                className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm"
                                placeholder="e.g. Senior Frontend Engineer"
                            />
                        </div>

                        <div className="grid grid-cols-2 gap-4">
                            <div className="space-y-2">
                                <label className="text-sm font-medium">Department</label>
                                <select
                                    value={newJobForm.department}
                                    onChange={(e) => setNewJobForm({ ...newJobForm, department: e.target.value })}
                                    className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm"
                                >
                                    <option value="Engineering">Engineering</option>
                                    <option value="Product">Product</option>
                                    <option value="Design">Design</option>
                                    <option value="Sales">Sales</option>
                                    <option value="Marketing">Marketing</option>
                                </select>
                            </div>
                            <div className="space-y-2">
                                <label className="text-sm font-medium">Headcount</label>
                                <input
                                    type="number"
                                    min="1"
                                    required
                                    value={newJobForm.headcount}
                                    onChange={(e) => setNewJobForm({ ...newJobForm, headcount: parseInt(e.target.value) || 1 })}
                                    className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm"
                                />
                            </div>
                        </div>

                        <div className="grid grid-cols-2 gap-4">
                            <div className="space-y-2">
                                <label className="text-sm font-medium">Location</label>
                                <select
                                    value={newJobForm.location}
                                    onChange={(e) => setNewJobForm({ ...newJobForm, location: e.target.value })}
                                    className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm"
                                >
                                    <option value="Remote">Remote</option>
                                    <option value="New York, NY">New York, NY</option>
                                    <option value="San Francisco, CA">San Francisco, CA</option>
                                    <option value="London, UK">London, UK</option>
                                </select>
                            </div>
                            <div className="space-y-2">
                                <label className="text-sm font-medium">Type</label>
                                <select
                                    value={newJobForm.type}
                                    onChange={(e) => setNewJobForm({ ...newJobForm, type: e.target.value })}
                                    className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm"
                                >
                                    <option value="FULL_TIME">Full Time</option>
                                    <option value="PART_TIME">Part Time</option>
                                    <option value="CONTRACT">Contract</option>
                                </select>
                            </div>
                        </div>

                        <DialogFooter className="mt-6">
                            <Button type="button" variant="outline" onClick={() => setIsCreateModalOpen(false)}>
                                Cancel
                            </Button>
                            <Button type="submit" disabled={isCreating}>
                                {isCreating ? "Creating..." : "Create Job"}
                            </Button>
                        </DialogFooter>
                    </form>
                </DialogContent>
            </Dialog>
        </div>
    );
}
