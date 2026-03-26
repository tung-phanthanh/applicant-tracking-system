import { cn } from "@/lib/utils";

export type JobDetailTabId = "overview" | "candidates";

interface JobDetailTabListProps {
    active: JobDetailTabId;
    onChange: (tab: JobDetailTabId) => void;
}

const TABS: { id: JobDetailTabId; label: string }[] = [
    { id: "overview", label: "Overview" },
    { id: "candidates", label: "Candidates" },
];

export default function JobDetailTabList({ active, onChange }: JobDetailTabListProps) {
    return (
        <div className="border-b border-border">
            <nav className="-mb-px flex gap-6 px-1" aria-label="Job sections">
                {TABS.map((tab) => (
                    <button
                        key={tab.id}
                        type="button"
                        onClick={() => onChange(tab.id)}
                        className={cn(
                            "whitespace-nowrap border-b-2 py-3 text-sm font-medium transition-colors",
                            active === tab.id
                                ? "border-primary text-foreground"
                                : "border-transparent text-muted-foreground hover:text-foreground",
                        )}
                    >
                        {tab.label}
                    </button>
                ))}
            </nav>
        </div>
    );
}
