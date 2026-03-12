import api from '@/lib/axios';
import type { Notification } from '@/types';

export const notificationService = {
    getNotifications: async (unreadOnly?: boolean): Promise<Notification[]> => {
        return api.get('/notifications', { params: { unreadOnly } });
    },

    getUnreadCount: async (): Promise<number> => {
        // Assume backend returns `{ count: number }` or simply a number
        return api.get('/notifications/unread-count');
    },

    markAsRead: async (id: string): Promise<void> => {
        return api.patch(`/notifications/${id}/read`);
    },

    markAllAsRead: async (): Promise<void> => {
        return api.patch('/notifications/read-all');
    }
};
