import api from '@/lib/api';
import type { Job, JobRequest } from '@/types/job';

export const jobApi = {
  getAll: () => api.get<Job[]>('/jobs'),
  getById: (id: number) => api.get<Job>(`/jobs/${id}`),
  create: (job: JobRequest) => api.post<Job>('/jobs', job),
  update: (id: number, job: JobRequest) => api.put<Job>(`/jobs/${id}`, job),
  delete: (id: number) => api.delete(`/jobs/${id}`),
};
