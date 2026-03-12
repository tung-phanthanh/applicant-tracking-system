import { cn } from "@/lib/utils";

interface ScoreSelectorProps {
    value: number; // 1–5
    onChange?: (score: number) => void;
    readonly?: boolean;
    size?: "sm" | "md";
}

const SCORE_COLORS = [
    "", // placeholder for index 0
    "text-rose-600 border-rose-400 bg-rose-50",   // 1
    "text-orange-500 border-orange-400 bg-orange-50", // 2
    "text-amber-500 border-amber-400 bg-amber-50", // 3
    "text-lime-600 border-lime-500 bg-lime-50",   // 4
    "text-emerald-600 border-emerald-500 bg-emerald-50", // 5
];

const SCORE_LABELS = ["", "Very Poor", "Poor", "Average", "Good", "Excellent"];

export function ScoreSelector({
    value,
    onChange,
    readonly = false,
    size = "md",
}: ScoreSelectorProps) {
    const btnSize = size === "sm" ? "h-7 w-7 text-xs" : "h-9 w-9 text-sm";

    return (
        <div className="flex items-center gap-1.5">
            {[1, 2, 3, 4, 5].map((score) => {
                const isSelected = value === score;
                return (
                    <button
                        key={score}
                        type="button"
                        disabled={readonly}
                        onClick={() => onChange?.(score)}
                        title={SCORE_LABELS[score]}
                        className={cn(
                            "flex items-center justify-center rounded-full border-2 font-semibold transition-all duration-150",
                            btnSize,
                            isSelected
                                ? SCORE_COLORS[score]
                                : "border-slate-200 bg-white text-slate-400 hover:border-slate-400",
                            readonly && "cursor-default",
                            !readonly && !isSelected && "hover:bg-slate-50 cursor-pointer",
                        )}
                    >
                        {score}
                    </button>
                );
            })}
            {value > 0 && (
                <span className={cn("ml-1 text-xs font-medium", SCORE_COLORS[value].split(" ")[0])}>
                    {SCORE_LABELS[value]}
                </span>
            )}
        </div>
    );
}
