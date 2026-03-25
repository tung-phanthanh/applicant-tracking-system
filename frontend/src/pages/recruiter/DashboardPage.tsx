import { Briefcase, Calendar, CalendarOff, Users } from "lucide-react";
import StatCard from "@/components/shared/StatCard";
import { Badge } from "@/components/ui/badge";

interface Application {
    id: string;
    name: string;
    initials: string;
    position: string;
    status: "Review" | "Screening" | "Interview" | "Offer";
}

const RECENT_APPLICATIONS: Application[] = [
    {
        id: "1",
        name: "John Smith",
        initials: "JS",
        position: "Senior Backend Engineer",
        status: "Review",
    },
    {
        id: "2",
        name: "Alice Lee",
        initials: "AL",
        position: "Product Designer",
        status: "Screening",
    },
    {
        id: "3",
        name: "Michael Brown",
        initials: "MB",
        position: "Frontend Developer",
        status: "Interview",
    },
];

const STATUS_VARIANT: Record<
    Application["status"],
    "default" | "secondary" | "outline" | "destructive"
> = {
    Review: "secondary",
    Screening: "default",
    Interview: "outline",
    Offer: "default",
};

const STATUS_CLASS: Record<Application["status"], string> = {
    Review:
        "bg-yellow-50 text-yellow-800 ring-1 ring-yellow-600/20 hover:bg-yellow-50",
    Screening:
        "bg-blue-50 text-blue-700 ring-1 ring-blue-700/10 hover:bg-blue-50",
    Interview:
        "bg-purple-50 text-purple-700 ring-1 ring-purple-700/10 hover:bg-purple-50",
    Offer: "bg-green-50 text-green-700 ring-1 ring-green-700/10 hover:bg-green-50",
};

export default function DashboardPage() {
    return (
        <div className="space-y-8">
            {/* Stats Cards */}
            <div className="grid grid-cols-1 gap-4 md:grid-cols-3 md:gap-6">
                <StatCard
                    title="Active Jobs"
                    value={12}
                    icon={Briefcase}
                    iconBgClass="bg-blue-50"
                    iconColorClass="text-blue-600"
                />
                <StatCard
                    title="New Candidates"
                    value={48}
                    icon={Users}
                    iconBgClass="bg-green-50"
                    iconColorClass="text-green-600"
                />
                <StatCard
                    title="Interviews Today"
                    value={5}
                    icon={Calendar}
                    iconBgClass="bg-purple-50"
                    iconColorClass="text-purple-600"
                />
            </div>

            {/* Content Grid */}
            <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
                {/* Recent Applications */}
                <div className="rounded-lg border border-border bg-card p-6 shadow-sm">
                    <h3 className="mb-4 text-lg font-semibold text-card-foreground">
                        Recent Applications
                    </h3>
                    <div className="space-y-1">
                        {RECENT_APPLICATIONS.map((app) => (
                            <div
                                key={app.id}
                                className="flex items-center justify-between border-b border-border py-3 last:border-0"
                            >
                                <div className="flex items-center gap-3">
                                    <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-muted text-sm font-bold text-muted-foreground">
                                        {app.initials}
                                    </div>
                                    <div>
                                        <p className="text-sm font-medium text-foreground">
                                            {app.name}
                                        </p>
                                        <p className="text-xs text-muted-foreground">
                                            Applied for {app.position}
                                        </p>
                                    </div>
                                </div>
                                <Badge
                                    variant={STATUS_VARIANT[app.status]}
                                    className={STATUS_CLASS[app.status]}
                                >
                                    {app.status}
                                </Badge>
                            </div>
                        ))}
                    </div>
                </div>

                {/* Today's Interviews */}
                <div className="rounded-lg border border-border bg-card p-6 shadow-sm">
                    <h3 className="mb-4 text-lg font-semibold text-card-foreground">
                        Today's Interviews
                    </h3>
                    <div className="flex h-48 items-center justify-center text-muted-foreground">
                        <div className="text-center">
                            <CalendarOff className="mx-auto mb-2 h-10 w-10 opacity-30" />
                            <p className="text-sm">No remaining interviews today</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
