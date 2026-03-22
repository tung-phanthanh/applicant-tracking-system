import { render, screen, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import AdminDashboardPage from '../../pages/admin/AdminDashboardPage';
import { dashboardService } from '../../services/api/dashboard.service';

vi.mock('../../services/api/dashboard.service', () => ({
  dashboardService: {
    getStats: vi.fn(),
  },
}));

describe('AdminDashboardPage', () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  it('shows loading state initially', () => {
    // Return a promise that doesn't resolve immediately
    vi.mocked(dashboardService.getStats).mockImplementation(() => new Promise(() => {}));
    
    render(<AdminDashboardPage />);
    
    // Check if loading spinner is present (by its class)
    expect(document.querySelector('.animate-spin')).toBeInTheDocument();
  });

  it('renders dashboard stats successfully', async () => {
    const mockStats = {
      activeJobs: 10,
      newCandidates: 25,
      interviewsToday: 5,
      offersSent: 2,
      hiringPipeline: {
        APPLIED: 100,
        INTERVIEW: 50,
        OFFER: 20
      },
      recentApplications: [],
      todaysInterviews: [],
    };
    
    vi.mocked(dashboardService.getStats).mockResolvedValue(mockStats);
    
    render(<AdminDashboardPage />);
    
    await waitFor(() => {
      expect(screen.getByText('Admin System Dashboard')).toBeInTheDocument();
    });

    expect(screen.getByText('10')).toBeInTheDocument();
    expect(screen.getByText('25')).toBeInTheDocument();
    expect(screen.getByText('5')).toBeInTheDocument();
    expect(screen.getByText('2')).toBeInTheDocument();
    
    expect(screen.getByText('APPLIED')).toBeInTheDocument();
    expect(screen.getByText('100')).toBeInTheDocument();
  });

  it('renders error state on fetch failure', async () => {
    vi.mocked(dashboardService.getStats).mockRejectedValue(new Error('Network error'));
    
    render(<AdminDashboardPage />);
    
    await waitFor(() => {
      expect(screen.getByText('Failed to load dashboard data. Please try again.')).toBeInTheDocument();
    });
  });
});
