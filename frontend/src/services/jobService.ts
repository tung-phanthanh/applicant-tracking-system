import api from "@/lib/api";
import type { JobDTO, JobOption } from "@/types/job";

export const jobService = {
    async getJobs(): Promise<JobOption[]> {
        const { data } = await api.get<JobDTO[]>("/jobs");
        return data.map((job) => ({
            jobId: job.id,
            title: job.title,
            status: job.status,
        }));
    },
};
