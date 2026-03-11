import { useState, useEffect } from "react";
import { Search, ChevronDown } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Input } from "@/components/ui/input";
import { cn } from "@/lib/utils";
import type { AuditAction, AuditLog } from "@/types/index";
import { auditLogService } from "@/services/api/auditLog.service";

const ACTION_STYLES: Record<AuditAction, string> = {
    CREATE: "bg-green-50 text-green-700 ring-1 ring-green-600/20",
    UPDATE: "bg-blue-50 text-blue-700 ring-1 ring-blue-700/10",
    DELETE: "bg-red-50 text-red-700 ring-1 ring-red-600/20",
    LOGIN: "bg-muted text-muted-foreground ring-1 ring-border",
    LOGOUT: "bg-muted text-muted-foreground ring-1 ring-border",
    EXPORT: "bg-purple-50 text-purple-700 ring-1 ring-purple-700/10",
};

const ALL_ACTIONS: AuditAction[] = ["CREATE", "UPDATE", "DELETE", "LOGIN", "LOGOUT", "EXPORT"];

function formatTimestamp(iso: string): string {
    const d = new Date(iso);
    return d.toLocaleString("en-GB", {
        day: "2-digit",
        month: "short",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
    });
}

export default function AuditLogPage() {
    const [logs, setLogs] = useState<AuditLog[]>([]);
    const [search, setSearch] = useState("");
    const [selectedAction, setSelectedAction] = useState<AuditAction | "ALL">("ALL");
    const [showFilterDropdown, setShowFilterDropdown] = useState(false);
    const [expanded, setExpanded] = useState<string | null>(null);

    useEffect(() => {
        const actionParam = selectedAction === "ALL" ? undefined : selectedAction;
        auditLogService.getLogs(actionParam).then((data: { id: string, timestamp: string, actorUsername?: string, actorEmail?: string, action: string, resource?: string, resourceId?: string, detail?: string, ipAddress?: string }[]) => {
            setLogs(
                data.map(d => ({
                    id: d.id,
                    timestamp: d.timestamp,
                    actor: d.actorUsername || 'System',
                    actorEmail: d.actorEmail || 'system@ats.local',
                    action: d.action,
                    resource: d.resource || 'Unknown',
                    resourceId: d.resourceId || 'N/A',
                    detail: d.detail || '',
                    ipAddress: d.ipAddress || '127.0.0.1'
                } as AuditLog))
            );
        }).catch(console.error);
    }, [selectedAction]);

    const filtered: AuditLog[] = logs.filter((log) => {
        const q = search.toLowerCase();
        return (
            !q ||
            log.actor.toLowerCase().includes(q) ||
            log.resource.toLowerCase().includes(q) ||
            log.detail.toLowerCase().includes(q) ||
            log.actorEmail.toLowerCase().includes(q)
        );
    });

    return (
        <div className="space-y-6">
            {/* Header */}
            <div>
                <h2 className="text-2xl font-bold text-foreground">Audit Log</h2>
                <p className="mt-1 text-sm text-muted-foreground">
                    A complete trail of user actions and system events.
                </p>
            </div>

            {/* Filters */}
            <div className="flex flex-wrap gap-3">
                <div className="relative flex-1 min-w-48">
                    <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
                    <Input
                        className="pl-9"
                        placeholder="Search by actor, resource, or detail…"
                        value={search}
                        onChange={(e) => setSearch(e.target.value)}
                    />
                </div>
                <div className="relative">
                    <Button
                        variant="outline"
                        className="gap-2"
                        onClick={() => setShowFilterDropdown((v) => !v)}
                    >
                        {selectedAction === "ALL" ? "All Actions" : selectedAction}
                        <ChevronDown className="h-4 w-4" />
                    </Button>
                    {showFilterDropdown && (
                        <div className="absolute right-0 top-full z-20 mt-1 w-40 rounded-md border border-border bg-card shadow-lg">
                            {(["ALL", ...ALL_ACTIONS] as const).map((action) => (
                                <button
                                    key={action}
                                    className={cn(
                                        "flex w-full items-center gap-2 px-3 py-2 text-left text-sm hover:bg-accent",
                                        selectedAction === action && "bg-accent font-medium",
                                    )}
                                    onClick={() => {
                                        setSelectedAction(action as AuditAction | "ALL");
                                        setShowFilterDropdown(false);
                                    }}
                                >
                                    {action === "ALL" ? (
                                        <span className="text-muted-foreground">All Actions</span>
                                    ) : (
                                        <Badge variant="outline" className={cn("text-xs", ACTION_STYLES[action as AuditAction])}>
                                            {action}
                                        </Badge>
                                    )}
                                </button>
                            ))}
                        </div>
                    )}
                </div>
            </div>

            {/* Stats Row */}
            <div className="flex gap-4 flex-wrap">
                {ALL_ACTIONS.map((action) => {
                    const count = logs.filter((l) => l.action === action).length;
                    return (
                        <div key={action} className="flex items-center gap-1.5">
                            <Badge variant="outline" className={cn("text-xs", ACTION_STYLES[action])}>
                                {action}
                            </Badge>
                            <span className="text-sm text-muted-foreground">{count}</span>
                        </div>
                    );
                })}
            </div>

            {/* Log Table */}
            <div className="rounded-lg border border-border bg-card shadow-sm overflow-hidden">
                {/* Table Header */}
                <div className="hidden grid-cols-[140px_1fr_100px_1fr_120px] gap-4 border-b border-border bg-muted/50 px-5 py-3 text-xs font-semibold uppercase tracking-wider text-muted-foreground md:grid">
                    <span>Timestamp</span>
                    <span>Actor</span>
                    <span>Action</span>
                    <span>Resource</span>
                    <span>IP Address</span>
                </div>

                {filtered.length === 0 && (
                    <div className="flex h-40 items-center justify-center text-sm text-muted-foreground">
                        No log entries match your filters.
                    </div>
                )}

                {filtered.map((log) => (
                    <div key={log.id} className="border-b border-border last:border-0">
                        {/* Row */}
                        <div
                            className="grid cursor-pointer grid-cols-1 gap-2 px-5 py-3 transition-colors hover:bg-accent/50 md:grid-cols-[140px_1fr_100px_1fr_120px] md:gap-4 md:items-center"
                            onClick={() => setExpanded(expanded === log.id ? null : log.id)}
                        >
                            <span className="text-xs text-muted-foreground font-mono whitespace-nowrap">
                                {formatTimestamp(log.timestamp)}
                            </span>
                            <div>
                                <p className="text-sm font-medium text-foreground">{log.actor}</p>
                                <p className="text-xs text-muted-foreground">{log.actorEmail}</p>
                            </div>
                            <Badge variant="outline" className={cn("text-xs w-fit", ACTION_STYLES[log.action])}>
                                {log.action}
                            </Badge>
                            <div>
                                <p className="text-sm text-foreground">{log.resource}</p>
                                <p className="text-xs text-muted-foreground font-mono">{log.resourceId}</p>
                            </div>
                            <span className="font-mono text-xs text-muted-foreground">{log.ipAddress}</span>
                        </div>

                        {/* Expanded Detail */}
                        {expanded === log.id && (
                            <div className="border-t border-border bg-muted/30 px-5 py-3">
                                <p className="text-sm text-foreground">{log.detail}</p>
                            </div>
                        )}
                    </div>
                ))}
            </div>

            <p className="text-xs text-muted-foreground text-right">
                Showing {filtered.length} of {logs.length} entries
            </p>
        </div>
    );
}
