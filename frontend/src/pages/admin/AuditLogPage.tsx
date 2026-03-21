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
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        auditLogService.getLogs(actionParam).then((data: any[]) => {
            setLogs(
                data.map(d => {
                    // Parse newValue JSON to extract description
                    let description = '';
                    let detailData = null;
                    if (d.newValue) {
                        try {
                            const parsed = typeof d.newValue === 'string'
                                ? JSON.parse(d.newValue)
                                : d.newValue;
                            description = parsed.description || '';
                            detailData = parsed.data || null;
                        } catch {
                            description = String(d.newValue);
                        }
                    }

                    // Fallback detail from oldValue/newValue raw text
                    if (!description && (d.oldValue || d.newValue)) {
                        description = `${d.action} on ${d.entityType || 'Unknown'}`;
                    }

                    return {
                        id: d.id,
                        timestamp: d.createdAt,
                        actor: d.userFullName || 'System',
                        actorEmail: d.userEmail || 'system@ats.local',
                        action: d.action,
                        resource: d.entityType || 'Unknown',
                        resourceId: d.entityId || 'N/A',
                        detail: description,
                        detailData: detailData,
                        ipAddress: d.ipAddress || '127.0.0.1'
                    // eslint-disable-next-line @typescript-eslint/no-explicit-any
                    } as AuditLog & { detailData: any };
                })
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
                <div className="hidden grid-cols-[140px_160px_180px_1fr_110px] gap-4 border-b border-border bg-muted/50 px-5 py-3 text-xs font-semibold uppercase tracking-wider text-muted-foreground md:grid">
                    <span>Timestamp</span>
                    <span>Actor</span>
                    <span>Action</span>
                    <span>Description</span>
                    <span>IP Address</span>
                </div>

                {filtered.length === 0 && (
                    <div className="flex h-40 items-center justify-center text-sm text-muted-foreground">
                        No log entries match your filters.
                    </div>
                )}

                {/* eslint-disable-next-line @typescript-eslint/no-explicit-any */}
                {filtered.map((log: any) => (
                    <div key={log.id} className="border-b border-border last:border-0">
                        {/* Row */}
                        <div
                            className="grid cursor-pointer grid-cols-1 gap-2 px-5 py-3 transition-colors hover:bg-accent/50 md:grid-cols-[140px_160px_180px_1fr_110px] md:gap-4 md:items-center"
                            onClick={() => setExpanded(expanded === log.id ? null : log.id)}
                        >
                            <span className="text-xs text-muted-foreground font-mono whitespace-nowrap">
                                {formatTimestamp(log.timestamp)}
                            </span>
                            <div className="min-w-0">
                                <p className="text-sm font-medium text-foreground truncate">{log.actor}</p>
                                <p className="text-xs text-muted-foreground truncate">{log.actorEmail}</p>
                            </div>
                            <div className="flex flex-col gap-1">
                                <Badge variant="outline" className={cn("text-xs w-fit", ACTION_STYLES[log.action as AuditAction])}>
                                    {log.action}
                                </Badge>
                                <span className="text-xs text-muted-foreground">{log.resource}</span>
                            </div>
                            <p className="text-sm text-foreground truncate" title={log.detail}>
                                {log.detail || <span className="text-muted-foreground italic">No description</span>}
                            </p>
                            <span className="font-mono text-xs text-muted-foreground">{log.ipAddress}</span>
                        </div>

                        {/* Expanded Detail */}
                        {expanded === log.id && (
                            <div className="border-t border-border bg-muted/30 px-5 py-4 space-y-3">
                                {log.detail && (
                                    <div>
                                        <p className="text-xs font-semibold uppercase text-muted-foreground mb-1">Description</p>
                                        <p className="text-sm text-foreground font-medium">{log.detail}</p>
                                    </div>
                                )}
                                <div className="grid grid-cols-2 gap-4 text-xs">
                                    <div>
                                        <p className="font-semibold uppercase text-muted-foreground mb-1">Resource Type</p>
                                        <p className="text-foreground">{log.resource}</p>
                                    </div>
                                    <div>
                                        <p className="font-semibold uppercase text-muted-foreground mb-1">Resource ID</p>
                                        <p className="font-mono text-foreground">{log.resourceId}</p>
                                    </div>
                                    <div>
                                        <p className="font-semibold uppercase text-muted-foreground mb-1">Performed By</p>
                                        <p className="text-foreground">{log.actor} ({log.actorEmail})</p>
                                    </div>
                                    <div>
                                        <p className="font-semibold uppercase text-muted-foreground mb-1">IP Address</p>
                                        <p className="font-mono text-foreground">{log.ipAddress}</p>
                                    </div>
                                </div>
                                {log.detailData && (
                                    <div>
                                        <p className="text-xs font-semibold uppercase text-muted-foreground mb-1">Data Snapshot</p>
                                        <pre className="rounded bg-muted p-3 text-xs overflow-auto max-h-48 text-foreground">{JSON.stringify(log.detailData, null, 2)}</pre>
                                    </div>
                                )}
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
