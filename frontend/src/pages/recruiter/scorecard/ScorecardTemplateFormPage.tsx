import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
    LayoutTemplate,
    Plus,
    Trash2,
    GripVertical,
    AlertTriangle,
    Save,
    ChevronLeft,
} from "lucide-react";
import { toast } from "sonner";
import { PageHeader } from "@/components/shared/PageHeader";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";
import { scorecardService } from "@/services/scorecardService";
import type { ScorecardTemplate } from "@/types/models";

interface CriteriaRow {
    id?: number;
    name: string;
    weight: number;
    displayOrder: number;
    isNew?: boolean;
}

export default function ScorecardTemplateFormPage() {
    const { id } = useParams<{ id: string }>();
    const isEdit = Boolean(id);
    const navigate = useNavigate();

    const [templateName, setTemplateName] = useState("");
    const [description, setDescription] = useState("");
    const [criteria, setCriteria] = useState<CriteriaRow[]>([
        { name: "", weight: 100, displayOrder: 1, isNew: true },
    ]);
    const [isLoading, setIsLoading] = useState(isEdit);
    const [isSaving, setIsSaving] = useState(false);
    const [dragIndex, setDragIndex] = useState<number | null>(null);

    // Load existing template for edit
    useEffect(() => {
        if (!isEdit || !id) return;
        scorecardService
            .getById(Number(id))
            .then((t: ScorecardTemplate) => {
                setTemplateName(t.name);
                setCriteria(
                    t.criteria
                        .sort((a, b) => (a.displayOrder ?? 0) - (b.displayOrder ?? 0))
                        .map((c, i) => ({
                            id: c.id,
                            name: c.name,
                            weight: c.weight,
                            displayOrder: i + 1,
                        })),
                );
            })
            .catch(() => toast.error("Failed to load template"))
            .finally(() => setIsLoading(false));
    }, [id, isEdit]);

    const totalWeight = criteria.reduce((sum, c) => sum + Number(c.weight || 0), 0);
    const weightOk = Math.abs(totalWeight - 100) < 0.01;

    const addCriterion = () => {
        setCriteria((prev) => [
            ...prev,
            {
                name: "",
                weight: 0,
                displayOrder: prev.length + 1,
                isNew: true,
            },
        ]);
    };

    const removeCriterion = (index: number) => {
        setCriteria((prev) =>
            prev
                .filter((_, i) => i !== index)
                .map((c, i) => ({ ...c, displayOrder: i + 1 })),
        );
    };

    const updateCriterion = (
        index: number,
        field: keyof CriteriaRow,
        value: string | number,
    ) => {
        setCriteria((prev) =>
            prev.map((c, i) => (i === index ? { ...c, [field]: value } : c)),
        );
    };

    // Drag & Drop handlers
    const handleDragStart = (index: number) => setDragIndex(index);
    const handleDragOver = (e: React.DragEvent, overIndex: number) => {
        e.preventDefault();
        if (dragIndex === null || dragIndex === overIndex) return;
        setCriteria((prev) => {
            const next = [...prev];
            const [moved] = next.splice(dragIndex, 1);
            next.splice(overIndex, 0, moved);
            return next.map((c, i) => ({ ...c, displayOrder: i + 1 }));
        });
        setDragIndex(overIndex);
    };
    const handleDragEnd = () => setDragIndex(null);

    const handleSave = async () => {
        if (!templateName.trim()) {
            toast.error("Template name is required");
            return;
        }
        if (!weightOk) {
            toast.error("Total weight must equal 100%");
            return;
        }

        setIsSaving(true);
        try {
            if (isEdit && id) {
                await scorecardService.update(Number(id), {
                    name: templateName,
                    description: description || undefined,
                });
                // Update each criterion
                await Promise.all(
                    criteria.map((c) =>
                        c.id
                            ? scorecardService.updateCriterion(c.id, {
                                name: c.name,
                                weight: c.weight,
                            })
                            : scorecardService.addCriterion(Number(id), {
                                name: c.name,
                                weight: c.weight,
                            }),
                    ),
                );
                const orderedIds = criteria.filter((c) => c.id).map((c) => c.id!);
                if (orderedIds.length > 0) {
                    await scorecardService.reorderCriteria(Number(id), orderedIds);
                }
                toast.success("Template updated successfully");
            } else {
                await scorecardService.create({
                    name: templateName,
                    description: description || undefined,
                    criteria: criteria.map((c) => ({ name: c.name, weight: c.weight })),
                });
                toast.success("Template created successfully");
            }
            navigate("/scorecard-templates");
        } catch {
            toast.error("Failed to save template");
        } finally {
            setIsSaving(false);
        }
    };

    if (isLoading) {
        return (
            <div className="flex h-48 items-center justify-center">
                <div className="h-8 w-8 animate-spin rounded-full border-4 border-indigo-600 border-t-transparent" />
            </div>
        );
    }

    return (
        <div className="space-y-6">
            <PageHeader
                title={isEdit ? "Edit Template" : "Create Scorecard Template"}
                description={
                    isEdit
                        ? "Update the template details and criteria"
                        : "Define criteria and weights for interview evaluation"
                }
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
                            onClick={handleSave}
                            disabled={isSaving}
                            className="gap-2 bg-indigo-600 hover:bg-indigo-700"
                        >
                            <Save className="h-4 w-4" />
                            {isSaving ? "Saving..." : "Save Template"}
                        </Button>
                    </div>
                }
            />

            <div className="grid grid-cols-1 gap-6 lg:grid-cols-3">
                {/* Left: Template Info */}
                <div className="space-y-4 lg:col-span-1">
                    <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
                        <h3 className="mb-4 text-sm font-semibold uppercase tracking-wide text-slate-500">
                            Template Info
                        </h3>
                        <div className="space-y-4">
                            <div>
                                <label className="mb-1.5 block text-sm font-medium text-slate-700">
                                    Template Name <span className="text-rose-500">*</span>
                                </label>
                                <input
                                    type="text"
                                    value={templateName}
                                    onChange={(e) => setTemplateName(e.target.value)}
                                    placeholder="e.g., Senior Engineer Evaluation"
                                    className="w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm text-slate-800 outline-none transition focus:border-indigo-400 focus:ring-2 focus:ring-indigo-100"
                                />
                            </div>
                            <div>
                                <label className="mb-1.5 block text-sm font-medium text-slate-700">
                                    Description
                                </label>
                                <textarea
                                    value={description}
                                    onChange={(e) => setDescription(e.target.value)}
                                    placeholder="Describe this evaluation template..."
                                    rows={3}
                                    className="w-full resize-none rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm text-slate-800 outline-none transition focus:border-indigo-400 focus:ring-2 focus:ring-indigo-100"
                                />
                            </div>
                        </div>
                    </div>

                    {/* Weight Summary */}
                    <div
                        className={cn(
                            "rounded-xl border p-5 shadow-sm",
                            weightOk
                                ? "border-emerald-200 bg-emerald-50"
                                : "border-amber-200 bg-amber-50",
                        )}
                    >
                        <div className="flex items-center justify-between">
                            <span className="text-sm font-medium text-slate-700">
                                Total Weight
                            </span>
                            <span
                                className={cn(
                                    "text-2xl font-bold",
                                    weightOk ? "text-emerald-600" : "text-amber-600",
                                )}
                            >
                                {totalWeight.toFixed(1)}%
                            </span>
                        </div>

                        {/* Progress bar */}
                        <div className="mt-3 h-2 w-full overflow-hidden rounded-full bg-white/60">
                            <div
                                className={cn(
                                    "h-full rounded-full transition-all",
                                    totalWeight > 100
                                        ? "bg-rose-500"
                                        : weightOk
                                            ? "bg-emerald-500"
                                            : "bg-amber-400",
                                )}
                                style={{ width: `${Math.min(totalWeight, 100)}%` }}
                            />
                        </div>

                        {!weightOk && (
                            <div className="mt-3 flex items-center gap-1.5 text-xs text-amber-700">
                                <AlertTriangle className="h-3.5 w-3.5 shrink-0" />
                                Total weight must equal exactly 100%
                            </div>
                        )}
                    </div>
                </div>

                {/* Right: Criteria */}
                <div className="lg:col-span-2">
                    <div className="rounded-xl border border-slate-200 bg-white shadow-sm">
                        <div className="flex items-center justify-between border-b border-slate-100 px-6 py-4">
                            <h3 className="text-sm font-semibold uppercase tracking-wide text-slate-500">
                                Evaluation Criteria
                            </h3>
                            <Button
                                variant="outline"
                                size="sm"
                                onClick={addCriterion}
                                className="gap-1.5 text-indigo-600 border-indigo-200 hover:bg-indigo-50"
                            >
                                <Plus className="h-3.5 w-3.5" /> Add Criterion
                            </Button>
                        </div>

                        <div className="divide-y divide-slate-100">
                            {/* Column headers */}
                            <div className="grid grid-cols-12 gap-3 px-6 py-2 text-xs font-semibold uppercase tracking-wide text-slate-400">
                                <div className="col-span-1"></div>
                                <div className="col-span-6">Criterion Name</div>
                                <div className="col-span-3 text-center">Weight (%)</div>
                                <div className="col-span-2"></div>
                            </div>

                            {criteria.map((criterion, index) => (
                                <div
                                    key={index}
                                    draggable
                                    onDragStart={() => handleDragStart(index)}
                                    onDragOver={(e) => handleDragOver(e, index)}
                                    onDragEnd={handleDragEnd}
                                    className={cn(
                                        "grid grid-cols-12 items-center gap-3 px-6 py-3 transition-colors",
                                        dragIndex === index && "bg-indigo-50/50",
                                    )}
                                >
                                    <div className="col-span-1 flex cursor-grab items-center text-slate-300 hover:text-slate-500">
                                        <GripVertical className="h-4 w-4" />
                                    </div>
                                    <div className="col-span-6">
                                        <input
                                            type="text"
                                            value={criterion.name}
                                            onChange={(e) =>
                                                updateCriterion(index, "name", e.target.value)
                                            }
                                            placeholder={`Criterion ${index + 1}`}
                                            className="w-full rounded-md border border-slate-200 bg-slate-50 px-3 py-1.5 text-sm text-slate-800 outline-none transition focus:border-indigo-400 focus:bg-white focus:ring-2 focus:ring-indigo-100"
                                        />
                                    </div>
                                    <div className="col-span-3">
                                        <div className="flex items-center gap-1">
                                            <input
                                                type="number"
                                                min={0}
                                                max={100}
                                                value={criterion.weight}
                                                onChange={(e) =>
                                                    updateCriterion(
                                                        index,
                                                        "weight",
                                                        parseFloat(e.target.value) || 0,
                                                    )
                                                }
                                                className="w-full rounded-md border border-slate-200 bg-slate-50 px-3 py-1.5 text-center text-sm font-medium text-slate-800 outline-none transition focus:border-indigo-400 focus:bg-white focus:ring-2 focus:ring-indigo-100"
                                            />
                                            <span className="text-xs text-slate-400">%</span>
                                        </div>
                                    </div>
                                    <div className="col-span-2 flex justify-end">
                                        <button
                                            onClick={() => removeCriterion(index)}
                                            disabled={criteria.length <= 1}
                                            className="rounded-md p-1.5 text-slate-300 transition hover:bg-rose-50 hover:text-rose-500 disabled:cursor-not-allowed disabled:opacity-40"
                                        >
                                            <Trash2 className="h-3.5 w-3.5" />
                                        </button>
                                    </div>
                                </div>
                            ))}

                            {/* Add row */}
                            <div className="px-6 py-3">
                                <button
                                    onClick={addCriterion}
                                    className="flex w-full items-center justify-center gap-2 rounded-lg border-2 border-dashed border-slate-200 py-2.5 text-sm text-slate-400 transition hover:border-indigo-300 hover:text-indigo-500"
                                >
                                    <Plus className="h-4 w-4" />
                                    Add another criterion
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
