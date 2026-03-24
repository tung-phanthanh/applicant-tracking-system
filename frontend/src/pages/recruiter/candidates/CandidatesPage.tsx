import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { PageHeader } from "@/components/shared/PageHeader";
import { StatusBadge } from "@/components/shared/StatusBadge";
import { Button } from "@/components/ui/button";
import { CardSkeleton } from "@/components/shared/LoadingSkeleton";
import { applicationService } from "@/services/applicationService";
import type { Application } from "@/types/models";
import { Users, MoreHorizontal } from "lucide-react";
import { toast } from "sonner";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";

export default function CandidatesPage() {
    const [applications, setApplications] = useState<Application[]>([]);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        loadData();
    }, []);

    const loadData = async () => {
        try {
            const data = await applicationService.getAllApplications();
            setApplications(data);
        } catch (error) {
            console.error(error);
            toast.error("Failed to load candidates");
        } finally {
            setIsLoading(false);
        }
    };

    if (isLoading) return <CardSkeleton />;

    return (
        <div className="space-y-6">
            <PageHeader
                title="Candidates"
                description="View all active candidates across all open jobs."
            />

            <div className="rounded-xl border border-slate-200 bg-white shadow-sm overflow-hidden">
                <div className="overflow-x-auto">
                    <table className="w-full text-left text-sm">
                        <thead className="border-b border-slate-200 bg-slate-50 text-slate-500">
                            <tr>
                                <th className="px-6 py-4 font-medium">Candidate</th>
                                <th className="px-6 py-4 font-medium">Applied Job</th>
                                <th className="px-6 py-4 font-medium">Stage</th>
                                <th className="px-6 py-4 font-medium">Status</th>
                                <th className="px-6 py-4 font-medium">Applied At</th>
                                <th className="px-6 py-4 font-medium text-right">Actions</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-slate-100">
                            {applications.length === 0 ? (
                                <tr>
                                    <td colSpan={6} className="p-8 text-center text-slate-500">
                                        <Users className="mx-auto mb-2 h-8 w-8 text-slate-300" />
                                        <p>No candidates found.</p>
                                    </td>
                                </tr>
                            ) : (
                                applications.map((app) => (
                                    <tr key={app.id} className="transition-colors hover:bg-slate-50/50 group">
                                        <td className="px-6 py-4 font-medium text-slate-900">
                                            {app.candidateName || `Candidate #${app.candidateId}`}
                                            <div className="text-xs font-normal text-slate-500 mt-0.5">App ID: {app.id}</div>
                                        </td>
                                        <td className="px-6 py-4 text-slate-600">{app.jobTitle || `Job #${app.jobId}`}</td>
                                        <td className="px-6 py-4">
                                            <StatusBadge status={app.stage} />
                                        </td>
                                        <td className="px-6 py-4">
                                            <span className={`inline-flex items-center rounded-full px-2 py-1 text-xs font-medium ${app.status === 'ACTIVE' ? 'bg-emerald-50 text-emerald-700' : 'bg-slate-100 text-slate-700'}`}>
                                                {app.status}
                                            </span>
                                        </td>
                                        <td className="px-6 py-4 text-slate-600">
                                            {app.appliedAt ? new Date(app.appliedAt).toLocaleDateString() : 'N/A'}
                                        </td>
                                        <td className="px-6 py-4 text-right">
                                            <DropdownMenu>
                                                <DropdownMenuTrigger asChild>
                                                    <Button variant="ghost" size="icon" className="h-8 w-8 text-slate-400 group-hover:text-slate-600">
                                                        <MoreHorizontal className="h-4 w-4" />
                                                    </Button>
                                                </DropdownMenuTrigger>
                                                <DropdownMenuContent align="end">
                                                    <DropdownMenuItem asChild>
                                                        <Link to={`/candidates/${app.id}/evaluation`}>View Evaluation</Link>
                                                    </DropdownMenuItem>
                                                    {app.stage === 'OFFER' && (
                                                        <DropdownMenuItem asChild>
                                                            <Link to={`/offers/create/${app.id}`}>Create Offer</Link>
                                                        </DropdownMenuItem>
                                                    )}
                                                </DropdownMenuContent>
                                            </DropdownMenu>
                                        </td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
}
