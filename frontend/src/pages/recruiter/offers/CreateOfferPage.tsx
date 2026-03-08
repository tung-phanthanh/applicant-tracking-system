import { useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import {
    FileText,
    ChevronLeft,
    Send,
    Save,
    Briefcase,
} from "lucide-react";
import { toast } from "sonner";
import { PageHeader } from "@/components/shared/PageHeader";
import { Button } from "@/components/ui/button";
import { offerService } from "@/services/offerService";
import type { CreateOfferRequest } from "@/types/models";

const CONTRACT_TYPES = ["Full-time", "Part-time", "Contract", "Internship", "Freelance"];
const LOCATIONS = ["On-site", "Remote", "Hybrid"];

export default function CreateOfferPage() {
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();
    const applicationId = Number(searchParams.get("applicationId") ?? "0");

    const [isSaving, setIsSaving] = useState(false);
    const [form, setForm] = useState({
        positionTitle: "",
        salary: "",
        bonus: "",
        startDate: "",
        contractType: "Full-time",
        workLocation: "Hybrid",
        benefits: "",
        notes: "",
    });

    const set = (field: string, value: string) =>
        setForm((prev) => ({ ...prev, [field]: value }));

    const buildPayload = (): CreateOfferRequest => ({
        applicationId,
        positionTitle: form.positionTitle,
        salary: parseFloat(form.salary) || 0,
        bonus: form.bonus ? parseFloat(form.bonus) : undefined,
        startDate: form.startDate || undefined,
        contractType: form.contractType || undefined,
        workLocation: form.workLocation || undefined,
        benefits: form.benefits || undefined,
        notes: form.notes || undefined,
    });

    const handleSaveDraft = async () => {
        if (!form.positionTitle || !form.salary) {
            toast.error("Position title and salary are required");
            return;
        }
        setIsSaving(true);
        try {
            const offer = await offerService.create(buildPayload());
            toast.success("Offer draft saved");
            navigate(`/offers/${offer.id}`);
        } catch {
            toast.error("Failed to save offer");
        } finally {
            setIsSaving(false);
        }
    };

    const handleSubmitForApproval = async () => {
        if (!form.positionTitle || !form.salary) {
            toast.error("Position title and salary are required");
            return;
        }
        setIsSaving(true);
        try {
            const offer = await offerService.create(buildPayload());
            await offerService.submit(offer.id);
            toast.success("Offer submitted for approval!");
            navigate(`/offers/${offer.id}`);
        } catch {
            toast.error("Failed to submit offer");
        } finally {
            setIsSaving(false);
        }
    };

    const inputCls =
        "w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm text-slate-800 outline-none transition focus:border-indigo-400 focus:ring-2 focus:ring-indigo-100";
    const labelCls = "mb-1.5 block text-sm font-medium text-slate-700";

    return (
        <div className="space-y-6">
            <PageHeader
                title="Create Offer"
                description="Prepare a job offer for this candidate"
                icon={FileText}
                actions={
                    <Button variant="outline" onClick={() => navigate(-1)}>
                        <ChevronLeft className="mr-1 h-4 w-4" /> Back
                    </Button>
                }
            />

            <div className="grid grid-cols-1 gap-6 lg:grid-cols-3">
                {/* Application Info */}
                <div className="lg:col-span-1">
                    <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
                        <h3 className="mb-4 text-xs font-semibold uppercase tracking-wide text-slate-500">
                            Candidate Info
                        </h3>
                        <div className="space-y-3 text-sm">
                            <div className="flex items-center gap-2 text-slate-600">
                                Application #{applicationId}
                            </div>
                            <div className="flex items-center gap-2 text-slate-500 text-xs">
                                <Briefcase className="h-3.5 w-3.5" />
                                Position will be filled in form
                            </div>
                        </div>
                    </div>
                </div>

                {/* Offer Form */}
                <div className="lg:col-span-2">
                    <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
                        <h3 className="mb-5 text-xs font-semibold uppercase tracking-wide text-slate-500">
                            Offer Details
                        </h3>

                        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                            <div className="sm:col-span-2">
                                <label className={labelCls}>
                                    Position Title <span className="text-rose-500">*</span>
                                </label>
                                <input
                                    className={inputCls}
                                    value={form.positionTitle}
                                    onChange={(e) => set("positionTitle", e.target.value)}
                                    placeholder="e.g., Senior Software Engineer"
                                />
                            </div>

                            <div>
                                <label className={labelCls}>
                                    Salary (VND / month) <span className="text-rose-500">*</span>
                                </label>
                                <input
                                    className={inputCls}
                                    type="number"
                                    min={0}
                                    value={form.salary}
                                    onChange={(e) => set("salary", e.target.value)}
                                    placeholder="e.g., 25000000"
                                />
                            </div>

                            <div>
                                <label className={labelCls}>Bonus (optional)</label>
                                <input
                                    className={inputCls}
                                    type="number"
                                    min={0}
                                    value={form.bonus}
                                    onChange={(e) => set("bonus", e.target.value)}
                                    placeholder="e.g., 3000000"
                                />
                            </div>

                            <div>
                                <label className={labelCls}>Start Date</label>
                                <input
                                    className={inputCls}
                                    type="date"
                                    value={form.startDate}
                                    onChange={(e) => set("startDate", e.target.value)}
                                />
                            </div>

                            <div>
                                <label className={labelCls}>Contract Type</label>
                                <select
                                    className={inputCls}
                                    value={form.contractType}
                                    onChange={(e) => set("contractType", e.target.value)}
                                >
                                    {CONTRACT_TYPES.map((t) => (
                                        <option key={t} value={t}>{t}</option>
                                    ))}
                                </select>
                            </div>

                            <div>
                                <label className={labelCls}>Work Location</label>
                                <select
                                    className={inputCls}
                                    value={form.workLocation}
                                    onChange={(e) => set("workLocation", e.target.value)}
                                >
                                    {LOCATIONS.map((l) => (
                                        <option key={l} value={l}>{l}</option>
                                    ))}
                                </select>
                            </div>

                            <div className="sm:col-span-2">
                                <label className={labelCls}>Benefits</label>
                                <textarea
                                    className={`${inputCls} resize - none`}
                                    rows={3}
                                    value={form.benefits}
                                    onChange={(e) => set("benefits", e.target.value)}
                                    placeholder="Health insurance, 13th month salary, annual leave..."
                                />
                            </div>

                            <div className="sm:col-span-2">
                                <label className={labelCls}>Additional Notes</label>
                                <textarea
                                    className={`${inputCls} resize - none`}
                                    rows={3}
                                    value={form.notes}
                                    onChange={(e) => set("notes", e.target.value)}
                                    placeholder="Any special terms or conditions..."
                                />
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* Actions */}
            <div className="flex items-center justify-end gap-3 pb-4">
                <Button variant="outline" onClick={() => navigate(-1)}>
                    Cancel
                </Button>
                <Button
                    variant="outline"
                    onClick={handleSaveDraft}
                    disabled={isSaving}
                    className="gap-2"
                >
                    <Save className="h-4 w-4" /> Save Draft
                </Button>
                <Button
                    onClick={handleSubmitForApproval}
                    disabled={isSaving}
                    className="gap-2 bg-indigo-600 hover:bg-indigo-700"
                >
                    <Send className="h-4 w-4" />
                    Submit for Approval
                </Button>
            </div>
        </div>
    );
}
