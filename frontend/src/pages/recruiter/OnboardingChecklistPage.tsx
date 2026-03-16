import { useState, useEffect, useCallback } from "react";
import { useParams } from "react-router-dom";
import { CheckSquare, Plus, User, Calendar, RefreshCw, AlertTriangle, ChevronLeft, ChevronRight } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { onboardingService } from "@/services/onboardingService";
import type { OnboardingProgress, OnboardingTask } from "@/types/onboarding";

export default function OnboardingChecklistPage() {
    const { applicationId } = useParams<{ applicationId: string }>();
    const [progress, setProgress] = useState<OnboardingProgress | null>(null);
    const [tasks, setTasks] = useState<OnboardingTask[]>([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const PAGE_SIZE = 20;

    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState("");
    const [showAddForm, setShowAddForm] = useState(false);
    const [newTitle, setNewTitle] = useState("");
    const [newCategory, setNewCategory] = useState("Before Start Date");
    const [actionLoading, setActionLoading] = useState<string | null>(null);

    const loadProgress = useCallback(async () => {
        if (!applicationId) return;
        try {
            const data = await onboardingService.getProgress(applicationId);
            setProgress(data);
        } catch { /* progress is secondary */ }
    }, [applicationId]);

    const loadTasks = useCallback(async (p = 0) => {
        if (!applicationId) return;
        setIsLoading(true);
        setError("");
        try {
            const result = await onboardingService.getTasksPaged(applicationId, p, PAGE_SIZE);
            setTasks(result.content);
            setTotalPages(result.totalPages);
            setPage(result.number);
        } catch {
            setError("Failed to load tasks. Is the backend running?");
        } finally {
            setIsLoading(false);
        }
    }, [applicationId]);

    useEffect(() => {
        loadProgress();
        loadTasks(0);
    }, [loadProgress, loadTasks]);

    const toggleTask = async (id: string) => {
        setActionLoading(id);
        try {
            const updated = await onboardingService.toggleTask(id);
            setTasks(tasks.map((t) => (t.id === id ? updated : t)));
            loadProgress(); // refresh progress bar
        } catch {
            setError("Failed to update task.");
        } finally {
            setActionLoading(null);
        }
    };

    const addTask = async () => {
        if (!newTitle.trim() || !applicationId) return;
        try {
            await onboardingService.createTask({
                applicationId,
                title: newTitle,
                category: newCategory,
                assignedTo: "",
                dueDate: "",
            });
            setNewTitle("");
            setShowAddForm(false);
            loadTasks(0);
            loadProgress();
        } catch {
            setError("Failed to add task.");
        }
    };

    const percent = progress?.progressPercent ?? 0;
    const completed = progress?.completedTasks ?? 0;
    const total = progress?.totalTasks ?? 0;

    // Group current page tasks by category
    const categories = [...new Set(tasks.map((t) => t.category))];

    return (
        <div className="space-y-6">
            {/* Header */}
            <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
                <div className="flex items-center gap-3">
                    <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary/10 text-primary">
                        <CheckSquare className="h-5 w-5" />
                    </div>
                    <div>
                        <h1 className="text-xl font-semibold text-foreground">Onboarding Checklist</h1>
                        <p className="text-sm text-muted-foreground">Application: {applicationId?.slice(0, 8)}…</p>
                    </div>
                </div>
                <Button variant="outline" size="sm" onClick={() => setShowAddForm(!showAddForm)}>
                    <Plus className="mr-1.5 h-4 w-4" /> Add Task
                </Button>
            </div>

            {error && (
                <div className="flex items-center gap-3 rounded-lg border border-destructive/30 bg-destructive/10 px-4 py-3">
                    <AlertTriangle className="h-4 w-4 text-destructive" />
                    <p className="text-sm text-destructive">{error}</p>
                </div>
            )}

            {/* Progress bar */}
            {progress !== null && (
                <div className="rounded-lg border border-border bg-card p-5 shadow-sm">
                    <div className="flex items-center justify-between text-sm mb-2">
                        <span className="font-medium text-foreground">Overall Progress</span>
                        <span className="text-muted-foreground">{completed}/{total} tasks ({percent}%)</span>
                    </div>
                    <div className="h-3 w-full rounded-full bg-muted">
                        <div className="h-3 rounded-full bg-primary transition-all" style={{ width: `${percent}%` }} />
                    </div>
                </div>
            )}

            {/* Add form */}
            {showAddForm && (
                <div className="flex flex-wrap gap-3 rounded-lg border border-border bg-card p-4 shadow-sm">
                    <Input className="flex-1 min-w-48" placeholder="Task title"
                        value={newTitle} onChange={(e) => setNewTitle(e.target.value)} />
                    <select className="h-10 rounded-md border border-input bg-background px-3 py-2 text-sm"
                        value={newCategory} onChange={(e) => setNewCategory(e.target.value)}>
                        <option>Before Start Date</option>
                        <option>Day 1</option>
                        <option>Week 1</option>
                    </select>
                    <Button onClick={addTask}>Add</Button>
                    <Button variant="outline" onClick={() => setShowAddForm(false)}>Cancel</Button>
                </div>
            )}

            {/* Tasks */}
            {isLoading ? (
                <div className="flex items-center justify-center py-20 text-muted-foreground">
                    <RefreshCw className="mr-2 h-5 w-5 animate-spin" /> Loading…
                </div>
            ) : (
                <>
                    {categories.map((cat) => (
                        <div key={cat} className="rounded-lg border border-border bg-card shadow-sm overflow-hidden">
                            <div className="px-6 py-4 border-b border-border bg-muted/50">
                                <h3 className="text-sm font-semibold text-foreground">{cat}</h3>
                            </div>
                            <ul className="divide-y divide-border">
                                {tasks.filter((t) => t.category === cat).map((t) => (
                                    <li key={t.id}
                                        className={`flex items-center gap-4 px-6 py-3.5 transition-colors hover:bg-muted/30 ${t.completed ? "opacity-60" : ""}`}>
                                        <input type="checkbox" checked={t.completed}
                                            disabled={actionLoading === t.id}
                                            onChange={() => toggleTask(t.id)}
                                            className="h-4 w-4 rounded border-gray-300 text-primary focus:ring-primary" />
                                        {actionLoading === t.id && <RefreshCw className="h-4 w-4 animate-spin text-muted-foreground" />}
                                        <span className={`flex-1 text-sm ${t.completed ? "line-through text-muted-foreground" : "text-foreground"}`}>
                                            {t.title}
                                        </span>
                                        {t.assignedTo && (
                                            <span className="flex items-center gap-1 text-xs text-muted-foreground">
                                                <User className="h-3 w-3" /> {t.assignedTo}
                                            </span>
                                        )}
                                        {t.dueDate && (
                                            <span className="flex items-center gap-1 text-xs text-muted-foreground">
                                                <Calendar className="h-3 w-3" /> {t.dueDate}
                                            </span>
                                        )}
                                    </li>
                                ))}
                            </ul>
                        </div>
                    ))}

                    {/* Pagination */}
                    {totalPages > 1 && (
                        <div className="flex items-center justify-between pt-2">
                            <p className="text-sm text-muted-foreground">Page {page + 1} of {totalPages}</p>
                            <div className="flex gap-1">
                                <Button variant="outline" size="sm" disabled={page === 0} onClick={() => loadTasks(page - 1)}>
                                    <ChevronLeft className="h-4 w-4" />
                                </Button>
                                <Button variant="outline" size="sm" disabled={page >= totalPages - 1} onClick={() => loadTasks(page + 1)}>
                                    <ChevronRight className="h-4 w-4" />
                                </Button>
                            </div>
                        </div>
                    )}
                </>
            )}
        </div>
    );
}
