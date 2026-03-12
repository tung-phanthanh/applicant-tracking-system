import { useState, useEffect } from "react";
import {
    Briefcase,
    Calendar,
    CheckCheck,
    Gift,
    AlertTriangle,
    Settings,
    Filter,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";
import type { Notification, NotificationType } from "@/types/index";
import { notificationService } from "@/services/api/notification.service";

const TYPE_META: Record<NotificationType, { icon: React.ReactNode; color: string }> = {
    application: { icon: <Briefcase className="h-4 w-4" />, color: "bg-blue-50 text-blue-600" },
    interview: { icon: <Calendar className="h-4 w-4" />, color: "bg-purple-50 text-purple-600" },
    offer: { icon: <Gift className="h-4 w-4" />, color: "bg-green-50 text-green-600" },
    system: { icon: <Settings className="h-4 w-4" />, color: "bg-muted text-muted-foreground" },
    alert: { icon: <AlertTriangle className="h-4 w-4" />, color: "bg-yellow-50 text-yellow-600" },
};

type FilterTab = "all" | "unread" | "read";

function timeAgo(iso: string): string {
    const diff = Math.floor((Date.now() - new Date(iso).getTime()) / 1000);
    if (diff < 60) return "just now";
    if (diff < 3600) return `${Math.floor(diff / 60)}m ago`;
    if (diff < 86400) return `${Math.floor(diff / 3600)}h ago`;
    return `${Math.floor(diff / 86400)}d ago`;
}

export default function NotificationsPage() {
    const [notifications, setNotifications] = useState<Notification[]>([]);
    const [tab, setTab] = useState<FilterTab>("all");

    useEffect(() => {
        notificationService.getNotifications().then(setNotifications).catch(console.error);
    }, []);

    const unreadCount = notifications.filter((n) => !n.read).length;

    const markAllRead = async () => {
        try {
            await notificationService.markAllAsRead();
            setNotifications((prev) => prev.map((n) => ({ ...n, read: true })));
        } catch (e) { console.error(e); }
    };

    const markRead = async (id: string) => {
        try {
            await notificationService.markAsRead(id);
            setNotifications((prev) =>
                prev.map((n) => (n.id === id ? { ...n, read: true } : n)),
            );
        } catch (e) { console.error(e); }
    };

    const filtered = notifications.filter((n) => {
        if (tab === "unread") return !n.read;
        if (tab === "read") return n.read;
        return true;
    });

    const TABS: { key: FilterTab; label: string }[] = [
        { key: "all", label: `All (${notifications.length})` },
        { key: "unread", label: `Unread (${unreadCount})` },
        { key: "read", label: "Read" },
    ];

    return (
        <div className="space-y-6">
            {/* Header */}
            <div className="flex items-center justify-between">
                <div>
                    <h2 className="text-2xl font-bold text-foreground">Notification Center</h2>
                    <p className="mt-1 text-sm text-muted-foreground">
                        {unreadCount > 0
                            ? `You have ${unreadCount} unread notification${unreadCount > 1 ? "s" : ""}.`
                            : "You're all caught up!"}
                    </p>
                </div>
                {unreadCount > 0 && (
                    <Button variant="outline" onClick={markAllRead} className="gap-2">
                        <CheckCheck className="h-4 w-4" />
                        Mark all as read
                    </Button>
                )}
            </div>

            {/* Tab Filter */}
            <div className="flex gap-1 rounded-lg border border-border bg-muted p-1 w-fit">
                {TABS.map(({ key, label }) => (
                    <button
                        key={key}
                        onClick={() => setTab(key)}
                        className={cn(
                            "rounded-md px-4 py-1.5 text-sm font-medium transition-colors",
                            tab === key
                                ? "bg-card text-foreground shadow-sm"
                                : "text-muted-foreground hover:text-foreground",
                        )}
                    >
                        {label}
                    </button>
                ))}
            </div>

            {/* Notification List */}
            <div className="rounded-lg border border-border bg-card shadow-sm overflow-hidden">
                {filtered.length === 0 && (
                    <div className="flex h-48 flex-col items-center justify-center gap-2 text-muted-foreground">
                        <Filter className="h-8 w-8 opacity-30" />
                        <p className="text-sm">No notifications in this category.</p>
                    </div>
                )}

                {filtered.map((notif) => {
                    const meta = TYPE_META[notif.type];
                    return (
                        <div
                            key={notif.id}
                            className={cn(
                                "flex items-start gap-4 border-b border-border px-5 py-4 last:border-0 transition-colors",
                                !notif.read ? "bg-primary/[0.03]" : "bg-card",
                            )}
                        >
                            {/* Icon */}
                            <div className={cn("mt-0.5 flex h-9 w-9 shrink-0 items-center justify-center rounded-full", meta.color)}>
                                {meta.icon}
                            </div>

                            {/* Content */}
                            <div className="min-w-0 flex-1">
                                <div className="flex items-start justify-between gap-2">
                                    <div>
                                        <p className={cn("text-sm font-medium", !notif.read ? "text-foreground" : "text-muted-foreground")}>
                                            {notif.title}
                                        </p>
                                        <p className="mt-0.5 text-sm text-muted-foreground">{notif.message}</p>
                                    </div>
                                    <div className="flex shrink-0 flex-col items-end gap-1">
                                        <span className="text-xs text-muted-foreground whitespace-nowrap">
                                            {timeAgo(notif.createdAt)}
                                        </span>
                                        {!notif.read && (
                                            <span className="h-2 w-2 rounded-full bg-primary" />
                                        )}
                                    </div>
                                </div>
                                {!notif.read && (
                                    <button
                                        onClick={() => markRead(notif.id)}
                                        className="mt-2 text-xs font-medium text-primary hover:underline"
                                    >
                                        Mark as read
                                    </button>
                                )}
                            </div>
                        </div>
                    );
                })}
            </div>

            {/* Legend */}
            <div className="flex flex-wrap gap-3">
                {(Object.entries(TYPE_META) as [NotificationType, typeof TYPE_META[NotificationType]][]).map(([type, meta]) => (
                    <div key={type} className="flex items-center gap-1.5">
                        <div className={cn("flex h-5 w-5 items-center justify-center rounded-full text-xs", meta.color)}>
                            {meta.icon}
                        </div>
                        <span className="text-xs capitalize text-muted-foreground">{type}</span>
                    </div>
                ))}
            </div>
        </div>
    );
}
