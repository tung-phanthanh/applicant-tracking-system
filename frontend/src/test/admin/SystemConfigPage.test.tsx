import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import SystemConfigPage from '../../pages/admin/SystemConfigPage';
import { systemConfigService } from '../../services/api/systemConfig.service';

vi.mock('../../services/api/systemConfig.service', () => ({
  systemConfigService: {
    getAllConfigs: vi.fn(),
    updateConfigs: vi.fn(),
  },
}));

describe('SystemConfigPage', () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  it('renders default system config successfully if backend returns empty', async () => {
    vi.mocked(systemConfigService.getAllConfigs).mockResolvedValue([]);

    render(<SystemConfigPage />);

    await waitFor(() => {
      expect(screen.getByText('System Configuration')).toBeInTheDocument();
      // Test some default keys
      expect(screen.getByLabelText('Company Name')).toHaveValue('Acme Corp');
      expect(screen.getByLabelText('Max Active Job Postings')).toHaveValue(50);
    });
  });

  it('updates form and saves changes to backend', async () => {
    // Mock the backend returning some overrides
    vi.mocked(systemConfigService.getAllConfigs).mockResolvedValue([
      { configKey: 'company_name', configValue: 'Tech Innovators', description: '', updatedAt: '' }
    ]);
    vi.mocked(systemConfigService.updateConfigs).mockResolvedValue(undefined as void);

    const user = userEvent.setup();
    render(<SystemConfigPage />);

    await waitFor(() => {
      expect(screen.getByLabelText('Company Name')).toHaveValue('Tech Innovators');
    });

    // Update Max Job Postings
    const jobInput = screen.getByLabelText('Max Active Job Postings');
    await user.clear(jobInput);
    await user.type(jobInput, '100');

    // Click Top Save Button (we have two buttons with Save Changes / Save All Settings)
    const saveButtons = screen.getAllByRole('button', { name: /Save /i });
    await user.click(saveButtons[0]);

    await waitFor(() => {
      expect(systemConfigService.updateConfigs).toHaveBeenCalled();
      const payload = vi.mocked(systemConfigService.updateConfigs).mock.calls[0][0];
      
      expect(payload).toHaveProperty('company_name', 'Tech Innovators');
      expect(payload).toHaveProperty('max_job_postings', '100');
    });
  });
});
