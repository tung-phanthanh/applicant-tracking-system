import { useEffect, useState } from "react";
import { notificationService } from "@/services/api/notification.service";
import type { Notification } from "@/types";
import { Bell, Check, CheckCircle2, AlertCircle } from "lucide-react";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";

export default function NotificationsPage() {
    const [notifications, setNotifications] = useState<Notification[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const fetchNotifications = async () => {
        try {
            setLoading(true);
            const data = await notificationService.getNotifications();
            setNotifications(data);
            setError(null);
        } catch (err: unknown) {
            console.error("Failed to load notifications:", err);
            setError("Could not load notifications. Please try again.");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchNotifications();
    }, []);

    const handleMarkAsRead = async (id: string) => {
        try {
            await notificationService.markAsRead(id);
            setNotifications((prev) =>
                prev.map((n) => (n.id === id ? { ...n, read: true } : n))
            );
        } catch (err) {
            console.error("Failed to mark as read:", err);
        }
    };

    const handleMarkAllAsRead = async () => {
        try {
            await notificationService.markAllAsRead();
            setNotifications((prev) =>
                prev.map((n) => ({ ...n, read: true }))
            );
        } catch (err) {
            console.error("Failed to mark all as read:", err);
        }
    };

    return (
        <div className="max-w-4xl mx-auto space-y-6">
            <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
                <div>
                    <h1 className="text-2xl font-bold tracking-tight text-foreground flex items-center gap-2">
                        <Bell className="h-6 w-6 text-primary" />
                        Notification Center
                    </h1>
                    <p className="text-muted-foreground mt-1">Stay updated with the latest system activities.</p>
                </div>

                <div className="flex gap-2">
                    <Button 
                        variant="outline" 
                        onClick={handleMarkAllAsRead}
                        disabled={notifications.every(n => n.read) || loading}
                        className="shrink-0"
                    >
                        <CheckCircle2 className="mr-2 h-4 w-4" />
                        Mark all as read
                    </Button>
                </div>
            </div>

            {error && (
                <div className="rounded-md border border-destructive/50 bg-destructive/10 p-4 text-destructive flex items-center gap-3">
                    <AlertCircle className="h-5 w-5" />
                    <div>
                        <h3 className="font-medium">Error</h3>
                        <p className="text-sm">{error}</p>
                    </div>
                </div>
            )}

            <div className="rounded-lg border border-border bg-card shadow-sm overflow-hidden">
                {loading ? (
                    <div className="flex justify-center p-8">
                        <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent"></div>
                    </div>
                ) : notifications.length === 0 ? (
                    <div className="p-12 text-center text-muted-foreground">
                        <Bell className="mx-auto h-12 w-12 opacity-20 mb-4" />
                        <p className="text-lg font-medium">No notifications yet</p>
                        <p className="text-sm">When you get notifications, they'll show up here.</p>
                    </div>
                ) : (
                    <div className="divide-y divide-border">
                        {notifications.map((notification) => (
                            <div 
                                key={notification.id} 
                                className={cn(
                                    "p-4 sm:p-6 flex gap-4 transition-colors hover:bg-muted/50",
                                    !notification.read ? "bg-primary/5" : "bg-card"
                                )}
                            >
                                <div className={cn(
                                    "mt-1 shrink-0 h-2 w-2 rounded-full",
                                    !notification.read ? "bg-primary shadow-[0_0_8px_rgba(var(--primary),0.8)]" : "bg-transparent"
                                )} />
                                
                                <div className="flex-1 space-y-1">
                                    <h4 className={cn(
                                        "text-base",
                                        !notification.read ? "font-semibold text-foreground" : "font-medium text-muted-foreground"
                                    )}>
                                        {notification.title}
                                    </h4>
                                    <p className="text-sm text-muted-foreground line-clamp-2">
                                        {notification.message}
                                    </p>
                                    <p className="text-xs text-muted-foreground/80 mt-2 block">
                                        {new Date(notification.createdAt).toLocaleDateString()} {new Date(notification.createdAt).toLocaleTimeString()}
                                    </p>
                                </div>

                                {!notification.read && (
                                    <Button
                                        variant="ghost"
                                        size="sm"
                                        className="shrink-0 text-muted-foreground hover:text-primary"
                                        onClick={() => handleMarkAsRead(notification.id)}
                                        title="Mark as read"
                                    >
                                        <Check className="h-4 w-4" />
                                        <span className="sr-only">Mark as read</span>
                                    </Button>
                                )}
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
}
