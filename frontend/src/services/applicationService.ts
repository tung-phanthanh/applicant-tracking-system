import { apiFetch } from "@/lib/api";
import type { Application } from "@/types/models";

export const applicationService = {
    getAllApplications() {
        return apiFetch<Application[]>("/applications");
    },

    getRecentApplications(limit: number = 10) {
        return apiFetch<Application[]>(`/applications/recent?limit=${limit}`);
    },

    getApplicationById(id: number) {
        return apiFetch<Application>(`/applications/${id}`);
    }
};
