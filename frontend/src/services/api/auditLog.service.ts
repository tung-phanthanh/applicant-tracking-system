import api from '@/lib/api';
import type { AuditLog } from '@/types';

export const auditLogService = {
    getLogs: async (action?: string): Promise<AuditLog[]> => {
        if (action) {
            const { data } = await api.get('/audit-logs/action', { params: { action } });
            return data;
        }
        const { data } = await api.get('/audit-logs');
        return data;
    }
};
