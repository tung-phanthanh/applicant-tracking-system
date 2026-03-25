import {
    useMutation,
    useQuery,
    useQueryClient,
    type UseMutationResult,
    type UseQueryResult,
} from "@tanstack/react-query";
import {
    approveJob,
    createJob,
    fetchApprovedJobs,
    fetchJobById,
    fetchPendingJobs,
    rejectJob,
    updateJob,
} from "@/services/jobs/api";
import type { CreateJobPayload, JobDTO, UpdateJobPayload } from "@/types/job";

export const jobKeys = {
    all: ["jobs"] as const,
    approved: () => [...jobKeys.all, "approved"] as const,
    pending: () => [...jobKeys.all, "pending"] as const,
    detail: (id: string) => [...jobKeys.all, "detail", id] as const,
};

export function useApprovedJobsQuery(): UseQueryResult<JobDTO[], Error> {
    return useQuery({
        queryKey: jobKeys.approved(),
        queryFn: fetchApprovedJobs,
    });
}

export function usePendingJobsQuery(): UseQueryResult<JobDTO[], Error> {
    return useQuery({
        queryKey: jobKeys.pending(),
        queryFn: fetchPendingJobs,
    });
}

export function useJobQuery(id: string | undefined): UseQueryResult<JobDTO, Error> {
    return useQuery({
        queryKey: jobKeys.detail(id ?? ""),
        queryFn: () => fetchJobById(id as string),
        enabled: Boolean(id),
    });
}

export function useCreateJobMutation(): UseMutationResult<
    JobDTO,
    Error,
    CreateJobPayload
> {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: createJob,
        onSuccess: () => {
            void queryClient.invalidateQueries({ queryKey: jobKeys.all });
        },
    });
}

export function useUpdateJobMutation(): UseMutationResult<
    JobDTO,
    Error,
    { id: string; payload: UpdateJobPayload }
> {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({ id, payload }) => updateJob(id, payload),
        onSuccess: () => {
            void queryClient.invalidateQueries({ queryKey: jobKeys.all });
        },
    });
}

export function useApproveJobMutation(): UseMutationResult<JobDTO, Error, string> {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: approveJob,
        onSuccess: () => {
            void queryClient.invalidateQueries({ queryKey: jobKeys.all });
        },
    });
}

export function useRejectJobMutation(): UseMutationResult<JobDTO, Error, string> {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: rejectJob,
        onSuccess: () => {
            void queryClient.invalidateQueries({ queryKey: jobKeys.all });
        },
    });
}
