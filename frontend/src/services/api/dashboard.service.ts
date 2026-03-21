import api from '@/lib/api';

export interface DashboardStats {
    activeJobs: number;
    newCandidates: number;
    interviewsToday: number;
    offersSent: number;
    hiringPipeline: Record<string, number>;
    recentApplications: Record<string, unknown>[];
    todaysInterviews: Record<string, unknown>[];
}

export const dashboardService = {
    getStats: async (): Promise<DashboardStats> => {
        const { data } = await api.get('/dashboard/stats');
        return data;
    }
};
