import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
    LayoutTemplate,
    Edit2,
    Archive,
    ArchiveRestore,
    Copy,
    ChevronLeft,
    Calendar,
    Building2,
} from "lucide-react";
import { toast } from "sonner";
import { PageHeader } from "@/components/shared/PageHeader";
import { StatusBadge } from "@/components/shared/StatusBadge";
import { CardSkeleton } from "@/components/shared/LoadingSkeleton";
import { Button } from "@/components/ui/button";
import { scorecardService } from "@/services/scorecardService";
import type { ScorecardTemplate } from "@/types/models";

export default function ScorecardTemplateDetailPage() {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();
    const [template, setTemplate] = useState<ScorecardTemplate | null>(null);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        if (!id) return;
        scorecardService
            .getById(Number(id))
            .then(setTemplate)
            .catch(() => toast.error("Failed to load template"))
            .finally(() => setIsLoading(false));
    }, [id]);

    const handleArchive = async () => {
        if (!template) return;
        try {
            if (template.archived) {
                await scorecardService.unarchive(template.id);
                toast.success("Template restored");
            } else {
                await scorecardService.archive(template.id);
                toast.success("Template archived");
            }
            setTemplate((prev) => prev ? { ...prev, archived: !prev.archived } : prev);
        } catch {
            toast.error("Action failed");
        }
    };

    const handleDuplicate = async () => {
        if (!template) return;
        try {
            await scorecardService.create({
                name: `${template.name} (Copy)`,
                departmentId: template.department?.id,
                criteria: template.criteria.map((c) => ({
                    name: c.name,
                    weight: c.weight,
                })),
            });
            toast.success("Template duplicated successfully");
            navigate("/scorecard-templates");
        } catch {
            toast.error("Duplicate failed");
        }
    };

    if (isLoading) return <CardSkeleton />;
    if (!template) return null;

    const totalWeight = template.criteria.reduce((s, c) => s + c.weight, 0);

    return (
        <div className="space-y-6">
            <PageHeader
                title={template.name}
                description="Scorecard Template Details"
                icon={LayoutTemplate}
                actions={
                    <div className="flex gap-2">
                        <Button
                            variant="outline"
                            onClick={() => navigate("/scorecard-templates")}
                        >
                            <ChevronLeft className="mr-1 h-4 w-4" /> Back
                        </Button>
                        <Button
                            variant="outline"
                            onClick={handleDuplicate}
                            className="gap-2"
                        >
                            <Copy className="h-4 w-4" /> Duplicate
                        </Button>
                        <Button
                            variant="outline"
                            onClick={handleArchive}
                            className="gap-2"
                        >
                            {template.archived ? (
                                <><ArchiveRestore className="h-4 w-4" /> Restore</>
                            ) : (
                                <><Archive className="h-4 w-4" /> Archive</>
                            )}
                        </Button>
                        <Button
                            onClick={() => navigate(`/scorecard-templates/${template.id}/edit`)}
                            className="gap-2 bg-indigo-600 hover:bg-indigo-700"
                        >
                            <Edit2 className="h-4 w-4" /> Edit Template
                        </Button>
                    </div>
                }
            />

            <div className="grid grid-cols-1 gap-6 lg:grid-cols-3">
                {/* Info card */}
                <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm lg:col-span-1 space-y-4">
                    <h3 className="text-xs font-semibold uppercase tracking-wide text-slate-500">
                        Template Info
                    </h3>

                    <div className="space-y-3">
                        <div className="flex items-center gap-2 text-sm text-slate-600">
                            <Building2 className="h-4 w-4 text-slate-400" />
                            <span className="font-medium text-slate-800">Department:</span>
                            {template.department?.name ?? "All Departments"}
                        </div>
                        <div className="flex items-center gap-2 text-sm text-slate-600">
                            <Calendar className="h-4 w-4 text-slate-400" />
                            <span className="font-medium text-slate-800">Created:</span>
                            {new Date(template.createdAt).toLocaleDateString("en-US", {
                                year: "numeric",
                                month: "long",
                                day: "numeric",
                            })}
                        </div>
                        <div className="flex items-center gap-2 text-sm">
                            <span className="font-medium text-slate-800">Status:</span>
                            <StatusBadge status={template.archived ? "INACTIVE" : "ACTIVE"} />
                        </div>
                        <div className="flex items-center gap-2 text-sm text-slate-600">
                            <span className="font-medium text-slate-800">Criteria Count:</span>
                            <span className="inline-flex h-5 w-5 items-center justify-center rounded-full bg-indigo-100 text-xs font-bold text-indigo-700">
                                {template.criteria.length}
                            </span>
                        </div>
                    </div>
                </div>

                {/* Criteria table */}
                <div className="rounded-xl border border-slate-200 bg-white shadow-sm lg:col-span-2">
                    <div className="border-b border-slate-100 px-6 py-4">
                        <h3 className="text-sm font-semibold uppercase tracking-wide text-slate-500">
                            Evaluation Criteria
                        </h3>
                    </div>
                    <table className="w-full text-sm">
                        <thead>
                            <tr className="bg-slate-50 text-xs font-semibold uppercase tracking-wide text-slate-400">
                                <th className="px-6 py-3 text-left">Criterion</th>
                                <th className="px-4 py-3 text-center">Weight</th>
                                <th className="px-4 py-3 text-center">Max Score</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-slate-100">
                            {template.criteria
                                .sort((a, b) => (a.displayOrder ?? 0) - (b.displayOrder ?? 0))
                                .map((criterion) => (
                                    <tr key={criterion.id} className="hover:bg-slate-50/50">
                                        <td className="px-6 py-3 font-medium text-slate-800">
                                            {criterion.name}
                                        </td>
                                        <td className="px-4 py-3 text-center">
                                            <div className="mx-auto flex max-w-[120px] items-center gap-2">
                                                <div className="h-1.5 flex-1 overflow-hidden rounded-full bg-slate-100">
                                                    <div
                                                        className="h-full rounded-full bg-indigo-500"
                                                        style={{ width: `${criterion.weight}%` }}
                                                    />
                                                </div>
                                                <span className="w-12 text-xs font-semibold text-slate-700">
                                                    {criterion.weight}%
                                                </span>
                                            </div>
                                        </td>
                                        <td className="px-4 py-3 text-center text-slate-500">5</td>
                                    </tr>
                                ))}
                        </tbody>
                        <tfoot>
                            <tr className="border-t-2 border-slate-200 bg-slate-50">
                                <td className="px-6 py-3 text-xs font-semibold uppercase text-slate-500">
                                    Total
                                </td>
                                <td className="px-4 py-3 text-center text-sm font-bold text-indigo-700">
                                    {totalWeight}%
                                </td>
                                <td />
                            </tr>
                        </tfoot>
                    </table>
                </div>
            </div>
        </div>
    );
}
