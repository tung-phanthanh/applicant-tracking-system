import { useState, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { ClipboardCheck, Check, X, Eye, RefreshCw, AlertTriangle, ChevronLeft, ChevronRight } from "lucide-react";
import { Button } from "@/components/ui/button";
import { offerService } from "@/services/offerService";
import type { Offer } from "@/types/offer";

export default function OfferApprovalPage() {
    const navigate = useNavigate();
    const [offers, setOffers] = useState<Offer[]>([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);
    const PAGE_SIZE = 10;

    const [isLoading, setIsLoading] = useState(true);
    const [actionLoading, setActionLoading] = useState<string | null>(null);
    const [error, setError] = useState("");

    const loadOffers = useCallback(async (p = 0) => {
        setIsLoading(true);
        setError("");
        try {
            const result = await offerService.getPendingApprovals(p, PAGE_SIZE);
            setOffers(result.content);
            setTotalPages(result.totalPages);
            setTotalElements(result.totalElements);
            setPage(result.number);
        } catch {
            setError("Failed to load pending offers. Is the backend running?");
        } finally {
            setIsLoading(false);
        }
    }, []);

    useEffect(() => { loadOffers(0); }, [loadOffers]);

    const handleApprove = async (id: string) => {
        setActionLoading(id);
        try {
            await offerService.processApproval(id, { status: "APPROVED", comment: "Approved" });
            loadOffers(page);
        } catch {
            setError("Failed to approve offer.");
        } finally {
            setActionLoading(null);
        }
    };

    const handleReject = async (id: string) => {
        setActionLoading(id);
        try {
            await offerService.processApproval(id, { status: "REJECTED", comment: "Rejected" });
            loadOffers(page);
        } catch {
            setError("Failed to reject offer.");
        } finally {
            setActionLoading(null);
        }
    };

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <div className="flex items-center gap-3">
                    <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary/10 text-primary">
                        <ClipboardCheck className="h-5 w-5" />
                    </div>
                    <div>
                        <h1 className="text-xl font-semibold text-foreground">Offer Approvals</h1>
                        <p className="text-sm text-muted-foreground">{totalElements} pending offer{totalElements !== 1 ? "s" : ""}.</p>
                    </div>
                </div>
            </div>

            {error && (
                <div className="flex items-center gap-3 rounded-lg border border-destructive/30 bg-destructive/10 px-4 py-3">
                    <AlertTriangle className="h-4 w-4 text-destructive" />
                    <p className="text-sm text-destructive">{error}</p>
                </div>
            )}

            <div className="overflow-hidden rounded-lg border border-border bg-card shadow-sm">
                {isLoading ? (
                    <div className="flex items-center justify-center py-20 text-muted-foreground">
                        <RefreshCw className="mr-2 h-5 w-5 animate-spin" /> Loading…
                    </div>
                ) : offers.length === 0 ? (
                    <div className="flex flex-col items-center justify-center gap-2 py-20 text-muted-foreground">
                        <ClipboardCheck className="h-10 w-10 opacity-30" />
                        <p className="text-sm">No pending approvals.</p>
                    </div>
                ) : (
                    <div className="overflow-x-auto">
                        <table className="min-w-full divide-y divide-border">
                            <thead className="bg-muted/50">
                                <tr>
                                    <th className="py-3 pl-6 pr-3 text-left text-xs font-semibold uppercase tracking-wider text-muted-foreground">Candidate</th>
                                    <th className="px-3 py-3 text-left text-xs font-semibold uppercase tracking-wider text-muted-foreground">Role</th>
                                    <th className="px-3 py-3 text-left text-xs font-semibold uppercase tracking-wider text-muted-foreground">Salary</th>
                                    <th className="px-3 py-3 text-left text-xs font-semibold uppercase tracking-wider text-muted-foreground">Bonus</th>
                                    <th className="px-3 py-3 text-left text-xs font-semibold uppercase tracking-wider text-muted-foreground">Requested By</th>
                                    <th className="py-3 pl-3 pr-6 text-right text-xs font-semibold uppercase tracking-wider text-muted-foreground">Actions</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-border">
                                {offers.map((o) => {
                                    const isActing = actionLoading === o.id;
                                    return (
                                        <tr key={o.id} className="transition-colors hover:bg-muted/30">
                                            <td className="whitespace-nowrap py-4 pl-6 pr-3 font-medium text-foreground">
                                                {o.candidateName ?? o.applicationId}
                                            </td>
                                            <td className="whitespace-nowrap px-3 py-4 text-sm text-muted-foreground">{o.positionTitle}</td>
                                            <td className="whitespace-nowrap px-3 py-4 text-sm text-foreground">
                                                {o.salary?.toLocaleString()}
                                            </td>
                                            <td className="whitespace-nowrap px-3 py-4 text-sm text-foreground">
                                                {o.signOnBonus?.toLocaleString()}
                                            </td>
                                            <td className="whitespace-nowrap px-3 py-4 text-sm text-muted-foreground">{o.createdByName}</td>
                                            <td className="whitespace-nowrap py-4 pl-3 pr-6 text-right">
                                                <div className="flex items-center justify-end gap-1">
                                                    <Button variant="ghost" size="sm" className="h-8 w-8 p-0 text-green-600 hover:text-green-700"
                                                        onClick={() => handleApprove(o.id)} disabled={isActing} title="Approve">
                                                        {isActing ? <RefreshCw className="h-4 w-4 animate-spin" /> : <Check className="h-4 w-4" />}
                                                    </Button>
                                                    <Button variant="ghost" size="sm" className="h-8 w-8 p-0 text-red-600 hover:text-red-700"
                                                        onClick={() => handleReject(o.id)} disabled={isActing} title="Reject">
                                                        <X className="h-4 w-4" />
                                                    </Button>
                                                    <Button variant="ghost" size="sm" className="h-8 w-8 p-0"
                                                        onClick={() => navigate(`/offers/${o.id}/preview`)} disabled={isActing} title="Preview">
                                                        <Eye className="h-4 w-4" />
                                                    </Button>
                                                </div>
                                            </td>
                                        </tr>
                                    );
                                })}
                            </tbody>
                        </table>
                    </div>
                )}
            </div>

            {/* Pagination */}
            {totalPages > 1 && (
                <div className="flex items-center justify-between">
                    <p className="text-sm text-muted-foreground">Page {page + 1} of {totalPages}</p>
                    <div className="flex gap-1">
                        <Button variant="outline" size="sm" disabled={page === 0} onClick={() => loadOffers(page - 1)}>
                            <ChevronLeft className="h-4 w-4" />
                        </Button>
                        <Button variant="outline" size="sm" disabled={page >= totalPages - 1} onClick={() => loadOffers(page + 1)}>
                            <ChevronRight className="h-4 w-4" />
                        </Button>
                    </div>
                </div>
            )}
        </div>
    );
}
