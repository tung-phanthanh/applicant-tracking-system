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
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog";

export default function InterviewsPage() {
    const [interviews, setInterviews] = useState<InterviewResponse[]>([]);
    const [isLoading, setIsLoading] = useState(true);

    const [rescheduleInterview, setRescheduleInterview] = useState<InterviewResponse | null>(null);
    const [rescheduleData, setRescheduleData] = useState({ scheduledAt: "", location: "", type: "Online" });
    const [isRescheduling, setIsRescheduling] = useState(false);

    useEffect(() => {
        loadData();
    }, []);

    const loadData = async () => {
        setIsLoading(true);
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

    const handleRescheduleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!rescheduleInterview) return;
        
        setIsRescheduling(true);
        try {
            await interviewService.reschedule(rescheduleInterview.id, rescheduleData);
            toast.success("Interview rescheduled successfully");
            setRescheduleInterview(null);
            loadData();
        } catch (error) {
            console.error(error);
            toast.error("Failed to reschedule interview");
        } finally {
            setIsRescheduling(false);
        }
    };

    const openRescheduleModal = (interview: InterviewResponse) => {
        setRescheduleInterview(interview);
        // Remove 'Z' if present and take first 16 chars for datetime-local input
        const formattedDate = new Date(interview.scheduledAt).toISOString().slice(0, 16);
        setRescheduleData({
            scheduledAt: formattedDate,
            location: interview.location || "",
            type: interview.type || "Online"
        });
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
                                                    <DropdownMenuItem onClick={() => openRescheduleModal(interview)}>
                                                        Reschedule
                                                    </DropdownMenuItem>
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

            {/* Reschedule Dialog */}
            <Dialog open={!!rescheduleInterview} onOpenChange={(open) => !open && setRescheduleInterview(null)}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>Reschedule Interview</DialogTitle>
                        <DialogDescription>
                            Change the schedule or location for {rescheduleInterview?.candidateName}'s interview.
                        </DialogDescription>
                    </DialogHeader>
                    
                    <form onSubmit={handleRescheduleSubmit} className="space-y-4 py-4">
                        <div className="space-y-2">
                            <label className="text-sm font-medium">New Date & Time</label>
                            <input
                                type="datetime-local"
                                required
                                value={rescheduleData.scheduledAt}
                                onChange={(e) => setRescheduleData({ ...rescheduleData, scheduledAt: e.target.value })}
                                className="w-full flex h-10 rounded-md border border-slate-300 bg-transparent px-3 py-2 text-sm placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-slate-400"
                            />
                        </div>
                        <div className="space-y-2">
                            <label className="text-sm font-medium">Type</label>
                            <select
                                value={rescheduleData.type}
                                onChange={(e) => setRescheduleData({ ...rescheduleData, type: e.target.value })}
                                className="w-full h-10 rounded-md border border-slate-300 bg-transparent px-3 text-sm focus:outline-none focus:ring-2 focus:ring-slate-400"
                            >
                                <option value="Online">Online</option>
                                <option value="Offline">Offline</option>
                            </select>
                        </div>
                        <div className="space-y-2">
                            <label className="text-sm font-medium">Location</label>
                            <input
                                type="text"
                                placeholder="Zoom link or Office Room"
                                value={rescheduleData.location}
                                onChange={(e) => setRescheduleData({ ...rescheduleData, location: e.target.value })}
                                className="w-full flex h-10 rounded-md border border-slate-300 bg-transparent px-3 py-2 text-sm placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-slate-400"
                            />
                        </div>
                        
                        <DialogFooter>
                            <Button type="button" variant="outline" onClick={() => setRescheduleInterview(null)}>
                                Cancel
                            </Button>
                            <Button type="submit" disabled={isRescheduling}>
                                {isRescheduling ? "Saving..." : "Save Changes"}
                            </Button>
                        </DialogFooter>
                    </form>
                </DialogContent>
            </Dialog>
        </div>
    );
}
