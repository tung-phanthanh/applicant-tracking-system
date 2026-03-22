import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import NotificationsPage from '../../pages/admin/NotificationsPage';
import { notificationService } from '../../services/api/notification.service';

vi.mock('../../services/api/notification.service', () => ({
  notificationService: {
    getNotifications: vi.fn(),
    markAsRead: vi.fn(),
    markAllAsRead: vi.fn(),
  },
}));

describe('NotificationsPage', () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  const mockNotifications = [
    {
      id: '1',
      title: 'New Candidate Applied',
      message: 'John Doe has applied for Software Engineer',
      type: 'INFO',
      read: false,
      createdAt: '2023-10-01T10:00:00Z',
    },
    {
      id: '2',
      title: 'System Update',
      message: 'System will be down for maintenance',
      type: 'WARNING',
      read: true,
      createdAt: '2023-10-02T10:00:00Z',
    },
  ];

  it('renders notifications successfully', async () => {
    vi.mocked(notificationService.getNotifications).mockResolvedValue(mockNotifications);

    render(<NotificationsPage />);

    await waitFor(() => {
      expect(screen.getByText('New Candidate Applied')).toBeInTheDocument();
      expect(screen.getByText('System Update')).toBeInTheDocument();
      expect(screen.getByText('John Doe has applied for Software Engineer')).toBeInTheDocument();
    });
  });

  it('marks a single notification as read', async () => {
    vi.mocked(notificationService.getNotifications).mockResolvedValue(mockNotifications);
    vi.mocked(notificationService.markAsRead).mockResolvedValue(undefined as void);

    const user = userEvent.setup();
    render(<NotificationsPage />);

    // Wait for the unread notification mark as read button
    await waitFor(() => {
      expect(screen.getByTitle('Mark as read')).toBeInTheDocument();
    });

    await user.click(screen.getByTitle('Mark as read'));

    await waitFor(() => {
      expect(notificationService.markAsRead).toHaveBeenCalledWith('1');
    });
  });

  it('marks all notifications as read', async () => {
    vi.mocked(notificationService.getNotifications).mockResolvedValue(mockNotifications);
    vi.mocked(notificationService.markAllAsRead).mockResolvedValue(undefined as void);

    const user = userEvent.setup();
    render(<NotificationsPage />);

    await waitFor(() => {
      expect(screen.getByText('Mark all as read')).toBeInTheDocument();
    });

    await user.click(screen.getByText('Mark all as read'));

    await waitFor(() => {
      expect(notificationService.markAllAsRead).toHaveBeenCalled();
    });
  });
});
