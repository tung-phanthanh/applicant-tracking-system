import { useState, useEffect, useCallback } from "react";
import { Link, useNavigate } from "react-router-dom";
import {
    LayoutTemplate,
    Plus,
    Search,
    MoreHorizontal,
    Edit2,
    Archive,
    Trash2,
    Copy,
    ArchiveRestore,
} from "lucide-react";
import { toast } from "sonner";
import { PageHeader } from "@/components/shared/PageHeader";
import { StatusBadge } from "@/components/shared/StatusBadge";
import { EmptyState } from "@/components/shared/EmptyState";
import { TableSkeleton } from "@/components/shared/LoadingSkeleton";
import { Button } from "@/components/ui/button";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { scorecardService } from "@/services/scorecardService";
import type { ScorecardTemplate } from "@/types/models";

export default function ScorecardTemplateListPage() {
    const navigate = useNavigate();
    const [templates, setTemplates] = useState<ScorecardTemplate[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [searchQuery, setSearchQuery] = useState("");
    const [totalPages, setTotalPages] = useState(1);
    const [page, setPage] = useState(0);

    const fetchTemplates = useCallback(async () => {
        setIsLoading(true);
        try {
            const res = await scorecardService.getAll({ page, size: 10 });
            setTemplates(res.content);
            setTotalPages(res.totalPages);
        } catch {
            toast.error("Failed to load scorecard templates");
        } finally {
            setIsLoading(false);
        }
    }, [page]);

    useEffect(() => {
        fetchTemplates();
    }, [fetchTemplates]);

    const filteredTemplates = templates.filter((t) =>
        t.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
        (t.department?.name ?? "").toLowerCase().includes(searchQuery.toLowerCase()),
    );

    const handleArchive = async (t: ScorecardTemplate) => {
        try {
            if (t.archived) {
                await scorecardService.unarchive(t.id);
                toast.success("Template restored");
            } else {
                await scorecardService.archive(t.id);
                toast.success("Template archived");
            }
            fetchTemplates();
        } catch {
            toast.error("Action failed");
        }
    };

    const handleDelete = async (id: number) => {
        if (!window.confirm("Delete this template permanently?")) return;
        try {
            await scorecardService.delete(id);
            toast.success("Template deleted");
            fetchTemplates();
        } catch {
            toast.error("Delete failed");
        }
    };

    const handleDuplicate = async (t: ScorecardTemplate) => {
        try {
            await scorecardService.create({
                name: `${t.name} (Copy)`,
                departmentId: t.department?.id,
                criteria: t.criteria.map((c) => ({ name: c.name, weight: c.weight })),
            });
            toast.success("Template duplicated");
            fetchTemplates();
        } catch {
            toast.error("Duplicate failed");
        }
    };

    return (
        <div className="space-y-6">
            <PageHeader
                title="Scorecard Templates"
                description="Manage interview evaluation templates for all departments"
                icon={LayoutTemplate}
                actions={
                    <Button
                        onClick={() => navigate("/scorecard-templates/new")}
                        className="gap-2 bg-indigo-600 hover:bg-indigo-700"
                    >
                        <Plus className="h-4 w-4" />
                        Create Template
                    </Button>
                }
            />

            {/* Search & Filter Bar */}
            <div className="flex items-center gap-3">
                <div className="relative flex-1 max-w-sm">
                    <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-400" />
                    <input
                        type="text"
                        placeholder="Search templates..."
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        className="w-full rounded-lg border border-slate-200 bg-white py-2 pl-9 pr-4 text-sm text-slate-700 outline-none ring-0 transition focus:border-indigo-400 focus:ring-2 focus:ring-indigo-100"
                    />
                </div>
            </div>

            {/* Table Card */}
            <div className="overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm">
                {isLoading ? (
                    <div className="p-6">
                        <TableSkeleton rows={5} />
                    </div>
                ) : filteredTemplates.length === 0 ? (
                    <EmptyState
                        icon={LayoutTemplate}
                        title="No templates found"
                        description={
                            searchQuery
                                ? "Try a different search term"
                                : "Create your first scorecard template to get started"
                        }
                        action={
                            !searchQuery && (
                                <Button
                                    onClick={() => navigate("/scorecard-templates/new")}
                                    className="gap-2 bg-indigo-600 hover:bg-indigo-700"
                                >
                                    <Plus className="h-4 w-4" />
                                    Create Template
                                </Button>
                            )
                        }
                    />
                ) : (
                    <table className="w-full text-sm">
                        <thead>
                            <tr className="border-b border-slate-100 bg-slate-50">
                                <th className="px-6 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
                                    Template Name
                                </th>
                                <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
                                    Department
                                </th>
                                <th className="px-4 py-3 text-center text-xs font-semibold uppercase tracking-wide text-slate-500">
                                    Criteria
                                </th>
                                <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
                                    Created
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
                            {filteredTemplates.map((t) => (
                                <tr
                                    key={t.id}
                                    className="group transition-colors hover:bg-indigo-50/30"
                                >
                                    <td className="px-6 py-4">
                                        <Link
                                            to={`/scorecard-templates/${t.id}`}
                                            className="font-medium text-slate-900 hover:text-indigo-600 transition-colors"
                                        >
                                            {t.name}
                                        </Link>
                                    </td>
                                    <td className="px-4 py-4 text-slate-600">
                                        {t.department?.name ?? (
                                            <span className="text-slate-400 italic">All Departments</span>
                                        )}
                                    </td>
                                    <td className="px-4 py-4 text-center">
                                        <span className="inline-flex h-6 w-6 items-center justify-center rounded-full bg-indigo-100 text-xs font-semibold text-indigo-700">
                                            {t.criteria?.length ?? 0}
                                        </span>
                                    </td>
                                    <td className="px-4 py-4 text-slate-500">
                                        {new Date(t.createdAt).toLocaleDateString("en-US", {
                                            year: "numeric",
                                            month: "short",
                                            day: "numeric",
                                        })}
                                    </td>
                                    <td className="px-4 py-4">
                                        <StatusBadge status={t.archived ? "INACTIVE" : "ACTIVE"} />
                                    </td>
                                    <td className="px-4 py-4 text-right">
                                        <DropdownMenu>
                                            <DropdownMenuTrigger asChild>
                                                <button className="rounded-md p-1.5 text-slate-400 opacity-0 transition-all hover:bg-slate-100 hover:text-slate-700 group-hover:opacity-100">
                                                    <MoreHorizontal className="h-4 w-4" />
                                                </button>
                                            </DropdownMenuTrigger>
                                            <DropdownMenuContent align="end" className="w-44">
                                                <DropdownMenuItem
                                                    onClick={() => navigate(`/scorecard-templates/${t.id}/edit`)}
                                                    className="gap-2"
                                                >
                                                    <Edit2 className="h-3.5 w-3.5" /> Edit
                                                </DropdownMenuItem>
                                                <DropdownMenuItem
                                                    onClick={() => handleDuplicate(t)}
                                                    className="gap-2"
                                                >
                                                    <Copy className="h-3.5 w-3.5" /> Duplicate
                                                </DropdownMenuItem>
                                                <DropdownMenuSeparator />
                                                <DropdownMenuItem
                                                    onClick={() => handleArchive(t)}
                                                    className="gap-2"
                                                >
                                                    {t.archived ? (
                                                        <><ArchiveRestore className="h-3.5 w-3.5" /> Restore</>
                                                    ) : (
                                                        <><Archive className="h-3.5 w-3.5" /> Archive</>
                                                    )}
                                                </DropdownMenuItem>
                                                <DropdownMenuItem
                                                    onClick={() => handleDelete(t.id)}
                                                    className="gap-2 text-rose-600 focus:text-rose-700"
                                                >
                                                    <Trash2 className="h-3.5 w-3.5" /> Delete
                                                </DropdownMenuItem>
                                            </DropdownMenuContent>
                                        </DropdownMenu>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                )}

                {/* Pagination */}
                {!isLoading && totalPages > 1 && (
                    <div className="flex items-center justify-between border-t border-slate-100 px-6 py-3">
                        <p className="text-xs text-slate-500">Page {page + 1} of {totalPages}</p>
                        <div className="flex gap-2">
                            <Button
                                variant="outline"
                                size="sm"
                                disabled={page === 0}
                                onClick={() => setPage((p) => p - 1)}
                            >
                                Previous
                            </Button>
                            <Button
                                variant="outline"
                                size="sm"
                                disabled={page >= totalPages - 1}
                                onClick={() => setPage((p) => p + 1)}
                            >
                                Next
                            </Button>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}
