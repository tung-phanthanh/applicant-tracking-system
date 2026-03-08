import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
    FileText,
    ChevronLeft,
    Download,
    Send,
    Edit2,
    Loader2,
} from "lucide-react";
import { toast } from "sonner";
import { PageHeader } from "@/components/shared/PageHeader";
import { Button } from "@/components/ui/button";
import { offerService } from "@/services/offerService";
import type { OfferResponse } from "@/types/models";

export default function OfferPdfPreviewPage() {
    const { offerId } = useParams<{ offerId: string }>();
    const navigate = useNavigate();
    const [offer, setOffer] = useState<OfferResponse | null>(null);
    const [pdfUrl, setPdfUrl] = useState<string | null>(null);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        if (!offerId) return;
        // Load offer info + PDF blob in parallel
        Promise.all([
            offerService.getById(Number(offerId)),
            offerService.getPreviewBlob(Number(offerId)),
        ])
            .then(([offerData, blobUrl]) => {
                setOffer(offerData);
                setPdfUrl(blobUrl);
            })
            .catch(() => {
                toast.error("Failed to load offer PDF");
            })
            .finally(() => setIsLoading(false));

        // Cleanup blob URL on unmount
        return () => {
            if (pdfUrl) URL.revokeObjectURL(pdfUrl);
        };
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [offerId]);

    const handleDownload = () => {
        if (!pdfUrl || !offer) return;
        const a = document.createElement("a");
        a.href = pdfUrl;
        a.download = `offer-${offer.candidateName.replace(/\s+/g, "-")}.pdf`;
        a.click();
    };

    return (
        <div className="space-y-6">
            <PageHeader
                title="Offer PDF Preview"
                description={offer ? `Preview for ${offer.candidateName}` : "Loading..."}
                icon={FileText}
                actions={
                    <div className="flex gap-2">
                        <Button variant="outline" onClick={() => navigate(-1)}>
                            <ChevronLeft className="mr-1 h-4 w-4" /> Back
                        </Button>
                        {offer && (
                            <Button
                                variant="outline"
                                onClick={() => navigate(`/offers/edit/${offer.id}`)}
                                className="gap-2"
                            >
                                <Edit2 className="h-4 w-4" /> Edit Offer
                            </Button>
                        )}
                        <Button
                            onClick={handleDownload}
                            disabled={!pdfUrl}
                            variant="outline"
                            className="gap-2"
                        >
                            <Download className="h-4 w-4" /> Download PDF
                        </Button>
                        {offer && offer.status === "APPROVED" && (
                            <Button
                                onClick={() => navigate(`/offers/${offerId}/send`)}
                                className="gap-2 bg-indigo-600 hover:bg-indigo-700"
                            >
                                <Send className="h-4 w-4" /> Send Offer
                            </Button>
                        )}
                    </div>
                }
            />

            {/* PDF Iframe Viewer */}
            <div className="overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm">
                {isLoading ? (
                    <div className="flex h-[700px] items-center justify-center">
                        <div className="flex flex-col items-center gap-3 text-slate-400">
                            <Loader2 className="h-8 w-8 animate-spin text-indigo-500" />
                            <p className="text-sm">Loading PDF preview...</p>
                        </div>
                    </div>
                ) : pdfUrl ? (
                    <iframe
                        src={pdfUrl}
                        title="Offer PDF Preview"
                        className="h-[700px] w-full"
                    />
                ) : (
                    <div className="flex h-[400px] items-center justify-center bg-slate-50">
                        {/* Fallback: HTML preview of offer details */}
                        {offer && (
                            <div className="mx-auto max-w-lg rounded-xl border border-slate-200 bg-white p-10 shadow print:shadow-none">
                                <div className="mb-6 flex items-center gap-3">
                                    <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-indigo-600 text-white font-bold text-lg">
                                        ATS
                                    </div>
                                    <div>
                                        <p className="text-lg font-bold text-slate-800">Job Offer Letter</p>
                                        <p className="text-sm text-slate-500">ATS Enterprise</p>
                                    </div>
                                </div>
                                <hr className="mb-6 border-slate-200" />
                                <p className="mb-4 text-sm text-slate-600">
                                    Dear <strong>{offer.candidateName}</strong>,
                                </p>
                                <p className="mb-4 text-sm text-slate-600">
                                    We are pleased to offer you the position of{" "}
                                    <strong>{offer.positionTitle}</strong> with a monthly salary of{" "}
                                    <strong>
                                        {new Intl.NumberFormat("vi-VN", {
                                            style: "currency",
                                            currency: "VND",
                                        }).format(offer.salary)}
                                    </strong>
                                    .
                                </p>
                                {offer.startDate && (
                                    <p className="mb-4 text-sm text-slate-600">
                                        Your expected start date is{" "}
                                        <strong>{new Date(offer.startDate).toLocaleDateString()}</strong>.
                                    </p>
                                )}
                                {offer.benefits && (
                                    <p className="mb-4 text-sm text-slate-600">
                                        Benefits include: {offer.benefits}
                                    </p>
                                )}
                                <div className="mt-8 flex justify-between text-sm">
                                    <div>
                                        <p className="font-medium text-slate-800">HR Department</p>
                                        <div className="mt-8 h-px w-32 bg-slate-300" />
                                        <p className="mt-1 text-xs text-slate-400">Authorized Signature</p>
                                    </div>
                                    <div className="text-right">
                                        <p className="font-medium text-slate-800">{offer.candidateName}</p>
                                        <div className="mt-8 h-px w-32 bg-slate-300 ml-auto" />
                                        <p className="mt-1 text-xs text-slate-400">Candidate Signature</p>
                                    </div>
                                </div>
                            </div>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
}
