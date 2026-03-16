export interface OnboardingTask {
    id: string;
    applicationId: string;
    title: string;
    category: string;
    assignedTo: string;
    dueDate: string;
    completed: boolean;
    sortOrder: number;
}

export interface OnboardingProgress {
    totalTasks: number;
    completedTasks: number;
    progressPercent: number;
    tasks: OnboardingTask[];
}

export interface CreateOnboardingTaskPayload {
    applicationId: string;
    title: string;
    category: string;
    assignedTo: string;
    dueDate: string;
}

export interface UpdateOnboardingTaskPayload {
    title?: string;
    category?: string;
    assignedTo?: string;
    dueDate?: string;
    completed?: boolean;
}
