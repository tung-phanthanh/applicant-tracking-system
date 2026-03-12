import api from '@/lib/axios';
import type { AuditLog } from '@/types';

export const auditLogService = {
    getLogs: async (action?: string): Promise<AuditLog[]> => {
        if (action) {
            return api.get('/audit-logs/action', { params: { action } });
        }
        return api.get('/audit-logs');
    }
};
