/**
 * Custom Hook: useAdminDashboard
 * Manages state and data fetching for admin dashboard
 */

import { useState, useEffect, useCallback } from 'react';
import {
  adminDashboardService,
  AdminAnalyticsDTO,
  SystemHealthDTO,
  UserManagementStatsDTO,
} from '../services/adminDashboardService';

interface AdminDashboardState {
  analytics: AdminAnalyticsDTO | null;
  health: SystemHealthDTO | null;
  userStats: UserManagementStatsDTO | null;
  loading: boolean;
  error: string | null;
  period: '7days' | '30days' | 'quarter' | 'default';
}

const initialState: AdminDashboardState = {
  analytics: null,
  health: null,
  userStats: null,
  loading: true,
  error: null,
  period: '30days',
};

export const useAdminDashboard = () => {
  const [state, setState] = useState<AdminDashboardState>(initialState);

  /**
   * Fetch all dashboard data
   */
  const fetchDashboardData = useCallback(async (period?: string) => {
    setState((prev) => ({ ...prev, loading: true, error: null }));
    try {
      const [analytics, health, userStats] = await Promise.all([
        adminDashboardService.getAnalytics(period || state.period),
        adminDashboardService.getSystemHealth(),
        adminDashboardService.getUserStats(),
      ]);

      setState((prev) => ({
        ...prev,
        analytics,
        health,
        userStats,
        loading: false,
        error: null,
      }));
    } catch (error) {
      const errorMessage =
        error instanceof Error ? error.message : 'Failed to load dashboard data';
      setState((prev) => ({
        ...prev,
        loading: false,
        error: errorMessage,
      }));
    }
  }, [state.period]);

  /**
   * Initial data load on component mount
   */
  useEffect(() => {
    fetchDashboardData();
  }, []);

  /**
   * Refetch dashboard data
   */
  const refetch = useCallback(async () => {
    await fetchDashboardData();
  }, [fetchDashboardData]);

  /**
   * Set time period for analytics
   */
  const setPeriod = useCallback(
    async (newPeriod: '7days' | '30days' | 'quarter' | 'default') => {
      setState((prev) => ({ ...prev, period: newPeriod }));
      await fetchDashboardData(newPeriod);
    },
    [fetchDashboardData]
  );

  /**
   * Reset all state to initial values
   */
  const reset = useCallback(() => {
    setState(initialState);
  }, []);

  return {
    ...state,
    refetch,
    setPeriod,
    reset,
  };
};

export default useAdminDashboard;
