import { Badge } from "@/components/ui/badge";
import { cn } from "@/lib/utils";
import type { JobApiStatus } from "@/types/job";

interface JobStatusBadgeProps {
    status: JobApiStatus;
    className?: string;
}

export default function JobStatusBadge({ status, className }: JobStatusBadgeProps) {
    const label = statusLabel(status);
    const variantClass = statusVariantClass(status);

    return (
        <Badge
            variant="secondary"
            className={cn(
                "rounded-full border-0 font-semibold",
                variantClass,
                className,
            )}
        >
            {label}
        </Badge>
    );
}

function statusLabel(status: JobApiStatus): string {
    switch (status) {
        case "PENDING":
            return "Pending approval";
        case "APPROVED":
            return "Published";
        case "REJECTED":
            return "Rejected";
        case "DRAFT":
            return "Draft";
        case "CLOSED":
            return "Closed";
        default:
            return status;
    }
}

function statusVariantClass(status: JobApiStatus): string {
    switch (status) {
        case "APPROVED":
            return "bg-primary/15 text-primary";
        case "PENDING":
            return "bg-muted text-muted-foreground";
        case "REJECTED":
            return "bg-destructive/15 text-destructive";
        case "DRAFT":
            return "bg-secondary text-secondary-foreground";
        case "CLOSED":
            return "bg-muted text-muted-foreground";
        default:
            return "bg-muted text-muted-foreground";
    }
}
