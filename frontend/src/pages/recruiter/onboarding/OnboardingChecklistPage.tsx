import { useState, useEffect, useCallback } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
    CheckSquare,
    ChevronLeft,
    Plus,
    Check,
    Edit2,
    PlayCircle,
    User,
    Calendar,
} from "lucide-react";
import { toast } from "sonner";
import { PageHeader } from "@/components/shared/PageHeader";
import { StatusBadge } from "@/components/shared/StatusBadge";
import { EmptyState } from "@/components/shared/EmptyState";
import { CardSkeleton } from "@/components/shared/LoadingSkeleton";
import { Button } from "@/components/ui/button";
import {
    onboardingService,
    type UpdateItemRequest,
} from "@/services/onboardingService";
import type { OnboardingChecklistResponse, OnboardingItem } from "@/types/models";
import { cn } from "@/lib/utils";

const DEFAULT_TASKS = [
    "Sign employment contract",
    "Create company email account",
    "Setup laptop & development environment",
    "Add to payroll system",
    "Office access card & ID badge",
    "Team introduction meeting",
];

export default function OnboardingChecklistPage() {
    const { applicationId } = useParams<{ applicationId: string }>();
    const navigate = useNavigate();
    const [data, setData] = useState<OnboardingChecklistResponse | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [editingId, setEditingId] = useState<number | null>(null);
    const [editValues, setEditValues] = useState<Partial<UpdateItemRequest>>({});
    const [isCreating, setIsCreating] = useState(false);

    const fetchChecklist = useCallback(async () => {
        if (!applicationId) return;
        setIsLoading(true);
        try {
            const res = await onboardingService.getChecklist(Number(applicationId));
            setData(res);
        } catch {
            // No checklist yet - show creation prompt
            setData(null);
        } finally {
            setIsLoading(false);
        }
    }, [applicationId]);

    useEffect(() => {
        fetchChecklist();
    }, [fetchChecklist]);

    const handleCreateDefault = async () => {
        setIsCreating(true);
        try {
            const res = await onboardingService.createChecklist(
                Number(applicationId),
                {
                    items: DEFAULT_TASKS.map((name) => ({ taskName: name })),
                },
            );
            setData(res);
            toast.success("Onboarding checklist created");
        } catch {
            toast.error("Failed to create checklist");
        } finally {
            setIsCreating(false);
        }
    };

    const handleStatusToggle = async (item: OnboardingItem) => {
        const nextStatus: Record<string, "PENDING" | "IN_PROGRESS" | "DONE"> = {
            PENDING: "IN_PROGRESS",
            IN_PROGRESS: "DONE",
            DONE: "PENDING",
        };
        const newStatus = nextStatus[item.status];
        try {
            await onboardingService.updateItem(item.id, { status: newStatus });
            toast.success(`Task marked as ${newStatus.replace("_", " ")}`);
            fetchChecklist();
        } catch {
            toast.error("Update failed");
        }
    };

    const handleSaveEdit = async (item: OnboardingItem) => {
        try {
            await onboardingService.updateItem(item.id, editValues);
            toast.success("Task updated");
            setEditingId(null);
            fetchChecklist();
        } catch {
            toast.error("Update failed");
        }
    };

    if (isLoading) return <CardSkeleton />;

    return (
        <div className="space-y-6">
            <PageHeader
                title="Onboarding Checklist"
                description="Track candidate onboarding progress"
                icon={CheckSquare}
                actions={
                    <Button variant="outline" onClick={() => navigate(-1)}>
                        <ChevronLeft className="mr-1 h-4 w-4" /> Back
                    </Button>
                }
            />

            {!data ? (
                <EmptyState
                    icon={CheckSquare}
                    title="No onboarding checklist yet"
                    description="Create the default onboarding checklist for this candidate"
                    action={
                        <Button
                            onClick={handleCreateDefault}
                            disabled={isCreating}
                            className="gap-2 bg-indigo-600 hover:bg-indigo-700"
                        >
                            <Plus className="h-4 w-4" />
                            {isCreating ? "Creating..." : "Create Checklist"}
                        </Button>
                    }
                />
            ) : (
                <div className="space-y-4">
                    {/* Progress bar */}
                    <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
                        <div className="mb-3 flex items-center justify-between">
                            <div>
                                <p className="text-sm font-semibold text-slate-800">
                                    {data.candidateName} — Onboarding Progress
                                </p>
                                <p className="text-xs text-slate-500">
                                    {data.completedCount} of {data.totalCount} tasks completed
                                </p>
                            </div>
                            <span
                                className={cn(
                                    "text-2xl font-bold",
                                    data.progressPercent >= 80
                                        ? "text-emerald-600"
                                        : data.progressPercent >= 50
                                            ? "text-indigo-600"
                                            : "text-amber-600",
                                )}
                            >
                                {data.progressPercent}%
                            </span>
                        </div>
                        <div className="h-3 w-full overflow-hidden rounded-full bg-slate-100">
                            <div
                                className={cn(
                                    "h-full rounded-full transition-all duration-500",
                                    data.progressPercent >= 80
                                        ? "bg-emerald-500"
                                        : data.progressPercent >= 50
                                            ? "bg-indigo-500"
                                            : "bg-amber-400",
                                )}
                                style={{ width: `${data.progressPercent}%` }}
                            />
                        </div>
                    </div>

                    {/* Task List */}
                    <div className="overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm">
                        <table className="w-full text-sm">
                            <thead>
                                <tr className="border-b border-slate-100 bg-slate-50">
                                    <th className="w-10 px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500" />
                                    <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
                                        Task
                                    </th>
                                    <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
                                        Assigned To
                                    </th>
                                    <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
                                        Due Date
                                    </th>
                                    <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
                                        Status
                                    </th>
                                    <th className="px-4 py-3 text-right text-xs font-semibold uppercase tracking-wide text-slate-500">
                                        Actions
                                    </th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-slate-100">
                                {data.items.map((item) => (
                                    <tr
                                        key={item.id}
                                        className={cn(
                                            "group transition-colors hover:bg-slate-50/50",
                                            item.status === "DONE" && "opacity-60",
                                        )}
                                    >
                                        {/* Status icon */}
                                        <td className="px-4 py-3">
                                            <button
                                                onClick={() => handleStatusToggle(item)}
                                                title="Click to advance status"
                                                className="flex h-7 w-7 items-center justify-center rounded-full transition-colors"
                                            >
                                                {item.status === "DONE" ? (
                                                    <div className="flex h-6 w-6 items-center justify-center rounded-full bg-emerald-500 text-white">
                                                        <Check className="h-3.5 w-3.5" />
                                                    </div>
                                                ) : item.status === "IN_PROGRESS" ? (
                                                    <PlayCircle className="h-6 w-6 text-indigo-500" />
                                                ) : (
                                                    <div className="h-5 w-5 rounded-full border-2 border-slate-300 hover:border-indigo-400" />
                                                )}
                                            </button>
                                        </td>

                                        {/* Task name */}
                                        <td className="px-4 py-3">
                                            {editingId === item.id ? (
                                                <input
                                                    className="w-full rounded border border-indigo-200 bg-indigo-50 px-2 py-1 text-sm outline-none focus:ring-2 focus:ring-indigo-200"
                                                    defaultValue={item.taskName}
                                                    onChange={(e) =>
                                                        setEditValues((v) => ({
                                                            ...v,
                                                            taskName: e.target.value,
                                                        }))
                                                    }
                                                />
                                            ) : (
                                                <span
                                                    className={cn(
                                                        "font-medium text-slate-800",
                                                        item.status === "DONE" && "line-through",
                                                    )}
                                                >
                                                    {item.taskName}
                                                </span>
                                            )}
                                        </td>

                                        {/* Assigned to */}
                                        <td className="px-4 py-3">
                                            {editingId === item.id ? (
                                                <input
                                                    className="w-full rounded border border-indigo-200 bg-indigo-50 px-2 py-1 text-sm outline-none"
                                                    defaultValue={item.assignedTo ?? ""}
                                                    placeholder="Assignee name"
                                                    onChange={(e) =>
                                                        setEditValues((v) => ({
                                                            ...v,
                                                            assignedTo: e.target.value,
                                                        }))
                                                    }
                                                />
                                            ) : (
                                                <div className="flex items-center gap-1.5 text-slate-600">
                                                    {item.assignedTo ? (
                                                        <>
                                                            <User className="h-3.5 w-3.5 text-slate-400" />
                                                            {item.assignedTo}
                                                        </>
                                                    ) : (
                                                        <span className="italic text-slate-400">Unassigned</span>
                                                    )}
                                                </div>
                                            )}
                                        </td>

                                        {/* Due date */}
                                        <td className="px-4 py-3">
                                            {editingId === item.id ? (
                                                <input
                                                    type="date"
                                                    className="rounded border border-indigo-200 bg-indigo-50 px-2 py-1 text-sm outline-none"
                                                    defaultValue={item.dueDate?.slice(0, 10) ?? ""}
                                                    onChange={(e) =>
                                                        setEditValues((v) => ({
                                                            ...v,
                                                            dueDate: e.target.value,
                                                        }))
                                                    }
                                                />
                                            ) : (
                                                <div className="flex items-center gap-1.5 text-slate-600">
                                                    {item.dueDate ? (
                                                        <>
                                                            <Calendar className="h-3.5 w-3.5 text-slate-400" />
                                                            {new Date(item.dueDate).toLocaleDateString("en-US", {
                                                                month: "short",
                                                                day: "numeric",
                                                            })}
                                                        </>
                                                    ) : (
                                                        <span className="italic text-slate-400">—</span>
                                                    )}
                                                </div>
                                            )}
                                        </td>

                                        {/* Status badge */}
                                        <td className="px-4 py-3">
                                            <StatusBadge status={item.status} />
                                        </td>

                                        {/* Actions */}
                                        <td className="px-4 py-3 text-right">
                                            {editingId === item.id ? (
                                                <div className="flex items-center justify-end gap-1">
                                                    <button
                                                        onClick={() => handleSaveEdit(item)}
                                                        className="rounded-md px-2 py-1 text-xs font-medium text-emerald-600 hover:bg-emerald-50"
                                                    >
                                                        Save
                                                    </button>
                                                    <button
                                                        onClick={() => setEditingId(null)}
                                                        className="rounded-md px-2 py-1 text-xs text-slate-400 hover:bg-slate-100"
                                                    >
                                                        Cancel
                                                    </button>
                                                </div>
                                            ) : (
                                                <button
                                                    onClick={() => {
                                                        setEditingId(item.id);
                                                        setEditValues({});
                                                    }}
                                                    className="rounded-md p-1.5 text-slate-300 opacity-0 transition hover:bg-slate-100 hover:text-indigo-600 group-hover:opacity-100"
                                                >
                                                    <Edit2 className="h-3.5 w-3.5" />
                                                </button>
                                            )}
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}
        </div>
    );
}
