import api from '@/lib/api';
import type { Notification } from '@/types';

export const notificationService = {
    getNotifications: async (unreadOnly?: boolean): Promise<Notification[]> => {
        const { data } = await api.get('/notifications', { params: { unreadOnly } });
        return data;
    },

    getUnreadCount: async (): Promise<number> => {
        // Assume backend returns `{ count: number }` or simply a number
        const { data } = await api.get('/notifications/unread-count');
        return data;
    },

    markAsRead: async (id: string): Promise<void> => {
        await api.patch(`/notifications/${id}/read`);
    },

    markAllAsRead: async (): Promise<void> => {
        await api.patch('/notifications/read-all');
    }
};

export const adminNotificationService = {
    sendBulkNotification: async (payload: { title: string; message: string; roleIds?: string[]; userIds?: string[] }) => {
        const { data } = await api.post('/notifications/admin/send', payload);
        return data;
    },
    getHistory: async (page = 0, size = 20): Promise<any[]> => {
        const { data } = await api.get('/notifications/admin/history', { params: { page, size } });
        return data;
    },
    getStats: async (): Promise<{ totalSent: number; delivered: number; failed: number; pending: number }> => {
        const { data } = await api.get('/notifications/admin/stats');
        return data;
    }
};
