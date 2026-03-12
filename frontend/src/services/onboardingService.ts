import { apiFetch } from "@/lib/api";
import type {
    OnboardingChecklistResponse,
    OnboardingItem,
} from "@/types/models";

export interface CreateChecklistRequest {
    items: { taskName: string; assignedTo?: string; dueDate?: string }[];
}

export interface UpdateItemRequest {
    taskName?: string;
    assignedTo?: string;
    dueDate?: string;
    status?: "PENDING" | "IN_PROGRESS" | "DONE";
}

export const onboardingService = {
    getChecklist(applicationId: number) {
        return apiFetch<OnboardingChecklistResponse>(
            `/onboarding/${applicationId}/checklist`,
        );
    },

    createChecklist(applicationId: number, body: CreateChecklistRequest) {
        return apiFetch<OnboardingChecklistResponse>(
            `/onboarding/${applicationId}/checklist`,
            { method: "POST", body },
        );
    },

    updateItem(itemId: number, body: UpdateItemRequest) {
        return apiFetch<OnboardingItem>(`/onboarding/items/${itemId}`, {
            method: "PUT",
            body,
        });
    },
};
