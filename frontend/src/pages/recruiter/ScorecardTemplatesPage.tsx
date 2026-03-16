import { useState, useEffect, useCallback } from "react";
import { Plus, Code, Users, Briefcase, Trash2, RefreshCw, AlertTriangle, ChevronLeft, ChevronRight } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { scorecardService } from "@/services/scorecardService";
import type { ScorecardTemplate, CreateScorecardTemplatePayload } from "@/types/scorecard";

const ICON_COLORS = [
    { bg: "bg-blue-100", text: "text-blue-600" },
    { bg: "bg-purple-100", text: "text-purple-600" },
    { bg: "bg-green-100", text: "text-green-600" },
    { bg: "bg-amber-100", text: "text-amber-600" },
];

const icons = [Code, Users, Briefcase];

export default function ScorecardTemplatesPage() {
    const [templates, setTemplates] = useState<ScorecardTemplate[]>([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);
    const PAGE_SIZE = 12;

    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState("");
    const [showForm, setShowForm] = useState(false);
    const [editingId, setEditingId] = useState<string | null>(null);
    const [formName, setFormName] = useState("");
    const [formCriteria, setFormCriteria] = useState<{ name: string; weight: number }[]>([
        { name: "", weight: 1 },
    ]);

    const loadTemplates = useCallback(async (p = 0) => {
        setIsLoading(true);
        setError("");
        try {
            const result = await scorecardService.getTemplates(p, PAGE_SIZE);
            setTemplates(result.content);
            setTotalPages(result.totalPages);
            setTotalElements(result.totalElements);
            setPage(result.number);
        } catch {
            setError("Failed to load templates. Is the backend running?");
        } finally {
            setIsLoading(false);
        }
    }, []);

    useEffect(() => { loadTemplates(0); }, [loadTemplates]);

    const handleSave = async () => {
        const payload: CreateScorecardTemplatePayload = {
            name: formName,
            criteria: formCriteria.filter((c) => c.name.trim()),
        };
        try {
            if (editingId) {
                await scorecardService.updateTemplate(editingId, payload);
            } else {
                await scorecardService.createTemplate(payload);
            }
            resetForm();
            loadTemplates(page);
        } catch {
            setError("Failed to save template.");
        }
    };

    const handleEdit = (template: ScorecardTemplate) => {
        setEditingId(template.id);
        setFormName(template.name);
        setFormCriteria(template.criteria.map((c) => ({ name: c.name, weight: c.weight })));
        setShowForm(true);
    };

    const handleDelete = async (id: string) => {
        try {
            await scorecardService.deleteTemplate(id);
            loadTemplates(page);
        } catch {
            setError("Failed to delete template.");
        }
    };

    const resetForm = () => {
        setShowForm(false);
        setEditingId(null);
        setFormName("");
        setFormCriteria([{ name: "", weight: 1 }]);
    };

    const addCriterion = () => setFormCriteria([...formCriteria, { name: "", weight: 1 }]);
    const removeCriterion = (i: number) => setFormCriteria(formCriteria.filter((_, idx) => idx !== i));
    const updateCriterion = (i: number, field: "name" | "weight", value: string | number) =>
        setFormCriteria(formCriteria.map((c, idx) => (idx === i ? { ...c, [field]: value } : c)));

    return (
        <div className="space-y-6">
            {/* Header */}
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-xl font-semibold text-foreground">Scorecard Templates</h1>
                    <p className="text-sm text-muted-foreground">
                        {totalElements} template{totalElements !== 1 ? "s" : ""} total
                    </p>
                </div>
                <Button size="sm" onClick={() => { resetForm(); setShowForm(true); }}>
                    <Plus className="mr-1.5 h-4 w-4" /> Create Template
                </Button>
            </div>

            {error && (
                <div className="flex items-center gap-3 rounded-lg border border-destructive/30 bg-destructive/10 px-4 py-3">
                    <AlertTriangle className="h-4 w-4 text-destructive" />
                    <p className="text-sm text-destructive">{error}</p>
                </div>
            )}

            {/* Grid */}
            {isLoading ? (
                <div className="flex items-center justify-center py-20 text-muted-foreground">
                    <RefreshCw className="mr-2 h-5 w-5 animate-spin" /> Loading…
                </div>
            ) : (
                <>
                    <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
                        {templates.map((t, idx) => {
                            const Icon = icons[idx % icons.length];
                            const color = ICON_COLORS[idx % ICON_COLORS.length];
                            return (
                                <div
                                    key={t.id}
                                    className="relative flex items-center space-x-3 rounded-lg border border-border bg-card px-6 py-5 shadow-sm hover:border-primary/30 transition-colors cursor-pointer"
                                    onClick={() => handleEdit(t)}
                                >
                                    <div className="flex-shrink-0">
                                        <div className={`flex h-10 w-10 items-center justify-center rounded-full ${color.bg}`}>
                                            <Icon className={`h-5 w-5 ${color.text}`} />
                                        </div>
                                    </div>
                                    <div className="min-w-0 flex-1">
                                        <p className="text-sm font-medium text-foreground">{t.name}</p>
                                        <p className="truncate text-sm text-muted-foreground">
                                            {t.departmentName ?? "All departments"} · {t.criteria.length} criteria
                                        </p>
                                    </div>
                                    <Button
                                        variant="ghost" size="sm" className="h-8 w-8 p-0 z-10"
                                        onClick={(e) => { e.stopPropagation(); handleDelete(t.id); }}
                                    >
                                        <Trash2 className="h-4 w-4 text-muted-foreground hover:text-destructive" />
                                    </Button>
                                </div>
                            );
                        })}
                    </div>

                    {/* Pagination */}
                    {totalPages > 1 && (
                        <div className="flex items-center justify-between pt-2">
                            <p className="text-sm text-muted-foreground">
                                Page {page + 1} of {totalPages}
                            </p>
                            <div className="flex gap-1">
                                <Button variant="outline" size="sm" disabled={page === 0}
                                    onClick={() => loadTemplates(page - 1)}>
                                    <ChevronLeft className="h-4 w-4" />
                                </Button>
                                <Button variant="outline" size="sm" disabled={page >= totalPages - 1}
                                    onClick={() => loadTemplates(page + 1)}>
                                    <ChevronRight className="h-4 w-4" />
                                </Button>
                            </div>
                        </div>
                    )}
                </>
            )}

            {/* Editor Form */}
            {showForm && (
                <div className="rounded-lg border border-border bg-card p-6 shadow-sm">
                    <h3 className="text-base font-semibold text-foreground">
                        {editingId ? "Edit Template" : "Create Template"}
                    </h3>
                    <div className="mt-5 space-y-6">
                        <div className="max-w-md">
                            <label className="block text-sm font-medium text-muted-foreground">Template Name</label>
                            <Input className="mt-1" placeholder="e.g., Backend Technical Interview"
                                value={formName} onChange={(e) => setFormName(e.target.value)} />
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-muted-foreground">Criteria</label>
                            <div className="mt-2 space-y-2">
                                {formCriteria.map((c, i) => (
                                    <div key={i} className="flex items-center gap-2">
                                        <Input className="flex-1" placeholder="Criterion name"
                                            value={c.name} onChange={(e) => updateCriterion(i, "name", e.target.value)} />
                                        <select className="h-10 rounded-md border border-input bg-background px-3 py-2 text-sm"
                                            value={c.weight} onChange={(e) => updateCriterion(i, "weight", Number(e.target.value))}>
                                            <option value={1}>Weight 1</option>
                                            <option value={1.5}>Weight 1.5</option>
                                            <option value={2}>Weight 2</option>
                                        </select>
                                        <Button variant="ghost" size="sm" className="h-8 w-8 p-0 text-destructive"
                                            onClick={() => removeCriterion(i)}>
                                            <Trash2 className="h-4 w-4" />
                                        </Button>
                                    </div>
                                ))}
                                <button type="button"
                                    className="flex items-center text-sm font-medium text-primary hover:text-primary/80"
                                    onClick={addCriterion}>
                                    <Plus className="mr-1 h-4 w-4" /> Add Criterion
                                </button>
                            </div>
                        </div>
                        <div className="flex justify-end gap-3 pt-4">
                            <Button variant="outline" onClick={resetForm}>Cancel</Button>
                            <Button onClick={handleSave}>Save Template</Button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}
