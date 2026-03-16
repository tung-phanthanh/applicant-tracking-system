import api from "@/lib/api";
import type {
    OnboardingProgress,
    OnboardingTask,
    CreateOnboardingTaskPayload,
    UpdateOnboardingTaskPayload,
} from "@/types/onboarding";
import type { PageResponse } from "@/types/scorecard";

export const onboardingService = {
    async getProgress(applicationId: string): Promise<OnboardingProgress> {
        const { data } = await api.get<OnboardingProgress>(
            `/onboarding/application/${applicationId}/progress`
        );
        return data;
    },

    async getTasksPaged(applicationId: string, page = 0, size = 20): Promise<PageResponse<OnboardingTask>> {
        const { data } = await api.get<PageResponse<OnboardingTask>>(
            `/onboarding/application/${applicationId}?page=${page}&size=${size}`
        );
        return data;
    },

    async createTask(payload: CreateOnboardingTaskPayload): Promise<OnboardingTask> {
        const { data } = await api.post<OnboardingTask>("/onboarding", payload);
        return data;
    },

    async updateTask(id: string, payload: UpdateOnboardingTaskPayload): Promise<OnboardingTask> {
        const { data } = await api.put<OnboardingTask>(`/onboarding/${id}`, payload);
        return data;
    },

    async toggleTask(id: string): Promise<OnboardingTask> {
        const { data } = await api.patch<OnboardingTask>(`/onboarding/${id}/toggle`);
        return data;
    },
};
