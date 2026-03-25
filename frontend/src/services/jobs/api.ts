import api from "@/lib/api";
import type { CreateJobPayload, JobDTO, UpdateJobPayload } from "@/types/job";

export async function fetchApprovedJobs(): Promise<JobDTO[]> {
    const { data } = await api.get<JobDTO[]>("/jobs");
    return data;
}

export async function fetchPendingJobs(): Promise<JobDTO[]> {
    const { data } = await api.get<JobDTO[]>("/jobs/pending");
    return data;
}

export async function fetchJobById(id: string): Promise<JobDTO> {
    const { data } = await api.get<JobDTO>(`/jobs/${id}`);
    return data;
}

export async function createJob(payload: CreateJobPayload): Promise<JobDTO> {
    const { data } = await api.post<JobDTO>("/jobs", payload);
    return data;
}

export async function updateJob(
    id: string,
    payload: UpdateJobPayload,
): Promise<JobDTO> {
    const { data } = await api.put<JobDTO>(`/jobs/${id}`, payload);
    return data;
}

export async function approveJob(id: string): Promise<JobDTO> {
    const { data } = await api.put<JobDTO>(`/jobs/${id}/approve`);
    return data;
}

export async function rejectJob(id: string): Promise<JobDTO> {
    const { data } = await api.put<JobDTO>(`/jobs/${id}/reject`);
    return data;
}
