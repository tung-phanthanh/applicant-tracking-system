import { useState } from "react";
import { Bell } from "lucide-react";

export function NotificationBell() {
    const [isOpen, setIsOpen] = useState(false);

    // Hardcode demo notifications for layout purposes
    const notifications = [
        {
            id: 1,
            title: "New Application Received",
            description: "John Doe applied for Frontend Developer",
            time: "5m ago",
            read: false,
        },
        {
            id: 2,
            title: "Offer Accepted",
            description: "Sarah Smith accepted the Senior Designer offer",
            time: "2h ago",
            read: true,
        },
        {
            id: 3,
            title: "Interview Scheduled",
            description: "System Design interview with Mike Johnson",
            time: "1d ago",
            read: true,
        },
    ];

    const unreadCount = notifications.filter((n) => !n.read).length;

    return (
        <div className="relative">
            <button
                onClick={() => setIsOpen(!isOpen)}
                className="relative flex h-9 w-9 items-center justify-center rounded-full text-slate-500 transition-colors hover:bg-slate-100 hover:text-slate-800"
            >
                <Bell className="h-5 w-5" />
                {unreadCount > 0 && (
                    <span className="absolute top-2 right-2 flex h-2 w-2 rounded-full bg-rose-500 ring-2 ring-white" />
                )}
            </button>

            {isOpen && (
                <div className="absolute right-0 mt-2 w-80 overflow-hidden rounded-xl border border-slate-200 bg-white shadow-lg ring-1 ring-black/5 z-50">
                    <div className="flex items-center justify-between border-b border-slate-100 px-4 py-3">
                        <h3 className="text-sm font-semibold text-slate-800">Notifications</h3>
                        {unreadCount > 0 && (
                            <button className="text-xs text-indigo-600 hover:text-indigo-700 font-medium">
                                Mark all as read
                            </button>
                        )}
                    </div>
                    <div className="max-h-[300px] overflow-y-auto">
                        {notifications.length === 0 ? (
                            <div className="p-4 text-center text-sm text-slate-500">
                                No notifications
                            </div>
                        ) : (
                            <ul className="divide-y divide-slate-100">
                                {notifications.map((notification) => (
                                    <li
                                        key={notification.id}
                                        className={`flex items-start gap-3 p-4 transition-colors hover:bg-slate-50 ${!notification.read ? "bg-indigo-50/30" : ""
                                            }`}
                                    >
                                        {!notification.read && (
                                            <span className="mt-1.5 h-2 w-2 shrink-0 rounded-full bg-indigo-500" />
                                        )}
                                        <div className="flex-1 space-y-1">
                                            <p
                                                className={`text-sm ${!notification.read
                                                        ? "font-medium text-slate-900"
                                                        : "text-slate-700"
                                                    }`}
                                            >
                                                {notification.title}
                                            </p>
                                            <p className="text-xs text-slate-500">
                                                {notification.description}
                                            </p>
                                            <p className="text-xs text-slate-400">
                                                {notification.time}
                                            </p>
                                        </div>
                                    </li>
                                ))}
                            </ul>
                        )}
                    </div>
                </div>
            )}
        </div>
    );
}
