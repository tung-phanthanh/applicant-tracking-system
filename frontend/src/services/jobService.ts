import { apiFetch } from "@/lib/api";
import type { Job } from "@/types/models";

export const jobService = {
    getAllJobs() {
        return apiFetch<Job[]>("/jobs");
    },

    getJobById(id: number) {
        return apiFetch<Job>(`/jobs/${id}`);
    },

    getActiveJobsCount() {
        return apiFetch<number>("/jobs/active/count");
    }
};
