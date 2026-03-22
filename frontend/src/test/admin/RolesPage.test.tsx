import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import RolesPage from '../../pages/admin/RolesPage';
import { roleService } from '../../services/api/role.service';

vi.mock('../../services/api/role.service', () => ({
  roleService: {
    getAllRoles: vi.fn(),
    getAllPermissions: vi.fn(),
    createRole: vi.fn(),
    updateRole: vi.fn(),
    deleteRole: vi.fn(),
  },
}));

describe('RolesPage', () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  it('renders roles list successfully', async () => {
    const mockRoles = [
      {
        id: '1',
        name: 'Admin',
        description: 'System Administrator',
        isSystem: true,
        userCount: 5,
        permissions: ['view_jobs', 'edit_jobs'],
        color: 'bg-red-500',
        createdAt: '2023-12-01T00:00:00Z',
      },
    ];

    vi.mocked(roleService.getAllRoles).mockResolvedValue(mockRoles);
    vi.mocked(roleService.getAllPermissions).mockResolvedValue([]);

    render(<RolesPage />);

    await waitFor(() => {
      expect(screen.getByText('Roles & Permissions')).toBeInTheDocument();
      expect(screen.getByText('Admin')).toBeInTheDocument();
      expect(screen.getByText('System Administrator')).toBeInTheDocument();
      expect(screen.getByText('System')).toBeInTheDocument();
    });
  });

  it('allows creating a new role', async () => {
    vi.mocked(roleService.getAllRoles).mockResolvedValue([]);
    vi.mocked(roleService.getAllPermissions).mockResolvedValue([
      { key: 'view_jobs', name: 'View Jobs', group: 'Jobs' },
      { key: 'view_candidates', name: 'View Candidates', group: 'Candidates' }
    ]);
    vi.mocked(roleService.createRole).mockResolvedValue({
      id: '2',
      name: 'HR Manager',
      description: 'Human Resources',
      isSystem: false,
      userCount: 0,
      permissions: ['view_candidates'],
      color: 'bg-blue-500',
      createdAt: '2023-12-01T00:00:00Z',
    });

    const user = userEvent.setup();
    render(<RolesPage />);

    // Click on New Role button
    await user.click(await screen.findByText('New Role'));

    // Fill the form
    await user.type(screen.getByLabelText('Role Name'), 'HR Manager');
    await user.type(screen.getByLabelText('Description'), 'Human Resources');

    // Submit form
    await user.click(screen.getByText('Create Role'));

    await waitFor(() => {
      expect(roleService.createRole).toHaveBeenCalledWith({
        name: 'HR Manager',
        description: 'Human Resources',
        permissions: [],
      });
      expect(screen.getByText('HR Manager')).toBeInTheDocument();
    });
  });

  it('expands role to show permissions and allows deletion for non-system roles', async () => {
    const mockRoles = [
      {
        id: '2',
        name: 'Custom Role',
        description: 'Custom',
        isSystem: false,
        userCount: 0,
        permissions: [],
        color: 'bg-green-500',
        createdAt: '2023-12-01T00:00:00Z',
      },
    ];

    vi.mocked(roleService.getAllRoles).mockResolvedValue(mockRoles);
    vi.mocked(roleService.getAllPermissions).mockResolvedValue([]);
    vi.mocked(roleService.deleteRole).mockResolvedValue(undefined as void);

    const user = userEvent.setup();
    render(<RolesPage />);

    // Role is expanded by default since it's the first one
    await waitFor(() => {
      // Find the Delete Role button
      expect(screen.getByText('Delete Role')).toBeInTheDocument();
    });

    // Click delete
    await user.click(screen.getByText('Delete Role'));
    
    // Confirm delete
    await user.click(screen.getByText('Confirm'));

    expect(roleService.deleteRole).toHaveBeenCalledWith('2');
  });
});
