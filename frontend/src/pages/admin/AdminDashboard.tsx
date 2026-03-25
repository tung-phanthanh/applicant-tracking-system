/**
 * AdminDashboard Component
 * Main admin dashboard component with tabs for analytics, health, and user management
 */

import React, { useState, useMemo } from 'react';
import {
  BarChart3,
  Activity,
  Users,
  Download,
  RefreshCw,
  AlertCircle,
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { LoadingSkeleton } from '@/components/shared/LoadingSkeleton';
import { EmptyState } from '@/components/shared/EmptyState';
import { useAdminDashboard } from '@/hooks/useAdminDashboard';
import RecruitmentAnalyticsSection from './components/RecruitmentAnalyticsSection';
import SystemHealthSection from './components/SystemHealthSection';
import UserManagementSection from './components/UserManagementSection';

export interface AdminDashboardProps {
  className?: string;
}

type DashboardTab = 'analytics' | 'health' | 'users';
type ExportFormat = 'csv' | 'excel' | 'pdf' | 'json';
type AnalyticsPeriod = '7days' | '30days' | 'quarter' | 'default';

export const AdminDashboard: React.FC<AdminDashboardProps> = ({ className = '' }) => {
  const { analytics, health, userStats, loading, error, period, setPeriod, refetch } =
    useAdminDashboard();

  const [activeTab, setActiveTab] = useState<DashboardTab>('analytics');
  const [showExportMenu, setShowExportMenu] = useState(false);
  const [startDate, setStartDate] = useState<string>('');
  const [endDate, setEndDate] = useState<string>('');

  // Memoize tab content to prevent unnecessary re-renders
  const tabContent = useMemo(() => {
    if (loading) {
      return (
        <div className="space-y-4">
          <LoadingSkeleton count={3} height="h-64" />
        </div>
      );
    }

    if (error) {
      return (
        <div className="rounded-lg border border-red-200 bg-red-50 p-4">
          <div className="flex items-start gap-3">
            <AlertCircle className="h-5 w-5 text-red-600 flex-shrink-0 mt-0.5" />
            <div>
              <h3 className="font-semibold text-red-800">Error Loading Dashboard</h3>
              <p className="text-sm text-red-700 mt-1">{error}</p>
              <Button
                onClick={() => refetch()}
                variant="outline"
                size="sm"
                className="mt-3"
              >
                Retry
              </Button>
            </div>
          </div>
        </div>
      );
    }

    switch (activeTab) {
      case 'analytics':
        return analytics ? (
          <RecruitmentAnalyticsSection data={analytics} />
        ) : (
          <EmptyState title="No Analytics Data" description="Unable to load analytics data" />
        );
      case 'health':
        return health ? (
          <SystemHealthSection data={health} />
        ) : (
          <EmptyState title="No Health Data" description="Unable to load system health data" />
        );
      case 'users':
        return userStats ? (
          <UserManagementSection data={userStats} />
        ) : (
          <EmptyState title="No User Data" description="Unable to load user management data" />
        );
      default:
        return null;
    }
  }, [activeTab, analytics, health, userStats, loading, error, refetch]);

  const handleExport = async (format: ExportFormat) => {
    // TODO: Implement export functionality
    console.log(`Exporting dashboard data as ${format}`);
    setShowExportMenu(false);
  };

  const handlePeriodChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setPeriod(e.target.value as AnalyticsPeriod);
  };

  const tabs: Array<{ id: DashboardTab; label: string; icon: React.ReactNode }> = [
    {
      id: 'analytics',
      label: 'Analytics',
      icon: <BarChart3 className="h-4 w-4" />,
    },
    {
      id: 'health',
      label: 'System Health',
      icon: <Activity className="h-4 w-4" />,
    },
    {
      id: 'users',
      label: 'User Management',
      icon: <Users className="h-4 w-4" />,
    },
  ];

  return (
    <div className={`space-y-6 ${className}`}>
      {/* Header */}
      <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Admin Dashboard</h1>
          <p className="mt-1 text-gray-600">
            Comprehensive overview of your recruitment system
          </p>
        </div>

        <div className="flex flex-wrap gap-2">
          <Button
            onClick={() => refetch()}
            variant="outline"
            size="sm"
            className="gap-2"
            aria-label="Refresh dashboard data"
          >
            <RefreshCw className="h-4 w-4" />
            Refresh
          </Button>

          <div className="relative">
            <Button
              onClick={() => setShowExportMenu(!showExportMenu)}
              variant="outline"
              size="sm"
              className="gap-2"
              aria-label="Export dashboard data"
            >
              <Download className="h-4 w-4" />
              Export
            </Button>

            {showExportMenu && (
              <div className="absolute right-0 top-full mt-2 w-40 rounded-lg border border-gray-200 bg-white shadow-lg z-10">
                <button
                  onClick={() => handleExport('csv')}
                  className="block w-full px-4 py-2 text-left text-sm hover:bg-gray-50 first:rounded-t-lg"
                >
                  Export as CSV
                </button>
                <button
                  onClick={() => handleExport('excel')}
                  className="block w-full px-4 py-2 text-left text-sm hover:bg-gray-50"
                >
                  Export as Excel
                </button>
                <button
                  onClick={() => handleExport('pdf')}
                  className="block w-full px-4 py-2 text-left text-sm hover:bg-gray-50"
                >
                  Export as PDF
                </button>
                <button
                  onClick={() => handleExport('json')}
                  className="block w-full px-4 py-2 text-left text-sm hover:bg-gray-50 last:rounded-b-lg"
                >
                  Export as JSON
                </button>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Period Selector (for analytics tab) */}
      {activeTab === 'analytics' && (
        <div className="flex flex-col gap-4 md:flex-row md:items-end">
          <div className="flex-1">
            <label htmlFor="period-select" className="block text-sm font-medium text-gray-700 mb-2">
              Analytics Period
            </label>
            <select
              id="period-select"
              value={period}
              onChange={handlePeriodChange}
              className="block w-full rounded-lg border border-gray-300 bg-white px-3 py-2 text-sm font-medium text-gray-900 hover:border-gray-400 focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-500/20"
            >
              <option value="7days">Last 7 Days</option>
              <option value="30days">Last 30 Days</option>
              <option value="quarter">This Quarter</option>
            </select>
          </div>

          {/* Optional: Date Range Picker */}
          <div className="flex gap-2 md:flex-1">
            <input
              type="date"
              value={startDate}
              onChange={(e) => setStartDate(e.target.value)}
              className="flex-1 rounded-lg border border-gray-300 px-3 py-2 text-sm text-gray-900 focus:border-blue-500 focus:outline-none"
              aria-label="Start date"
              title="Select start date"
            />
            <input
              type="date"
              value={endDate}
              onChange={(e) => setEndDate(e.target.value)}
              className="flex-1 rounded-lg border border-gray-300 px-3 py-2 text-sm text-gray-900 focus:border-blue-500 focus:outline-none"
              aria-label="End date"
              title="Select end date"
            />
          </div>
        </div>
      )}

      {/* Tabs Navigation */}
      <div className="border-b border-gray-200" role="tablist">
        <nav className="-mb-px flex gap-8" aria-label="Dashboard sections">
          {tabs.map((tab) => (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id)}
              role="tab"
              aria-selected={activeTab === tab.id}
              aria-controls={`${tab.id}-panel`}
              className={`inline-flex items-center gap-2 border-b-2 px-1 py-4 text-sm font-medium transition-colors ${
                activeTab === tab.id
                  ? 'border-blue-500 text-blue-600'
                  : 'border-transparent text-gray-600 hover:border-gray-300 hover:text-gray-900'
              }`}
            >
              {tab.icon}
              {tab.label}
            </button>
          ))}
        </nav>
      </div>

      {/* Tab Content */}
      <div
        id={`${activeTab}-panel`}
        role="tabpanel"
        aria-labelledby={activeTab}
        className="rounded-lg bg-gray-50 p-6"
      >
        {tabContent}
      </div>
    </div>
  );
};

export default AdminDashboard;
