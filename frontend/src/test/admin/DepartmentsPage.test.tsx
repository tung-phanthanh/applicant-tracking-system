import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import DepartmentsPage from '../../pages/admin/DepartmentsPage';
import { departmentService } from '../../services/api/department.service';

vi.mock('../../services/api/department.service', () => ({
  departmentService: {
    getAllDepartments: vi.fn(),
    createDepartment: vi.fn(),
    updateDepartment: vi.fn(),
    deleteDepartment: vi.fn(),
    toggleStatus: vi.fn(),
  },
}));

describe('DepartmentsPage', () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  const mockDepts = [
    {
      id: '1',
      name: 'Engineering',
      description: 'Eng dept',
      head: 'Alice',
      employeeCount: 15,
      openPositions: 2,
      status: 'active' as const,
    },
    {
      id: '2',
      name: 'Sales',
      description: 'Sales dept',
      head: 'Bob',
      employeeCount: 5,
      openPositions: 0,
      status: 'inactive' as const,
    },
  ];

  it('renders departments list successfully', async () => {
    vi.mocked(departmentService.getAllDepartments).mockResolvedValue(mockDepts);

    render(<DepartmentsPage />);

    await waitFor(() => {
      expect(screen.getByText('Engineering')).toBeInTheDocument();
      expect(screen.getByText('Sales')).toBeInTheDocument();
      expect(screen.getByText('Alice')).toBeInTheDocument();
    });
  });

  it('allows creating a new department', async () => {
    vi.mocked(departmentService.getAllDepartments).mockResolvedValue([]);
    vi.mocked(departmentService.createDepartment).mockResolvedValue({
      id: '3',
      name: 'Marketing',
      description: 'Marketing dept',
      head: 'Charlie',
      employeeCount: 10,
      openPositions: 1,
      status: 'active',
    });

    const user = userEvent.setup();
    render(<DepartmentsPage />);

    // Click on New Department
    await user.click(await screen.findByText('New Department'));

    const nameInputs = screen.getAllByLabelText(/Name/i);
    await user.type(nameInputs[0], 'Marketing');
    
    // There are actually multiple inputs in our shared form, we lookup by ID if needed or label
    // Wait for form to appear
    await user.type(screen.getByLabelText(/Team Lead/i), 'Charlie');
    await user.type(screen.getByLabelText(/Description/i), 'Marketing dept');
    await user.type(screen.getByLabelText(/Employee Count/i), '10');

    // Submit form
    await user.click(screen.getByText('Save'));

    await waitFor(() => {
      expect(departmentService.createDepartment).toHaveBeenCalledWith({
        name: 'Marketing',
        description: 'Marketing dept',
        head: 'Charlie',
        employeeCount: 10,
      });
      expect(screen.getByText('Marketing')).toBeInTheDocument();
    });
  });

  it('filters departments by search input', async () => {
    vi.mocked(departmentService.getAllDepartments).mockResolvedValue(mockDepts);
    const user = userEvent.setup();
    render(<DepartmentsPage />);

    await waitFor(() => {
      expect(screen.getByText('Engineering')).toBeInTheDocument();
      expect(screen.getByText('Sales')).toBeInTheDocument();
    });

    const searchInput = screen.getByPlaceholderText('Search departments or team leads…');
    await user.type(searchInput, 'engi');

    await waitFor(() => {
      expect(screen.getByText('Engineering')).toBeInTheDocument();
      expect(screen.queryByText('Sales')).not.toBeInTheDocument();
    });
  });
});
