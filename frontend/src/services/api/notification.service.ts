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
