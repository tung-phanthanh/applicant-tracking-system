import { useState, useEffect } from "react";
import { useParams, useNavigate, useSearchParams } from "react-router-dom";
import { DollarSign, Calendar, FileText, RefreshCw, Send, AlertCircle } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { offerService } from "@/services/offerService";

export default function OfferDraftPage() {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();
    const isEdit = Boolean(id);
    const applicationId = searchParams.get("applicationId");

    const [salary, setSalary] = useState("120000");
    const [equity, setEquity] = useState("5000");
    const [bonus, setBonus] = useState("15000");
    const [position, setPosition] = useState("Senior Frontend Developer");
    const [startDate, setStartDate] = useState("2023-11-15");
    const [expiryDate, setExpiryDate] = useState("2023-11-01");
    const [isSaving, setIsSaving] = useState(false);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        if (isEdit && id) {
            offerService.getById(id).then(offer => {
                setSalary(String(offer.salary));
                setEquity(String(offer.equity));
                setBonus(String(offer.signOnBonus));
                setPosition(offer.positionTitle);
                setStartDate(offer.startDate.split('T')[0]);
                setExpiryDate(offer.expiryDate.split('T')[0]);
            }).catch(err => {
                console.error("Failed to load offer:", err);
                setError("Failed to load offer");
            });
        }
    }, [isEdit, id]);

    const handleSave = async () => {
        if (!applicationId && !isEdit) {
            setError("No application ID provided");
            return;
        }
        setIsSaving(true);
        setError(null);
        try {
            const payload = {
                applicationId: applicationId!,
                salary: Number(salary),
                equity: Number(equity),
                signOnBonus: Number(bonus),
                positionTitle: position,
                startDate: startDate,
                expiryDate: expiryDate,
            };
            if (isEdit && id) {
                await offerService.update(id, payload);
            } else {
                await offerService.create(payload);
            }
            navigate("/offers/approvals");
        } catch (err: any) {
            console.error("Failed to save offer:", err);
            setError(err.response?.data?.message || "Failed to save offer");
        } finally {
            setIsSaving(false);
        }
    };

    const handleSubmit = async () => {
        if (!applicationId && !isEdit) {
            setError("No application ID provided");
            return;
        }
        setIsSubmitting(true);
        setError(null);
        try {
            let offerId = id;
            if (!offerId) {
                const payload = {
                    applicationId: applicationId!,
                    salary: Number(salary),
                    equity: Number(equity),
                    signOnBonus: Number(bonus),
                    positionTitle: position,
                    startDate: startDate,
                    expiryDate: expiryDate,
                };
                const created = await offerService.create(payload);
                offerId = created.id;
            }
            await offerService.submitForApproval(offerId);
            navigate("/offers/approvals");
        } catch (err: any) {
            console.error("Failed to submit offer:", err);
            setError(err.response?.data?.message || "Failed to submit offer for approval");
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <div className="mx-auto max-w-3xl space-y-6">
            {error && (
                <div className="flex items-center gap-2 rounded-lg border border-red-200 bg-red-50 p-4 text-red-700">
                    <AlertCircle className="h-5 w-5" />
                    <p>{error}</p>
                </div>
            )}
            <div>
                <h1 className="text-xl font-semibold text-foreground">
                    {isEdit ? "Edit Offer" : "Draft Offer"}
                </h1>
                <p className="text-sm text-muted-foreground">
                    Configure the offer details before sending for approval.
                </p>
            </div>

            {/* Compensation */}
            <div className="rounded-lg border border-border bg-card p-6 shadow-sm">
                <div className="flex items-center gap-2 mb-5">
                    <DollarSign className="h-5 w-5 text-primary" />
                    <h3 className="font-semibold text-foreground">Compensation Details</h3>
                </div>
                <div className="grid grid-cols-1 gap-5 sm:grid-cols-3">
                    <div>
                        <label className="block text-sm font-medium text-muted-foreground">Base Salary ($)</label>
                        <Input className="mt-1" value={salary} onChange={(e) => setSalary(e.target.value)} />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-muted-foreground">Equity ($)</label>
                        <Input className="mt-1" value={equity} onChange={(e) => setEquity(e.target.value)} />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-muted-foreground">Sign-On Bonus ($)</label>
                        <Input className="mt-1" value={bonus} onChange={(e) => setBonus(e.target.value)} />
                    </div>
                </div>
            </div>

            {/* Dates */}
            <div className="rounded-lg border border-border bg-card p-6 shadow-sm">
                <div className="flex items-center gap-2 mb-5">
                    <Calendar className="h-5 w-5 text-primary" />
                    <h3 className="font-semibold text-foreground">Key Dates</h3>
                </div>
                <div className="grid grid-cols-1 gap-5 sm:grid-cols-3">
                    <div>
                        <label className="block text-sm font-medium text-muted-foreground">Position Title</label>
                        <Input className="mt-1" value={position} onChange={(e) => setPosition(e.target.value)} />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-muted-foreground">Start Date</label>
                        <Input className="mt-1" type="date" value={startDate} onChange={(e) => setStartDate(e.target.value)} />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-muted-foreground">Offer Expiry</label>
                        <Input className="mt-1" type="date" value={expiryDate} onChange={(e) => setExpiryDate(e.target.value)} />
                    </div>
                </div>
            </div>

            {/* Approval Chain */}
            <div className="rounded-lg border border-border bg-card p-6 shadow-sm">
                <div className="flex items-center gap-2 mb-5">
                    <FileText className="h-5 w-5 text-primary" />
                    <h3 className="font-semibold text-foreground">Approval Chain</h3>
                </div>
                <ol className="space-y-3 ml-4 list-decimal text-sm text-muted-foreground">
                    <li>HR Manager — <span className="text-yellow-600">Pending</span></li>
                    <li>VP of Engineering — <span className="text-muted-foreground">Not yet</span></li>
                </ol>
            </div>

            {/* Actions */}
            <div className="flex justify-end gap-3 pt-2">
                <Button variant="outline" onClick={() => navigate(-1)}>Cancel</Button>
                <Button variant="outline" onClick={handleSave} disabled={isSaving}>
                    {isSaving ? <><RefreshCw className="mr-1.5 h-4 w-4 animate-spin" /> Saving…</> : "Save Draft"}
                </Button>
                <Button onClick={handleSubmit} disabled={isSubmitting}>
                    {isSubmitting ? (
                        <><RefreshCw className="mr-1.5 h-4 w-4 animate-spin" /> Submitting…</>
                    ) : (
                        <><Send className="mr-1.5 h-4 w-4" /> Submit for Approval</>
                    )}
                </Button>
            </div>
        </div>
    );
}
