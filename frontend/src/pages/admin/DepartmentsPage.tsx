import { useState, useEffect } from "react";
import {
    Building2,
    Plus,
    Search,
    Pencil,
    Trash2,
    Users,
    Briefcase,
    Check,
    X,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { cn } from "@/lib/utils";
import type { Department, DepartmentStatus } from "@/types/index";
import { departmentService } from "@/services/api/department.service";

interface DeptFormState {
    name: string;
    description: string;
    head: string;
    employeeCount: string;
}

const EMPTY_FORM: DeptFormState = {
    name: "",
    description: "",
    head: "",
    employeeCount: "0",
};

export default function DepartmentsPage() {
    const [departments, setDepartments] = useState<Department[]>([]);
    const [search, setSearch] = useState("");
    const [editingId, setEditingId] = useState<string | null>(null);
    const [creating, setCreating] = useState(false);
    const [form, setForm] = useState<DeptFormState>(EMPTY_FORM);
    const [deleteConfirm, setDeleteConfirm] = useState<string | null>(null);

    const filtered = departments.filter(
        (d) =>
            d.name.toLowerCase().includes(search.toLowerCase()) ||
            d.head.toLowerCase().includes(search.toLowerCase()),
    );

    const startEdit = (dept: Department) => {
        setEditingId(dept.id);
        setCreating(false);
        setForm({
            name: dept.name,
            description: dept.description,
            head: dept.head,
            employeeCount: String(dept.employeeCount),
        });
    };

    useEffect(() => {
        departmentService.getAllDepartments().then(setDepartments).catch(console.error);
    }, []);

    const saveEdit = async () => {
        if (!form.name.trim() || !editingId) return;
        try {
            const updated = await departmentService.updateDepartment(editingId, {
                name: form.name.trim(),
                description: form.description.trim(),
                head: form.head.trim(),
                employeeCount: Number(form.employeeCount) || 0,
            });
            setDepartments((prev) => prev.map((d) => (d.id === editingId ? updated : d)));
            setEditingId(null);
        } catch (e) {
            console.error(e);
        }
    };

    const createDept = async () => {
        if (!form.name.trim()) return;
        setCreating(false);
        try {
            const newDept = await departmentService.createDepartment({
                name: form.name.trim(),
                description: form.description.trim() || "No description",
                head: form.head.trim() || "—",
                employeeCount: Number(form.employeeCount) || 0,
            });
            setDepartments((prev) => [...prev, newDept]);
            setForm(EMPTY_FORM);
        } catch (e) {
            console.error(e);
        }
    };

    const deleteDept = async (id: string) => {
        setDeleteConfirm(null);
        try {
            await departmentService.deleteDepartment(id);
            setDepartments((prev) => prev.filter((d) => d.id !== id));
        } catch (e) {
            console.error(e);
        }
    };

    const toggleStatus = async (id: string) => {
        const dept = departments.find((d) => d.id === id);
        if (!dept) return;
        const newStatus = dept.status === "active" ? false : true;

        // Optimistic update
        setDepartments((prev) =>
            prev.map((d) =>
                d.id === id
                    ? { ...d, status: (d.status === "active" ? "inactive" : "active") as DepartmentStatus }
                    : d,
            ),
        );
        try {
            await departmentService.toggleStatus(id, newStatus);
        } catch (e) {
            console.error(e);
            // Revert
            setDepartments((prev) =>
                prev.map((d) => (d.id === id ? { ...d, status: dept.status } : d)),
            );
        }
    };

    const totalEmployees = departments.filter(d => d.status === "active").reduce((s, d) => s + d.employeeCount, 0);
    const totalOpenings = departments.filter(d => d.status === "active").reduce((s, d) => s + d.openPositions, 0);

    return (
        <div className="space-y-6">
            {/* Header */}
            <div className="flex items-center justify-between">
                <div>
                    <h2 className="text-2xl font-bold text-foreground">Department Management</h2>
                    <p className="mt-1 text-sm text-muted-foreground">
                        {departments.filter(d => d.status === "active").length} active departments · {totalEmployees} employees · {totalOpenings} open positions
                    </p>
                </div>
                <Button
                    onClick={() => { setCreating(true); setEditingId(null); setForm(EMPTY_FORM); }}
                    className="gap-2"
                >
                    <Plus className="h-4 w-4" />
                    New Department
                </Button>
            </div>

            {/* Search */}
            <div className="relative max-w-sm">
                <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
                <Input
                    className="pl-9"
                    placeholder="Search departments or team leads…"
                    value={search}
                    onChange={(e) => setSearch(e.target.value)}
                />
            </div>

            {/* Create Form */}
            {creating && (
                <DeptForm
                    form={form}
                    setForm={setForm}
                    onSave={createDept}
                    onCancel={() => setCreating(false)}
                    title="New Department"
                />
            )}

            {/* Cards Grid */}
            <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-3">
                {filtered.map((dept) => (
                    <div
                        key={dept.id}
                        className={cn(
                            "rounded-lg border border-border bg-card shadow-sm transition-opacity",
                            dept.status === "inactive" && "opacity-60",
                        )}
                    >
                        {editingId === dept.id ? (
                            <div className="p-5">
                                <DeptForm
                                    form={form}
                                    setForm={setForm}
                                    onSave={saveEdit}
                                    onCancel={() => setEditingId(null)}
                                    title={`Edit: ${dept.name}`}
                                />
                            </div>
                        ) : (
                            <>
                                <div className="p-5">
                                    <div className="mb-3 flex items-start justify-between">
                                        <div className="flex items-center gap-3">
                                            <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-primary text-primary-foreground">
                                                <Building2 className="h-5 w-5" />
                                            </div>
                                            <div>
                                                <h3 className="font-semibold text-card-foreground">{dept.name}</h3>
                                                <Badge
                                                    variant="outline"
                                                    className={cn(
                                                        "mt-0.5 text-xs",
                                                        dept.status === "active"
                                                            ? "border-green-600/30 text-green-700"
                                                            : "border-muted-foreground/30 text-muted-foreground",
                                                    )}
                                                >
                                                    {dept.status}
                                                </Badge>
                                            </div>
                                        </div>
                                    </div>
                                    <p className="mb-4 text-sm text-muted-foreground line-clamp-2">
                                        {dept.description}
                                    </p>
                                    <div className="grid grid-cols-2 gap-3 text-sm">
                                        <div className="flex items-center gap-1.5 text-muted-foreground">
                                            <Users className="h-3.5 w-3.5" />
                                            <span>{dept.employeeCount} employees</span>
                                        </div>
                                        <div className="flex items-center gap-1.5 text-muted-foreground">
                                            <Briefcase className="h-3.5 w-3.5" />
                                            <span>{dept.openPositions} open roles</span>
                                        </div>
                                    </div>
                                    <p className="mt-2 text-xs text-muted-foreground">
                                        Team Lead: <span className="font-medium text-foreground">{dept.head}</span>
                                    </p>
                                </div>

                                {/* Card Footer */}
                                <div className="flex items-center justify-between border-t border-border px-5 py-3">
                                    {deleteConfirm === dept.id ? (
                                        <div className="flex items-center gap-2">
                                            <span className="text-xs text-destructive">Delete?</span>
                                            <Button variant="destructive" size="sm" onClick={() => deleteDept(dept.id)}><Check className="h-3 w-3" /></Button>
                                            <Button variant="outline" size="sm" onClick={() => setDeleteConfirm(null)}><X className="h-3 w-3" /></Button>
                                        </div>
                                    ) : (
                                        <Button
                                            variant="ghost"
                                            size="sm"
                                            onClick={() => setDeleteConfirm(dept.id)}
                                            className="gap-1.5 text-xs text-destructive hover:text-destructive"
                                        >
                                            <Trash2 className="h-3.5 w-3.5" />
                                            Delete
                                        </Button>
                                    )}
                                    <div className="flex gap-2">
                                        <Button variant="outline" size="sm" onClick={() => toggleStatus(dept.id)} className="text-xs">
                                            {dept.status === "active" ? "Deactivate" : "Activate"}
                                        </Button>
                                        <Button variant="outline" size="sm" onClick={() => startEdit(dept)} className="gap-1.5 text-xs">
                                            <Pencil className="h-3.5 w-3.5" />
                                            Edit
                                        </Button>
                                    </div>
                                </div>
                            </>
                        )}
                    </div>
                ))}
            </div>

            {filtered.length === 0 && (
                <div className="flex h-48 items-center justify-center rounded-lg border border-dashed border-border">
                    <p className="text-sm text-muted-foreground">No departments match your search.</p>
                </div>
            )}
        </div>
    );
}

// ─── Shared Inline Form ────────────────────────────────────────────────────
interface DeptFormProps {
    form: DeptFormState;
    setForm: React.Dispatch<React.SetStateAction<DeptFormState>>;
    onSave: () => void;
    onCancel: () => void;
    title: string;
}

function DeptForm({ form, setForm, onSave, onCancel, title }: DeptFormProps) {
    const set = (k: keyof DeptFormState) => (e: React.ChangeEvent<HTMLInputElement>) =>
        setForm((prev) => ({ ...prev, [k]: e.target.value }));

    return (
        <div className="rounded-lg border border-border bg-card p-5 shadow-sm">
            <h3 className="mb-4 font-semibold text-card-foreground">{title}</h3>
            <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
                <div className="space-y-1.5">
                    <Label htmlFor="dept-name">Name *</Label>
                    <Input id="dept-name" placeholder="Engineering" value={form.name} onChange={set("name")} />
                </div>
                <div className="space-y-1.5">
                    <Label htmlFor="dept-head">Team Lead</Label>
                    <Input id="dept-head" placeholder="Full name" value={form.head} onChange={set("head")} />
                </div>
                <div className="space-y-1.5 md:col-span-2">
                    <Label htmlFor="dept-desc">Description</Label>
                    <Input id="dept-desc" placeholder="Brief description" value={form.description} onChange={set("description")} />
                </div>
                <div className="space-y-1.5">
                    <Label htmlFor="dept-count">Employee Count</Label>
                    <Input id="dept-count" type="number" min="0" value={form.employeeCount} onChange={set("employeeCount")} />
                </div>
            </div>
            <div className="mt-4 flex gap-2">
                <Button onClick={onSave} disabled={!form.name.trim()}>Save</Button>
                <Button variant="outline" onClick={onCancel}>Cancel</Button>
            </div>
        </div>
    );
}
