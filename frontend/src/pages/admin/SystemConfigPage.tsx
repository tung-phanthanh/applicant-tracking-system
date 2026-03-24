import { useState, useEffect } from "react";
import {
    Settings,
    Globe,
    Mail,
    Lock,
    Puzzle,
    Save,
    Check,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Switch } from "@/components/ui/switch";
import { cn } from "@/lib/utils";
import type { SystemConfigItem } from "@/types/index";
import { systemConfigService } from "@/services/api/systemConfig.service";

const INITIAL_CONFIG: SystemConfigItem[] = [
    // General
    { key: "company_name", label: "Company Name", description: "Displayed across the application", value: "Acme Corp", type: "text", group: "General" },
    { key: "default_timezone", label: "Default Timezone", description: "Used for date/time display", value: "UTC+7 (Asia/Ho_Chi_Minh)", type: "text", group: "General" },
    { key: "max_job_postings", label: "Max Active Job Postings", description: "Maximum number of active job postings", value: 50, type: "number", group: "General" },
    { key: "candidate_expiry_days", label: "Candidate Expiry Period (days)", description: "Days before inactive candidates are archived", value: 180, type: "number", group: "General" },
    // Email
    { key: "email_notify_new_app", label: "New Application Alert", description: "Email recruiter when a new application arrives", value: true, type: "toggle", group: "Email" },
    { key: "email_notify_interview", label: "Interview Reminders", description: "Send email reminders 24h before interviews", value: true, type: "toggle", group: "Email" },
    { key: "email_notify_offer", label: "Offer Status Updates", description: "Email team when an offer is accepted or declined", value: true, type: "toggle", group: "Email" },
    { key: "email_digest_weekly", label: "Weekly Summary Digest", description: "Send a weekly hiring pipeline summary email", value: false, type: "toggle", group: "Email" },
    // Security
    { key: "mfa_required", label: "Require MFA", description: "Enforce multi-factor authentication for all users", value: false, type: "toggle", group: "Security" },
    { key: "session_timeout_mins", label: "Session Timeout (minutes)", description: "Auto-logout after inactivity", value: 60, type: "number", group: "Security" },
    { key: "password_min_length", label: "Minimum Password Length", description: "Minimum character count for passwords", value: 8, type: "number", group: "Security" },
    { key: "audit_log_retention_days", label: "Audit Log Retention (days)", description: "How long to store audit log entries", value: 365, type: "number", group: "Security" },
    // Integrations
    { key: "slack_notify", label: "Slack Notifications", description: "Send hiring events to Slack channel", value: false, type: "toggle", group: "Integrations" },
    { key: "calendar_sync", label: "Calendar Integration", description: "Sync interviews with Google/Outlook Calendar", value: true, type: "toggle", group: "Integrations" },
    { key: "linkedin_scraper", label: "LinkedIn Integration", description: "Enable LinkedIn profile scraping", value: false, type: "toggle", group: "Integrations" },
];

const GROUP_META: Record<string, { icon: React.ReactNode; label: string; description: string }> = {
    General: { icon: <Globe className="h-4 w-4" />, label: "General Settings", description: "Basic system preferences" },
    Email: { icon: <Mail className="h-4 w-4" />, label: "Email Notifications", description: "Control automated email alerts" },
    Security: { icon: <Lock className="h-4 w-4" />, label: "Security", description: "Authentication and session policies" },
    Integrations: { icon: <Puzzle className="h-4 w-4" />, label: "Integrations", description: "Third-party service connections" },
};

const GROUPS = ["General", "Email", "Security", "Integrations"] as const;

export default function SystemConfigPage() {
    const [config, setConfig] = useState<SystemConfigItem[]>(INITIAL_CONFIG);
    const [saved, setSaved] = useState(false);

    useEffect(() => {
        systemConfigService.getAllConfigs().then((data) => {
            const valueMap = new Map(data.map(d => [d.configKey, d.configValue]));
            setConfig((prev) => prev.map(c => {
                const serverVal = valueMap.get(c.key);
                if (serverVal !== undefined && serverVal !== null) {
                    let parsed: string | number | boolean = serverVal;
                    if (c.type === "number") parsed = Number(serverVal);
                    else if (c.type === "toggle") parsed = serverVal === "true";
                    return { ...c, value: parsed };
                }
                return c;
            }));
        }).catch(console.error);
    }, []);

    const updateValue = (key: string, value: SystemConfigItem["value"]) => {
        setConfig((prev) =>
            prev.map((item) => (item.key === key ? { ...item, value } : item)),
        );
        setSaved(false);
    };

    const handleSave = async () => {
        const payload: Record<string, string> = {};
        config.forEach(c => {
            payload[c.key] = String(c.value);
        });

        try {
            await systemConfigService.updateConfigs(payload);
            setSaved(true);
            setTimeout(() => setSaved(false), 3000);
        } catch (e) {
            console.error("Failed to save config:", e);
        }
    };

    return (
        <div className="space-y-6">
            {/* Header */}
            <div className="flex items-center justify-between">
                <div>
                    <h2 className="text-2xl font-bold text-foreground">System Configuration</h2>
                    <p className="mt-1 text-sm text-muted-foreground">
                        Manage system-wide settings and integrations.
                    </p>
                </div>
                <Button onClick={handleSave} className={cn("gap-2", saved && "bg-green-600 hover:bg-green-600")}>
                    {saved ? <Check className="h-4 w-4" /> : <Save className="h-4 w-4" />}
                    {saved ? "Saved!" : "Save Changes"}
                </Button>
            </div>

            {/* Section Cards */}
            <div className="space-y-6">
                {GROUPS.map((group) => {
                    const meta = GROUP_META[group];
                    const items = config.filter((c) => c.group === group);
                    return (
                        <div key={group} className="rounded-lg border border-border bg-card shadow-sm">
                            {/* Section Header */}
                            <div className="flex items-center gap-3 border-b border-border px-6 py-4">
                                <div className="flex h-8 w-8 items-center justify-center rounded-md bg-muted text-muted-foreground">
                                    {meta.icon}
                                </div>
                                <div>
                                    <h3 className="font-semibold text-card-foreground">{meta.label}</h3>
                                    <p className="text-xs text-muted-foreground">{meta.description}</p>
                                </div>
                            </div>

                            {/* Settings */}
                            <div className="divide-y divide-border">
                                {items.map((item) => (
                                    <div
                                        key={item.key}
                                        className="flex items-center justify-between gap-4 px-6 py-4"
                                    >
                                        <div className="min-w-0 flex-1">
                                            <Label htmlFor={item.key} className="text-sm font-medium text-foreground cursor-pointer">
                                                {item.label}
                                            </Label>
                                            <p className="mt-0.5 text-xs text-muted-foreground">{item.description}</p>
                                        </div>
                                        <div className="shrink-0">
                                            {item.type === "toggle" && (
                                                <Switch
                                                    id={item.key}
                                                    checked={item.value as boolean}
                                                    onCheckedChange={(v) => updateValue(item.key, v)}
                                                />
                                            )}
                                            {item.type === "text" && (
                                                <Input
                                                    id={item.key}
                                                    value={item.value as string}
                                                    onChange={(e) => updateValue(item.key, e.target.value)}
                                                    className="w-52"
                                                />
                                            )}
                                            {item.type === "number" && (
                                                <Input
                                                    id={item.key}
                                                    type="number"
                                                    min="0"
                                                    value={item.value as number}
                                                    onChange={(e) => updateValue(item.key, Number(e.target.value))}
                                                    className="w-28"
                                                />
                                            )}
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>
                    );
                })}
            </div>

            {/* Bottom Save */}
            <div className="flex justify-end">
                <Button onClick={handleSave} className={cn("gap-2", saved && "bg-green-600 hover:bg-green-600")}>
                    {saved ? <Check className="h-4 w-4" /> : <Settings className="h-4 w-4" />}
                    {saved ? "Saved!" : "Save All Settings"}
                </Button>
            </div>
        </div>
    );
}
