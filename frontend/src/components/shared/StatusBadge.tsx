import { cn } from "@/lib/utils";
import type {
    OfferStatus,
    ApplicationStage,
    HiringRecommendation,
    OnboardingItemStatus,
} from "@/types/models";

type StatusValue =
    | OfferStatus
    | ApplicationStage
    | HiringRecommendation
    | OnboardingItemStatus
    | string;

interface StatusBadgeProps {
    status: StatusValue;
    className?: string;
}

const STATUS_CONFIG: Record<string, { label: string; className: string }> = {
    // Offer statuses
    DRAFT: {
        label: "Draft",
        className: "bg-slate-100 text-slate-700 border-slate-300",
    },
    PENDING_APPROVAL: {
        label: "Pending Approval",
        className: "bg-amber-50 text-amber-700 border-amber-300",
    },
    APPROVED: {
        label: "Approved",
        className: "bg-emerald-50 text-emerald-700 border-emerald-300",
    },
    REJECTED: {
        label: "Rejected",
        className: "bg-rose-50 text-rose-700 border-rose-300",
    },
    SENT: {
        label: "Sent",
        className: "bg-sky-50 text-sky-700 border-sky-300",
    },

    // Application stages
    APPLIED: {
        label: "Applied",
        className: "bg-gray-50 text-gray-700 border-gray-300",
    },
    SCREENING: {
        label: "Screening",
        className: "bg-blue-50 text-blue-700 border-blue-300",
    },
    INTERVIEW: {
        label: "Interview",
        className: "bg-purple-50 text-purple-700 border-purple-300",
    },
    OFFER: {
        label: "Offer",
        className: "bg-teal-50 text-teal-700 border-teal-300",
    },
    HIRED: {
        label: "Hired",
        className: "bg-emerald-100 text-emerald-800 border-emerald-400",
    },

    // Hiring recommendations
    STRONG_HIRE: {
        label: "Strong Hire",
        className: "bg-emerald-500 text-white border-emerald-600",
    },
    HIRE: {
        label: "Hire",
        className: "bg-emerald-100 text-emerald-800 border-emerald-300",
    },
    NEUTRAL: {
        label: "Neutral",
        className: "bg-slate-100 text-slate-700 border-slate-300",
    },
    NO_HIRE: {
        label: "No Hire",
        className: "bg-rose-100 text-rose-700 border-rose-300",
    },
    STRONG_NO_HIRE: {
        label: "Strong No Hire",
        className: "bg-rose-500 text-white border-rose-600",
    },

    // Onboarding item statuses
    PENDING: {
        label: "Pending",
        className: "bg-slate-100 text-slate-600 border-slate-300",
    },
    IN_PROGRESS: {
        label: "In Progress",
        className: "bg-blue-50 text-blue-700 border-blue-300",
    },
    DONE: {
        label: "Done",
        className: "bg-emerald-50 text-emerald-700 border-emerald-300",
    },

    // Interview status
    SCHEDULED: {
        label: "Scheduled",
        className: "bg-blue-50 text-blue-700 border-blue-300",
    },
    COMPLETED: {
        label: "Completed",
        className: "bg-emerald-50 text-emerald-700 border-emerald-300",
    },
    CANCELLED: {
        label: "Cancelled",
        className: "bg-rose-50 text-rose-700 border-rose-300",
    },
    WITHDRAWN: {
        label: "Withdrawn",
        className: "bg-slate-100 text-slate-500 border-slate-300",
    },
};

export function StatusBadge({ status, className }: StatusBadgeProps) {
    const config = STATUS_CONFIG[status] ?? {
        label: status,
        className: "bg-slate-100 text-slate-600 border-slate-300",
    };

    return (
        <span
            className={cn(
                "inline-flex items-center rounded-full border px-2.5 py-0.5 text-xs font-medium",
                config.className,
                className,
            )}
        >
            {config.label}
        </span>
    );
}
