import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
    FileText,
    ChevronLeft,
    CheckCircle,
    XCircle,
    Clock,
    Eye,
    Send,
    Edit2,
    User,
    DollarSign,
    Calendar,
} from "lucide-react";
import { toast } from "sonner";
import { PageHeader } from "@/components/shared/PageHeader";
import { StatusBadge } from "@/components/shared/StatusBadge";
import { CardSkeleton } from "@/components/shared/LoadingSkeleton";
import { Button } from "@/components/ui/button";
import { offerService } from "@/services/offerService";
import type { OfferResponse } from "@/types/models";
import { cn } from "@/lib/utils";

export default function OfferApprovalPage() {
    const { offerId } = useParams<{ offerId: string }>();
    const navigate = useNavigate();
    const [offer, setOffer] = useState<OfferResponse | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [comment, setComment] = useState("");
    const [isActing, setIsActing] = useState(false);

    // For demo: simulate role (in real app from auth context)
    const isHrManager = true;

    useEffect(() => {
        if (!offerId) return;
        offerService
            .getById(Number(offerId))
            .then(setOffer)
            .catch(() => toast.error("Failed to load offer"))
            .finally(() => setIsLoading(false));
    }, [offerId]);

    const reload = () => {
        setIsLoading(true);
        offerService.getById(Number(offerId)).then(setOffer).finally(() => setIsLoading(false));
    };

    const handleApprove = async () => {
        setIsActing(true);
        try {
            await offerService.approve(Number(offerId), { comment: comment || undefined });
            toast.success("Offer approved!");
            setComment("");
            reload();
        } catch {
            toast.error("Approval failed");
        } finally {
            setIsActing(false);
        }
    };

    const handleReject = async () => {
        if (!comment.trim()) {
            toast.error("Please provide a reason for rejection");
            return;
        }
        setIsActing(true);
        try {
            await offerService.reject(Number(offerId), { comment });
            toast.success("Offer rejected");
            setComment("");
            reload();
        } catch {
            toast.error("Rejection failed");
        } finally {
            setIsActing(false);
        }
    };

    if (isLoading) return <CardSkeleton />;
    if (!offer) return null;

    const isPendingApproval = offer.status === "PENDING_APPROVAL";
    const isDraft = offer.status === "DRAFT";

    const formatCurrency = (n: number) =>
        new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(n);

    return (
        <div className="space-y-6">
            <PageHeader
                title="Offer Detail"
                description={`Offer for ${offer.candidateName}`}
                icon={FileText}
                actions={
                    <div className="flex gap-2">
                        <Button variant="outline" onClick={() => navigate(-1)}>
                            <ChevronLeft className="mr-1 h-4 w-4" /> Back
                        </Button>
                        {(isDraft || offer.status === "REJECTED") && (
                            <Button
                                variant="outline"
                                onClick={() => navigate(`/offers/edit/${offer.id}`)}
                                className="gap-2"
                            >
                                <Edit2 className="h-4 w-4" /> Edit
                            </Button>
                        )}
                        {offer.status === "APPROVED" && (
                            <>
                                <Button
                                    variant="outline"
                                    onClick={() => navigate(`/offers/${offer.id}/preview`)}
                                    className="gap-2"
                                >
                                    <Eye className="h-4 w-4" /> Preview PDF
                                </Button>
                                <Button
                                    onClick={() => navigate(`/offers/${offer.id}/send`)}
                                    className="gap-2 bg-indigo-600 hover:bg-indigo-700"
                                >
                                    <Send className="h-4 w-4" /> Send Offer
                                </Button>
                            </>
                        )}
                    </div>
                }
            />

            <div className="grid grid-cols-1 gap-6 lg:grid-cols-3">
                {/* Offer Details */}
                <div className="space-y-4 lg:col-span-2">
                    {/* Candidate Info */}
                    <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
                        <div className="flex items-center justify-between">
                            <div className="flex items-center gap-3">
                                <div className="flex h-12 w-12 items-center justify-center rounded-full bg-indigo-100 text-lg font-bold text-indigo-700">
                                    {offer.candidateName.split(" ").map((n) => n[0]).join("").slice(0, 2)}
                                </div>
                                <div>
                                    <h3 className="text-base font-semibold text-slate-800">{offer.candidateName}</h3>
                                    <p className="text-sm text-slate-500">{offer.jobTitle}</p>
                                </div>
                            </div>
                            <StatusBadge status={offer.status} className="text-sm px-3 py-1" />
                        </div>
                    </div>

                    {/* Offer Terms */}
                    <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
                        <h3 className="mb-4 text-xs font-semibold uppercase tracking-wide text-slate-500">
                            Offer Terms
                        </h3>
                        <div className="grid grid-cols-2 gap-4">
                            <InfoRow icon={User} label="Position" value={offer.positionTitle} />
                            <InfoRow icon={DollarSign} label="Salary" value={formatCurrency(offer.salary)} />
                            {offer.bonus && (
                                <InfoRow icon={DollarSign} label="Bonus" value={formatCurrency(offer.bonus)} />
                            )}
                            {offer.startDate && (
                                <InfoRow icon={Calendar} label="Start Date" value={new Date(offer.startDate).toLocaleDateString("en-US", { year: "numeric", month: "long", day: "numeric" })} />
                            )}
                            {offer.contractType && <InfoRow icon={FileText} label="Contract" value={offer.contractType} />}
                            {offer.workLocation && <InfoRow icon={FileText} label="Location" value={offer.workLocation} />}
                            {offer.benefits && (
                                <div className="col-span-2">
                                    <p className="text-xs font-medium uppercase tracking-wide text-slate-400">Benefits</p>
                                    <p className="mt-1 text-sm text-slate-700">{offer.benefits}</p>
                                </div>
                            )}
                            {offer.notes && (
                                <div className="col-span-2">
                                    <p className="text-xs font-medium uppercase tracking-wide text-slate-400">Notes</p>
                                    <p className="mt-1 text-sm text-slate-700">{offer.notes}</p>
                                </div>
                            )}
                        </div>
                    </div>

                    {/* Approval History */}
                    {offer.approvals.length > 0 && (
                        <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
                            <h3 className="mb-4 text-xs font-semibold uppercase tracking-wide text-slate-500">
                                Approval History
                            </h3>
                            <div className="space-y-3">
                                {offer.approvals.map((a, i) => (
                                    <div key={i} className="flex items-start gap-3">
                                        <div className={cn(
                                            "mt-0.5 flex h-6 w-6 shrink-0 items-center justify-center rounded-full",
                                            a.status === "APPROVED" ? "bg-emerald-100" : "bg-rose-100",
                                        )}>
                                            {a.status === "APPROVED" ? (
                                                <CheckCircle className="h-3.5 w-3.5 text-emerald-600" />
                                            ) : (
                                                <XCircle className="h-3.5 w-3.5 text-rose-500" />
                                            )}
                                        </div>
                                        <div>
                                            <p className="text-sm font-medium text-slate-800">{a.approvedBy}</p>
                                            {a.comment && (
                                                <p className="mt-0.5 text-sm text-slate-500 italic">"{a.comment}"</p>
                                            )}
                                            <p className="mt-0.5 text-xs text-slate-400">
                                                {new Date(a.approvedAt).toLocaleString()}
                                            </p>
                                        </div>
                                        <div className="ml-auto">
                                            <StatusBadge status={a.status} />
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>
                    )}
                </div>

                {/* Sidebar: Status + Approver Actions */}
                <div className="space-y-4 lg:col-span-1">
                    {/* Status timeline */}
                    <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
                        <h3 className="mb-4 text-xs font-semibold uppercase tracking-wide text-slate-500">
                            Approval Status
                        </h3>
                        {[
                            { label: "Draft", done: true },
                            { label: "Pending Approval", done: ["PENDING_APPROVAL", "APPROVED", "REJECTED", "SENT"].includes(offer.status) },
                            { label: "Approved / Rejected", done: ["APPROVED", "REJECTED", "SENT"].includes(offer.status) },
                            { label: "Sent to Candidate", done: offer.status === "SENT" },
                        ].map((step, i) => (
                            <div key={i} className="flex items-center gap-3 py-2">
                                <div className={cn(
                                    "flex h-5 w-5 items-center justify-center rounded-full border-2 text-xs font-bold",
                                    step.done
                                        ? "border-indigo-500 bg-indigo-500 text-white"
                                        : "border-slate-200 bg-white text-slate-300",
                                )}>
                                    {step.done ? "✓" : i + 1}
                                </div>
                                <span className={cn("text-sm", step.done ? "font-medium text-slate-800" : "text-slate-400")}>
                                    {step.label}
                                </span>
                            </div>
                        ))}
                    </div>

                    {/* HR Manager actions */}
                    {isHrManager && isPendingApproval && (
                        <div className="rounded-xl border border-amber-100 bg-amber-50 p-5 shadow-sm">
                            <h3 className="mb-3 flex items-center gap-2 text-sm font-semibold text-amber-800">
                                <Clock className="h-4 w-4" /> Action Required
                            </h3>
                            <textarea
                                value={comment}
                                onChange={(e) => setComment(e.target.value)}
                                placeholder="Add your comment (required for rejection)..."
                                rows={3}
                                className="mb-3 w-full resize-none rounded-lg border border-amber-200 bg-white px-3 py-2 text-sm outline-none focus:border-amber-400 focus:ring-2 focus:ring-amber-100"
                            />
                            <div className="flex gap-2">
                                <Button
                                    onClick={handleApprove}
                                    disabled={isActing}
                                    className="flex-1 gap-1.5 bg-emerald-600 text-sm hover:bg-emerald-700"
                                >
                                    <CheckCircle className="h-3.5 w-3.5" /> Approve
                                </Button>
                                <Button
                                    onClick={handleReject}
                                    disabled={isActing}
                                    variant="outline"
                                    className="flex-1 gap-1.5 border-rose-200 text-rose-600 text-sm hover:bg-rose-50"
                                >
                                    <XCircle className="h-3.5 w-3.5" /> Reject
                                </Button>
                            </div>
                        </div>
                    )}

                    {/* Meta info */}
                    <div className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm text-sm space-y-2">
                        <div className="flex justify-between text-slate-500">
                            <span>Created by</span>
                            <span className="font-medium text-slate-700">{offer.createdBy}</span>
                        </div>
                        <div className="flex justify-between text-slate-500">
                            <span>Created at</span>
                            <span className="font-medium text-slate-700">
                                {new Date(offer.createdAt).toLocaleDateString()}
                            </span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

function InfoRow({
    icon: Icon,
    label,
    value,
}: {
    icon: React.ElementType;
    label: string;
    value: string;
}) {
    return (
        <div className="flex items-start gap-2">
            <Icon className="mt-0.5 h-4 w-4 shrink-0 text-slate-400" />
            <div>
                <p className="text-xs font-medium uppercase tracking-wide text-slate-400">{label}</p>
                <p className="text-sm font-medium text-slate-800">{value}</p>
            </div>
        </div>
    );
}
