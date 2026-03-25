import type { ReactNode } from "react";

interface CreateJobStaticStepProps {
    title: string;
    description: string;
    children?: ReactNode;
}

export default function CreateJobStaticStep({
    title,
    description,
    children,
}: CreateJobStaticStepProps) {
    return (
        <div className="space-y-6">
            <div>
                <h2 className="text-lg font-medium text-foreground">{title}</h2>
                <p className="mt-1 text-sm text-muted-foreground">{description}</p>
            </div>
            {children}
        </div>
    );
}
