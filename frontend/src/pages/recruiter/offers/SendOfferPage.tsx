import { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Mail, ChevronLeft, Send, Paperclip } from "lucide-react";
import { toast } from "sonner";
import { PageHeader } from "@/components/shared/PageHeader";
import { Button } from "@/components/ui/button";

export default function SendOfferPage() {
    const { offerId } = useParams<{ offerId: string }>();
    const navigate = useNavigate();
    const [isSending, setIsSending] = useState(false);
    const [subject, setSubject] = useState(
        "Job Offer — We'd Love You to Join Our Team!",
    );
    const [body, setBody] = useState(
        `Dear [Candidate Name],

We are excited to extend this offer of employment to you for the position of [Position Title] at our company.

Please review the attached offer letter and sign it by [Deadline Date]. If you have any questions, feel free to reach out to us.

We look forward to welcoming you to our team!

Best regards,
HR Department`,
    );

    const handleSend = async () => {
        if (!subject.trim() || !body.trim()) {
            toast.error("Subject and body are required");
            return;
        }
        setIsSending(true);
        try {
            // Real implementation would call a sendOffer API
            // For now we just simulate success after delay
            await new Promise((r) => setTimeout(r, 1500));
            toast.success("Offer email sent to candidate!");
            navigate(`/offers/${offerId}`);
        } catch {
            toast.error("Failed to send offer");
        } finally {
            setIsSending(false);
        }
    };

    return (
        <div className="space-y-6">
            <PageHeader
                title="Send Offer"
                description="Email the offer letter to the candidate"
                icon={Mail}
                actions={
                    <Button variant="outline" onClick={() => navigate(-1)}>
                        <ChevronLeft className="mr-1 h-4 w-4" /> Back
                    </Button>
                }
            />

            <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
                <div className="space-y-4">
                    {/* Email Subject */}
                    <div>
                        <label className="mb-1.5 block text-sm font-medium text-slate-700">
                            Email Subject
                        </label>
                        <input
                            type="text"
                            value={subject}
                            onChange={(e) => setSubject(e.target.value)}
                            className="w-full rounded-lg border border-slate-200 bg-slate-50 px-3 py-2 text-sm text-slate-800 outline-none transition focus:border-indigo-400 focus:bg-white focus:ring-2 focus:ring-indigo-100"
                        />
                    </div>

                    {/* Email Body */}
                    <div>
                        <label className="mb-1.5 block text-sm font-medium text-slate-700">
                            Email Body
                        </label>
                        <textarea
                            value={body}
                            onChange={(e) => setBody(e.target.value)}
                            rows={12}
                            className="w-full resize-none rounded-lg border border-slate-200 bg-slate-50 px-3 py-2 text-sm text-slate-800 font-mono outline-none transition focus:border-indigo-400 focus:bg-white focus:ring-2 focus:ring-indigo-100"
                        />
                    </div>

                    {/* Attachment */}
                    <div className="flex items-center gap-2 rounded-lg border border-slate-200 bg-slate-50 px-4 py-3">
                        <Paperclip className="h-4 w-4 text-slate-400" />
                        <span className="text-sm text-slate-600">
                            offer-letter-{offerId}.pdf
                        </span>
                        <span className="ml-auto text-xs text-emerald-600 font-medium">
                            Attached
                        </span>
                    </div>
                </div>
            </div>

            {/* Actions */}
            <div className="flex items-center justify-end gap-3 pb-4">
                <Button variant="outline" onClick={() => navigate(-1)}>
                    Cancel
                </Button>
                <Button
                    onClick={handleSend}
                    disabled={isSending}
                    className="gap-2 bg-indigo-600 hover:bg-indigo-700"
                >
                    {isSending ? (
                        <>Sending...</>
                    ) : (
                        <>
                            <Send className="h-4 w-4" /> Send Offer
                        </>
                    )}
                </Button>
            </div>
        </div>
    );
}
