import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import AuditLogPage from '../../pages/admin/AuditLogPage';
import { auditLogService } from '../../services/api/auditLog.service';

vi.mock('../../services/api/auditLog.service', () => ({
  auditLogService: {
    getLogs: vi.fn(),
  },
}));

describe('AuditLogPage', () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  const mockLogs = [
    {
      id: '1',
      createdAt: '2023-10-01T12:00:00Z',
      userFullName: 'John Doe',
      userEmail: 'john@example.com',
      action: 'UPDATE',
      entityType: 'User',
      entityId: '123',
      newValue: '{"description": "Updated user profile"}',
      ipAddress: '192.168.1.1',
    },
    {
      id: '2',
      createdAt: '2023-10-02T14:30:00Z',
      userFullName: 'Jane Smith',
      userEmail: 'jane@example.com',
      action: 'CREATE',
      entityType: 'Job',
      entityId: '456',
      newValue: '{"description": "Created new job listing"}',
      ipAddress: '192.168.1.2',
    },
  ];

  it('renders audit logs successfully', async () => {
    vi.mocked(auditLogService.getLogs).mockResolvedValue(mockLogs);

    render(<AuditLogPage />);

    await waitFor(() => {
      expect(screen.getByText('John Doe')).toBeInTheDocument();
      expect(screen.getByText('Jane Smith')).toBeInTheDocument();
    });
  });

  it('filters logs by search query', async () => {
    vi.mocked(auditLogService.getLogs).mockResolvedValue(mockLogs);
    const user = userEvent.setup();
    render(<AuditLogPage />);

    await waitFor(() => {
      expect(screen.getByText('John Doe')).toBeInTheDocument();
    });

    const searchInput = screen.getByPlaceholderText('Search by actor, resource, or detail…');
    await user.type(searchInput, 'Jane');

    await waitFor(() => {
      expect(screen.getByText('Jane Smith')).toBeInTheDocument();
      expect(screen.queryByText('John Doe')).not.toBeInTheDocument();
    });
  });

  it('fetches filtered logs when action is selected', async () => {
    vi.mocked(auditLogService.getLogs).mockResolvedValue(mockLogs);
    const user = userEvent.setup();
    render(<AuditLogPage />);

    await waitFor(() => {
      expect(auditLogService.getLogs).toHaveBeenCalledWith(undefined);
    });

    // Open dropdown
    await user.click(screen.getByText('All Actions'));

    // Select CREATE
    const createOption = screen.getAllByText('CREATE')[0]; // since the badge also has CREATE
    await user.click(createOption);

    await waitFor(() => {
      expect(auditLogService.getLogs).toHaveBeenCalledWith('CREATE');
    });
  });
});
