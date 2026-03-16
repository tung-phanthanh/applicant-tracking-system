import { useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { ArrowLeft, Download, Send, Building2 } from "lucide-react";
import { Button } from "@/components/ui/button";

export default function OfferPreviewPage() {
    const { id: _id } = useParams<{ id: string }>();
    const navigate = useNavigate();
    const [isSending, setIsSending] = useState(false);

    // Mock data matching test/offer-preview.html
    const offer = {
        candidateName: "Sarah Jenkins",
        positionTitle: "Senior Frontend Developer",
        salary: 120000,
        equity: 5000,
        bonus: 15000,
        startDate: "November 15, 2023",
        companyName: "Enterprise ATS Co.",
    };

    const handleSend = async () => {
        setIsSending(true);
        await new Promise((r) => setTimeout(r, 800));
        setIsSending(false);
        navigate("/offers/approvals");
    };

    return (
        <div className="mx-auto max-w-3xl space-y-6">
            {/* Actions bar */}
            <div className="flex items-center justify-between">
                <Button variant="ghost" onClick={() => navigate(-1)}>
                    <ArrowLeft className="mr-1.5 h-4 w-4" /> Back
                </Button>
                <div className="flex gap-2">
                    <Button variant="outline" size="sm">
                        <Download className="mr-1.5 h-4 w-4" /> Download PDF
                    </Button>
                    <Button size="sm" onClick={handleSend} disabled={isSending}>
                        <Send className="mr-1.5 h-4 w-4" />
                        {isSending ? "Sending…" : "Send Offer"}
                    </Button>
                </div>
            </div>

            {/* Offer Letter Preview */}
            <div className="rounded-lg border border-border bg-white p-10 shadow-sm">
                {/* Company Header */}
                <div className="flex items-center gap-3 border-b border-border pb-6 mb-6">
                    <div className="flex h-12 w-12 items-center justify-center rounded-full bg-primary/10">
                        <Building2 className="h-6 w-6 text-primary" />
                    </div>
                    <div>
                        <h2 className="text-lg font-bold text-foreground">{offer.companyName}</h2>
                        <p className="text-sm text-muted-foreground">Official Offer Letter</p>
                    </div>
                </div>

                {/* Body */}
                <div className="space-y-4 text-sm leading-relaxed text-foreground">
                    <p>Dear <strong>{offer.candidateName}</strong>,</p>
                    <p>
                        We are excited to offer you the position of <strong>{offer.positionTitle}</strong> at {offer.companyName}.
                        After a thorough evaluation, we believe your skills and experience make you an excellent fit for our team.
                    </p>

                    <div className="my-6 rounded-md bg-muted/30 p-4">
                        <h4 className="text-sm font-semibold text-foreground mb-2">Compensation Package</h4>
                        <ul className="list-disc ml-5 space-y-1 text-sm text-muted-foreground">
                            <li>Base Salary: <strong className="text-foreground">${offer.salary.toLocaleString()}</strong> per year</li>
                            <li>Stock Options: <strong className="text-foreground">${offer.equity.toLocaleString()}</strong></li>
                            <li>Sign-On Bonus: <strong className="text-foreground">${offer.bonus.toLocaleString()}</strong></li>
                        </ul>
                    </div>

                    <div className="my-6 rounded-md bg-muted/30 p-4">
                        <h4 className="text-sm font-semibold text-foreground mb-2">Benefits</h4>
                        <ul className="list-disc ml-5 space-y-1 text-sm text-muted-foreground">
                            <li>Comprehensive health, dental, and vision insurance</li>
                            <li>401(k) with company match</li>
                            <li>Unlimited PTO</li>
                            <li>Professional development budget</li>
                        </ul>
                    </div>

                    <p>
                        Your anticipated start date is <strong>{offer.startDate}</strong>.
                        Please review the terms above and confirm your acceptance.
                    </p>

                    <div className="mt-8 space-y-3">
                        <p>Sincerely,</p>
                        <p className="text-sm font-semibold text-foreground">HR Department</p>
                        <p className="text-xs text-muted-foreground">{offer.companyName}</p>
                    </div>
                </div>

                {/* Signature section */}
                <div className="mt-10 rounded-md border border-dashed border-border p-6">
                    <h4 className="text-sm font-semibold text-foreground mb-3">Candidate Acceptance</h4>
                    <div className="flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
                        <div className="flex-1">
                            <label className="text-xs text-muted-foreground">Digital Signature</label>
                            <div className="mt-1 h-16 rounded-md border border-input bg-background" />
                        </div>
                        <div className="text-right">
                            <label className="text-xs text-muted-foreground">Date</label>
                            <p className="text-sm text-foreground">_________________</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
