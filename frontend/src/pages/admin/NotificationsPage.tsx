import { useEffect, useState } from "react";
import { adminNotificationService } from "@/services/api/notification.service";
import { Send, CheckCircle2, AlertCircle, History, BarChart3, Mail, Users } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { cn } from "@/lib/utils";
import StatCard from "@/components/shared/StatCard";

export default function NotificationsPage() {
    const [stats, setStats] = useState<{ totalSent: number; delivered: number; failed: number; pending: number } | null>(null);
    const [history, setHistory] = useState<any[]>([]);
    
    // Form state
    const [title, setTitle] = useState("");
    const [message, setMessage] = useState("");
    const [sending, setSending] = useState(false);
    const [successMsg, setSuccessMsg] = useState<string | null>(null);

    useEffect(() => {
        adminNotificationService.getStats().then(setStats).catch(console.error);
        adminNotificationService.getHistory().then(setHistory).catch(console.error);
    }, []);

    const handleSend = async () => {
        if (!title.trim() || !message.trim()) return;
        setSending(true);
        try {
            await adminNotificationService.sendBulkNotification({ title, message });
            setSuccessMsg("Notification queued for delivery!");
            setTitle("");
            setMessage("");
            setTimeout(() => setSuccessMsg(null), 3000);
        } catch (e) {
            console.error("Failed to send notification", e);
        } finally {
            setSending(false);
        }
    };

    return (
        <div className="space-y-8">
            <div>
                <h1 className="text-2xl font-bold tracking-tight text-foreground flex items-center gap-2">
                    <Send className="h-6 w-6 text-primary" />
                    Notification Center (Admin)
                </h1>
                <p className="text-muted-foreground mt-1">Send bulk alerts, warnings, and updates to system users.</p>
            </div>

            {/* Stats */}
            {stats && (
                <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
                    <StatCard
                        title="Total Sent"
                        value={stats.totalSent}
                        icon={Mail}
                        iconBgClass="bg-blue-100 dark:bg-blue-900/30"
                        iconColorClass="text-blue-600 dark:text-blue-400"
                    />
                    <StatCard
                        title="Delivered"
                        value={stats.delivered}
                        icon={CheckCircle2}
                        iconBgClass="bg-green-100 dark:bg-green-900/30"
                        iconColorClass="text-green-600 dark:text-green-400"
                    />
                    <StatCard
                        title="Failed"
                        value={stats.failed}
                        icon={AlertCircle}
                        iconBgClass="bg-red-100 dark:bg-red-900/30"
                        iconColorClass="text-red-600 dark:text-red-400"
                    />
                    <StatCard
                        title="Pending"
                        value={stats.pending}
                        icon={History}
                        iconBgClass="bg-amber-100 dark:bg-amber-900/30"
                        iconColorClass="text-amber-600 dark:text-amber-400"
                    />
                </div>
            )}

            <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                {/* Send Form */}
                <div className="lg:col-span-1 border border-border bg-card rounded-lg shadow-sm p-6 space-y-5">
                    <h3 className="font-semibold text-lg flex items-center gap-2">
                        <Send className="h-5 w-5 text-indigo-500" />
                        Send Bulk Alert
                    </h3>

                    {successMsg && (
                        <div className="bg-green-50 text-green-700 p-3 rounded text-sm border border-green-200">
                            {successMsg}
                        </div>
                    )}

                    <div className="space-y-1.5">
                        <Label>Title</Label>
                        <Input
                            placeholder="e.g. System Maintenance"
                            value={title}
                            onChange={(e) => setTitle(e.target.value)}
                        />
                    </div>
                    
                    <div className="space-y-1.5">
                        <Label>Message content</Label>
                        <textarea
                            className="w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 min-h-[120px]"
                            placeholder="Write your message here..."
                            value={message}
                            onChange={(e) => setMessage(e.target.value)}
                        />
                    </div>

                    <div className="space-y-1.5">
                        <Label>Target Audience (Optional)</Label>
                        <div className="flex gap-2 text-sm text-muted-foreground bg-muted p-2 rounded items-center">
                            <Users className="h-4 w-4 shrink-0" />
                            Defaulting to 'All System Users'
                        </div>
                    </div>

                    <Button 
                        onClick={handleSend} 
                        disabled={!title.trim() || !message.trim() || sending} 
                        className="w-full gap-2"
                    >
                        <Send className="h-4 w-4" />
                        {sending ? "Queuing..." : "Send Notification"}
                    </Button>
                </div>

                {/* History */}
                <div className="lg:col-span-2 border border-border bg-card rounded-lg shadow-sm">
                    <div className="px-6 py-4 border-b border-border">
                        <h3 className="font-semibold text-lg flex items-center gap-2">
                            <BarChart3 className="h-5 w-5 text-indigo-500" />
                            Recent Send History
                        </h3>
                    </div>
                    
                    {history.length === 0 ? (
                        <div className="p-12 text-center text-muted-foreground flex flex-col items-center">
                            <History className="h-10 w-10 opacity-20 mb-3" />
                            <p>No bulk notifications sent yet.</p>
                        </div>
                    ) : (
                        <div className="divide-y divide-border">
                            {history.map((h, idx) => (
                                <div key={idx} className="p-4 px-6 gap-3 flex justify-between items-center group">
                                    <div className="min-w-0">
                                        <h4 className="font-medium text-foreground">{h.title}</h4>
                                        <p className="text-sm text-muted-foreground truncate max-w-lg mt-0.5">{h.message}</p>
                                    </div>
                                    <span className="text-xs font-mono text-muted-foreground shrink-0 hidden sm:block">
                                        {new Date().toLocaleDateString()}
                                    </span>
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}
