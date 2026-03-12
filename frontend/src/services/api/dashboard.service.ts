import api from '@/lib/axios';

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
        return api.get('/dashboard/stats');
    }
};
