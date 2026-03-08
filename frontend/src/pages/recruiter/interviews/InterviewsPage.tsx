import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { PageHeader } from "@/components/shared/PageHeader";
import { StatusBadge } from "@/components/shared/StatusBadge";
import { Button } from "@/components/ui/button";
import { CardSkeleton } from "@/components/shared/LoadingSkeleton";
import { interviewService } from "@/services/interviewService";
import type { InterviewResponse } from "@/types/models";
import { CalendarDays, MoreHorizontal, Video } from "lucide-react";
import { toast } from "sonner";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";

export default function InterviewsPage() {
    const [interviews, setInterviews] = useState<InterviewResponse[]>([]);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        loadData();
    }, []);

    const loadData = async () => {
        try {
            const data = await interviewService.getAllInterviews();
            setInterviews(data);
        } catch (error) {
            console.error(error);
            toast.error("Failed to load interviews");
        } finally {
            setIsLoading(false);
        }
    };

    if (isLoading) return <CardSkeleton />;

    return (
        <div className="space-y-6">
            <PageHeader
                title="Interviews"
                description="Manage upcoming and past interviews."
            />

            <div className="rounded-xl border border-slate-200 bg-white shadow-sm overflow-hidden">
                <div className="overflow-x-auto">
                    <table className="w-full text-left text-sm">
                        <thead className="border-b border-slate-200 bg-slate-50 text-slate-500">
                            <tr>
                                <th className="px-6 py-4 font-medium">Candidate</th>
                                <th className="px-6 py-4 font-medium">Position</th>
                                <th className="px-6 py-4 font-medium">Scheduled For</th>
                                <th className="px-6 py-4 font-medium">Status</th>
                                <th className="px-6 py-4 font-medium">Type</th>
                                <th className="px-6 py-4 font-medium text-right">Actions</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-slate-100">
                            {interviews.length === 0 ? (
                                <tr>
                                    <td colSpan={6} className="p-8 text-center text-slate-500">
                                        <CalendarDays className="mx-auto mb-2 h-8 w-8 text-slate-300" />
                                        <p>No interviews found.</p>
                                    </td>
                                </tr>
                            ) : (
                                interviews.map((interview) => (
                                    <tr key={interview.id} className="transition-colors hover:bg-slate-50/50 group">
                                        <td className="px-6 py-4 font-medium text-slate-900">
                                            {interview.candidateName}
                                        </td>
                                        <td className="px-6 py-4 text-slate-600">{interview.jobTitle}</td>
                                        <td className="px-6 py-4 text-slate-900 font-medium whitespace-nowrap">
                                            {new Date(interview.scheduledAt).toLocaleString([], { dateStyle: 'medium', timeStyle: 'short' })}
                                        </td>
                                        <td className="px-6 py-4">
                                            <StatusBadge status={interview.status} />
                                        </td>
                                        <td className="px-6 py-4 text-slate-600">
                                            <div className="flex items-center gap-1.5">
                                                <Video className="h-3.5 w-3.5 text-slate-400" />
                                                <span>{interview.type || 'Online'}</span>
                                            </div>
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
                                                        <Link to={`/interviews/${interview.id}/evaluate`}>Evaluate</Link>
                                                    </DropdownMenuItem>
                                                    <DropdownMenuItem>Reschedule</DropdownMenuItem>
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
