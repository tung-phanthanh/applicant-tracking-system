import { cn } from "@/lib/utils";

const STEPS = [
    { step: 1, label: "Job details", short: "Step 1" },
    { step: 2, label: "Review", short: "Step 2" },
] as const;

interface CreateJobStepNavProps {
    currentStep: number;
    onStepClick?: (step: number) => void;
}

export default function CreateJobStepNav({
    currentStep,
    onStepClick,
}: CreateJobStepNavProps) {
    return (
        <nav aria-label="Progress" className="mb-8">
            <ol className="flex flex-col gap-4 md:flex-row md:gap-8">
                {STEPS.map(({ step, label, short }) => {
                    const isActive = step === currentStep;
                    const isDone = step < currentStep;
                    return (
                        <li key={step} className="md:flex-1">
                            <button
                                type="button"
                                onClick={() => onStepClick?.(step)}
                                className={cn(
                                    "group flex w-full flex-col border-l-4 py-2 pl-4 text-left transition-colors md:border-l-0 md:border-t-4 md:pb-0 md:pl-0 md:pt-4",
                                    isActive || isDone
                                        ? "border-primary"
                                        : "border-border hover:border-muted-foreground/40",
                                )}
                            >
                                <span
                                    className={cn(
                                        "text-sm font-medium",
                                        isActive || isDone
                                            ? "text-foreground"
                                            : "text-muted-foreground",
                                    )}
                                >
                                    {short}
                                </span>
                                <span className="text-sm font-medium text-foreground">
                                    {label}
                                </span>
                            </button>
                        </li>
                    );
                })}
            </ol>
        </nav>
    );
}
